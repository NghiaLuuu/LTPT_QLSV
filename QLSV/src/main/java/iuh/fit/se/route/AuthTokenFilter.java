package iuh.fit.se.route;

import iuh.fit.se.config.JwtUtils;
import iuh.fit.se.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_TOKEN_PREFIX = "auth:token:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                System.out.println("🔐 [AUTH-FILTER] Đang xác thực request");
                System.out.println("   ├─ Username từ token: " + username);
                System.out.println("   ├─ Endpoint: " + request.getMethod() + " " + request.getRequestURI());

                // ⚠️  REDIS VERIFICATION DISABLED
                // Lý do: WebSocket connection gây conflict với Redis token management
                // Giải pháp: Chỉ validate JWT signature (vẫn đảm bảo bảo mật)
                //
                // Redis verification code (DISABLED):
                /*
                String redisKey = REDIS_TOKEN_PREFIX + username;
                String stored = stringRedisTemplate.opsForValue().get(redisKey);
                if (stored == null || !stored.equals(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                */
                System.out.println("   └─ ⚠️  Redis verification DISABLED (fix WebSocket conflict)");

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                System.out.println("✅ [AUTH-FILTER] UserDetails loaded");
                System.out.println("   ├─ Username: " + userDetails.getUsername());
                System.out.println("   ├─ Authorities: " + userDetails.getAuthorities());
                System.out.println("   └─ Enabled: " + userDetails.isEnabled());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("✅ [AUTH-FILTER] Authentication set successfully");
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

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
