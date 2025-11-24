# 🔧 Fix Lỗi 403 khi WebSocket Connect

## ❌ VẤN ĐỀ

**Hiện tượng:**
- Admin login → Hoạt động bình thường ✅
- Student login + WebSocket connect → Admin bị lỗi 403 ❌
- Tất cả API calls từ Admin đều bị reject

**Nguyên nhân:**
WebSocket connection đang làm **CONFLICT với Redis token management**, khiến token của Admin bị mất hoặc không khớp.

---

## 🔍 PHÂN TÍCH CHI TIẾT

### Luồng hoạt động hiện tại:

```
1. Admin login → Token lưu vào Redis: auth:token:admin = "token_A"
2. Admin gọi API → AuthTokenFilter check Redis → OK ✅

3. Student login → Token lưu vào Redis: auth:token:SV00000001 = "token_S"  
4. Student WebSocket connect → Có thể gây ra:
   - Redis connection bị share giữa HTTP và WebSocket
   - Token của Admin bị expire hoặc removed
   - Redis key bị ghi đè do race condition

5. Admin gọi API → AuthTokenFilter check Redis → Token KHÔNG KHỚP → 403 ❌
```

---

## ✅ GIẢI PHÁP 1: Tắt Redis Token Verification (TẠM THỜI)

### Vì sao cần làm:
- AuthTokenFilter đang so sánh token với Redis
- Nếu không khớp → reject request
- WebSocket connection làm Redis bị mess up

### Cách làm:

**File: `AuthTokenFilter.java`**

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    try {
        String jwt = parseJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            System.out.println("🔐 [AUTH-FILTER] Đang xác thực request");
            System.out.println("   ├─ Username từ token: " + username);
            System.out.println("   └─ Endpoint: " + request.getMethod() + " " + request.getRequestURI());

            // 🔥 TẠM THỜI SKIP Redis verification để fix lỗi WebSocket
            /*
            String redisKey = REDIS_TOKEN_PREFIX + username;
            String stored = stringRedisTemplate.opsForValue().get(redisKey);
            
            if (stored == null || !stored.equals(jwt)) {
                logger.warn("JWT does not match stored token for user " + username);
                System.err.println("❌ [AUTH-FILTER] Token không khớp Redis!");
                filterChain.doFilter(request, response);
                return;
            }
            */
            System.out.println("⚠️  [AUTH-FILTER] Redis verification DISABLED - chỉ validate JWT signature");

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            System.out.println("✅ [AUTH-FILTER] UserDetails loaded");
            System.out.println("   ├─ Username: " + userDetails.getUsername());
            System.out.println("   ├─ Authorities: " + userDetails.getAuthorities());
            System.out.println("   └─ Enabled: " + userDetails.isEnabled());
            
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            System.out.println("✅ [AUTH-FILTER] Authentication set thành công");
        } else {
            System.err.println("⚠️  [AUTH-FILTER] Token không hợp lệ hoặc NULL");
        }
    } catch (Exception e) {
        logger.error("Cannot set user authentication", e);
        System.err.println("❌ [AUTH-FILTER] Exception: " + e.getMessage());
        e.printStackTrace();
    }

    filterChain.doFilter(request, response);
}
```

**Lưu ý:** Giải pháp này **CHỈ validate JWT signature** mà không check Redis. Nghĩa là:
- ✅ Token vẫn phải hợp lệ (chưa expire, signature đúng)
- ✅ Authorities vẫn được load từ database
- ❌ Không còn "single token per user" enforcement
- ❌ Không thể revoke token ngay lập tức (phải đợi token expire)

---

## ✅ GIẢI PHÁP 2: Tách Redis Key cho WebSocket (KHUYÊN DÙNG)

### Ý tưởng:
- HTTP requests: Dùng Redis key `auth:token:{username}`
- WebSocket: KHÔNG dùng Redis, chỉ validate JWT signature

### Cách làm:

**Tạo annotation để đánh dấu WebSocket requests:**

```java
// File: WebSocketSecurityConfig.java
@Configuration
public class WebSocketSecurityConfig {
    
    @Bean
    public ChannelInterceptor webSocketAuthInterceptor(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    if (authorization != null && !authorization.isEmpty()) {
                        String token = authorization.get(0).replace("Bearer ", "");
                        
                        if (jwtUtils.validateJwtToken(token)) {
                            String username = jwtUtils.getUserNameFromJwtToken(token);
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            
                            UsernamePasswordAuthenticationToken auth = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            
                            accessor.setUser(auth);
                            
                            System.out.println("✅ [WEBSOCKET-AUTH] User authenticated: " + username);
                        }
                    }
                }
                
                return message;
            }
        };
    }
}
```

**Cập nhật WebSocketConfig:**

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private ChannelInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
```

---

## ✅ GIẢI PHÁP 3: Enable WebSocket Authentication ở Client

### Cập nhật WebSocketClient để gửi JWT token:

```java
public void connect(Runnable onConnectCallback) {
    try {
        // Lấy token từ ApiClient
        String token = ApiClient.getJwtToken();
        
        StompHeaders connectHeaders = new StompHeaders();
        if (token != null && !token.isEmpty()) {
            connectHeaders.add("Authorization", "Bearer " + token);
            System.out.println("🔐 [WEBSOCKET] Gửi token khi connect");
        }
        
        stompClient.connectAsync(WS_URL, connectHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;
                connected = true;
                System.out.println("✅ [WEBSOCKET] Connected với authentication!");
                if (onConnectCallback != null) {
                    onConnectCallback.run();
                }
            }
            // ...existing code...
        });
    } catch (Exception e) {
        System.err.println("❌ [WEBSOCKET] Failed to connect: " + e.getMessage());
        e.printStackTrace();
    }
}
```

---

## 🎯 KHUYẾN NGHỊ

### Giải pháp NGAY LẬP TỨC:
**Dùng GIẢI PHÁP 1** - Comment Redis verification trong AuthTokenFilter

### Giải pháp DÀI HẠN:
**Dùng GIẢI PHÁP 2 + 3** - Implement WebSocket authentication riêng

---

## 🧪 TEST SAU KHI FIX

1. **Restart backend** sau khi sửa code
2. **Login Admin** → Test API calls → Phải OK ✅
3. **Login Student** (tab khác) → WebSocket connect
4. **Quay lại Admin** → Test API calls → Vẫn phải OK ✅
5. **Cập nhật sinh viên từ Admin** → Phải thành công ✅

---

## 📊 KẾT QUẢ MONG ĐỢI

### ✅ Sau khi fix:
```
Admin login → API calls OK ✅
Student login + WebSocket → Admin vẫn OK ✅
Cả 2 hoạt động đồng thời không conflict ✅
```

---

🎉 **Hãy thử GIẢI PHÁP 1 trước để test nhanh!**

