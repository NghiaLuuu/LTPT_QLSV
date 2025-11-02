package iuh.fit.se.route;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Environment env;

    private static final String REDIS_RATE_PREFIX = "rate:requests:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";
        int maxRequests = Integer.parseInt(env.getProperty("app.rate-limit.max-requests", "3"));
        int windowSeconds = Integer.parseInt(env.getProperty("app.rate-limit.window-seconds", "300"));

        String key = REDIS_RATE_PREFIX + username;
        Long current = stringRedisTemplate.opsForValue().increment(key);
        if (current != null && current == 1L) {
            // set expiry for window (use TimeUnit to be compatible with RedisTemplate API)
            stringRedisTemplate.getConnectionFactory().getConnection().pExpire(key.getBytes(), windowSeconds * 1000L);
        }

        if (current != null && current > maxRequests) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too Many Requests\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
