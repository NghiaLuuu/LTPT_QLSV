package iuh.fit.se.service.impl;

import iuh.fit.se.config.JwtUtils;
import iuh.fit.se.dto.request.LoginRequest;
import iuh.fit.se.dto.request.RegisterRequest;
import iuh.fit.se.dto.request.ChangePasswordRequest;
import iuh.fit.se.dto.response.JwtResponse;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ConflictException;
import iuh.fit.se.model.User;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.AuthService;
import iuh.fit.se.util.LocalCacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LocalCacheClient localCacheClient;

    private static final String REDIS_TOKEN_PREFIX = "auth:token:";
    private static final String REDIS_LOGIN_ATTEMPTS_PREFIX = "auth:attempts:";
    private static final String REDIS_ACCOUNT_LOCKED_PREFIX = "auth:locked:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_TIME_SECONDS = 30;

    @Override
    public JwtResponse login(LoginRequest request) {
        String username = request.getUsername();

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║ 🔐 [AUTH] Yêu cầu đăng nhập");
        System.out.println("║ 👤 Username: " + username);
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        // Kiểm tra xem tài khoản có bị khóa không
        String lockKey = REDIS_ACCOUNT_LOCKED_PREFIX + username;
        String lockedUntil = stringRedisTemplate.opsForValue().get(lockKey);
        if (lockedUntil != null) {
            Long ttl = stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            String errorMsg = "Tài khoản đã bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau " + (ttl != null ? ttl : 30) + " giây";

            System.err.println("\n╔═══════════════════════════════════════════════���════════════════╗");
            System.err.println("║ 🔒 [AUTH-BLOCKED] Tài khoản bị khóa");
            System.err.println("║ 👤 Username: " + username);
            System.err.println("║ ⏱️  Còn lại: " + (ttl != null ? ttl : 30) + " giây");
            System.err.println("╚═══════════════════════════════���════════════════════════════════╝\n");

            throw new BadRequestException(errorMsg);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException("User không tồn tại"));

            // Generate refresh token, save to DB
            String refreshToken = UUID.randomUUID().toString();
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            // Store access token in Redis keyed by username
            String redisKey = REDIS_TOKEN_PREFIX + user.getUsername();
            long jwtExpMs = jwtUtils.getJwtExpirationMs();
            stringRedisTemplate.opsForValue().set(redisKey, jwt, jwtExpMs, TimeUnit.MILLISECONDS);

            // Đăng nhập thành công, xóa số lần thử đăng nhập
            String attemptsKey = REDIS_LOGIN_ATTEMPTS_PREFIX + username;
            stringRedisTemplate.delete(attemptsKey);

            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║ ✅ [AUTH-SUCCESS] Đăng nhập thành công");
            System.out.println("║ 👤 Username: " + username);
            System.out.println("║ 🎭 Role: " + user.getRole().name());
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

            return new JwtResponse(jwt, refreshToken, user.getUsername(), user.getRole().name());
        } catch (BadCredentialsException e) {
            // Đăng nhập thất bại, tăng số lần thử
            handleFailedLogin(username);

            System.err.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.err.println("║ ❌ [AUTH-FAILED] Đăng nhập thất bại");
            System.err.println("║ 👤 Username: " + username);
            System.err.println("║ 📝 Lý do: Sai tên đăng nhập hoặc mật khẩu");
            System.err.println("╚════════════════════════════════════════════════════════════════╝\n");

            throw new BadRequestException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    private void handleFailedLogin(String username) {
        String attemptsKey = REDIS_LOGIN_ATTEMPTS_PREFIX + username;
        String lockKey = REDIS_ACCOUNT_LOCKED_PREFIX + username;

        // Lấy số lần thử hiện tại
        String attemptsStr = stringRedisTemplate.opsForValue().get(attemptsKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
        attempts++;

        System.out.println("\n⚠️  [AUTH-ATTEMPT] Số lần đăng nhập sai: " + attempts + "/" + MAX_LOGIN_ATTEMPTS + " (Username: " + username + ")");

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            // Khóa tài khoản
            stringRedisTemplate.opsForValue().set(lockKey, String.valueOf(System.currentTimeMillis()), LOCK_TIME_SECONDS, TimeUnit.SECONDS);
            stringRedisTemplate.delete(attemptsKey);

            System.err.println("\n╔═══════════════════════���════════════════════════════════════════╗");
            System.err.println("║ 🔒 [AUTH-LOCKED] Tài khoản bị khóa tự động");
            System.err.println("║ 👤 Username: " + username);
            System.err.println("║ ⏱️  Thời gian khóa: " + LOCK_TIME_SECONDS + " giây");
            System.err.println("╚═══════════════════════════════════════════════════════════════���╝\n");
        } else {
            // Lưu số lần thử với TTL 30 giây
            stringRedisTemplate.opsForValue().set(attemptsKey, String.valueOf(attempts), LOCK_TIME_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Override
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        userRepository.save(user);

        // Evict local cache for this user if present
        localCacheClient.evict("user:" + user.getUsername());

        return "Đăng ký thành công";
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User không tồn tại"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không đúng");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Evict local cache after update
        localCacheClient.evict("user:" + user.getUsername());
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Refresh token không hợp lệ"));

        // generate new access token
        // create an Authentication-like principal via username
        String newJwt = jwtUtils.generateJwtTokenFromUsername(user.getUsername());

        // update Redis stored token
        String redisKey = REDIS_TOKEN_PREFIX + user.getUsername();
        long jwtExpMs = jwtUtils.getJwtExpirationMs();
        stringRedisTemplate.opsForValue().set(redisKey, newJwt, jwtExpMs, TimeUnit.MILLISECONDS);

        return new JwtResponse(newJwt, refreshToken, user.getUsername(), user.getRole().name());
    }

    @Override
    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User không tồn tại"));

        // Remove refresh token from DB
        user.setRefreshToken(null);
        userRepository.save(user);

        // Remove access token from Redis
        String redisKey = REDIS_TOKEN_PREFIX + user.getUsername();
        try {
            stringRedisTemplate.delete(redisKey);
        } catch (Exception e) {
            // log and continue
            System.err.println("Failed to delete redis key " + redisKey + ": " + e.getMessage());
        }

        // Evict local cache
        try {
            localCacheClient.evict("user:" + user.getUsername());
        } catch (Exception ignored) {}
    }
}
