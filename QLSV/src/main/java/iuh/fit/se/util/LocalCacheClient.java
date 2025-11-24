package iuh.fit.se.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
public class LocalCacheClient {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private void logInitStatus() {
        if (initialized.compareAndSet(false, true)) {
            System.out.println("\n╔═══════════════════════════════���════════════════════════════════╗");
            System.out.println("║ 🔧 [CACHE-INIT] LocalCacheClient đang khởi tạo...            ║");
            System.out.println("╠═══════════════════════════════���════════════════════════════════╣");
            if (stringRedisTemplate != null) {
                System.out.println("║ ✅ StringRedisTemplate: AVAILABLE                             ║");
                try {
                    stringRedisTemplate.opsForValue().set("test:connection", "OK");
                    String result = stringRedisTemplate.opsForValue().get("test:connection");
                    if ("OK".equals(result)) {
                        System.out.println("║ ✅ Redis Connection: WORKING                                  ║");
                        stringRedisTemplate.delete("test:connection");
                    } else {
                        System.out.println("║ ⚠️  Redis Connection: TEST FAILED                             ║");
                    }
                } catch (Exception e) {
                    System.out.println("║ ❌ Redis Connection: ERROR - " + e.getMessage());
                }
            } else {
                System.out.println("║ ❌ StringRedisTemplate: NULL (Redis sẽ không được dùng)      ║");
            }

            if (objectMapper != null) {
                System.out.println("║ ✅ ObjectMapper: AVAILABLE                                    ║");
            } else {
                System.out.println("║ ❌ ObjectMapper: NULL                                         ║");
            }
            System.out.println("╚═══════════════════════════════════════���════════════════════════╝\n");
        }
    }

    /**
     * New signature that accepts the target class for safe (de)serialization with Redis.
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> loader) {
        logInitStatus();

        // 1) Check local JVM cache first
        if (cache.containsKey(key)) {
            System.out.println("✅ [CACHE] LOCAL HIT - Lấy dữ liệu từ local cache: " + key);
            return (T) cache.get(key);
        }

        // 2) Check Redis (if available)
        if (stringRedisTemplate != null) {
            try {
                String json = stringRedisTemplate.opsForValue().get(key);
                if (json != null) {
                    try {
                        T value = objectMapper.readValue(json, clazz);
                        cache.put(key, value); // warm local cache
                        System.out.println("✅ [REDIS-CACHE] HIT - Lấy dữ liệu từ Redis: " + key);
                        return value;
                    } catch (Exception e) {
                        System.err.println("❌ [REDIS-CACHE] Tồn tại key nhưng không thể deserialize: " + key + " - " + e.getMessage());
                        // Fall through to loader
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ [REDIS] Lỗi khi truy vấn Redis cho key=" + key + ": " + e.getMessage());
            }
        }

        // 3) MISS: load from DB
        System.out.println("⚠️  [CACHE] MISS - Không tìm thấy trong cache (local/redis): " + key);
        System.out.println("📊 [DATABASE] Đang load dữ liệu từ database cho key: " + key + " ...");

        long startTime = System.currentTimeMillis();
        T value = loader.get();
        long endTime = System.currentTimeMillis();

        if (value != null) {
            cache.put(key, value);
            System.out.println("💾 [CACHE] Đã lưu vào local cache: " + key + " (Load time: " + (endTime - startTime) + "ms)");

            if (stringRedisTemplate != null) {
                try {
                    String json = objectMapper.writeValueAsString(value);
                    stringRedisTemplate.opsForValue().set(key, json);
                    System.out.println("💾 [REDIS-CACHE] Đã lưu vào Redis: " + key);
                } catch (Exception e) {
                    System.err.println("❌ [REDIS-CACHE] Không thể lưu vào Redis cho key=" + key + ": " + e.getMessage());
                }
            }
        } else {
            System.out.println("❌ [DATABASE] Không tìm thấy dữ liệu cho key: " + key);
        }

        return value;
    }

    /**
     * TypeReference overload to support deserializing generic types (e.g., List<T>) from Redis.
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, TypeReference<T> typeRef, Supplier<T> loader) {
        logInitStatus();

        // local cache
        if (cache.containsKey(key)) {
            System.out.println("✅ [CACHE] LOCAL HIT - Lấy dữ liệu từ local cache: " + key);
            return (T) cache.get(key);
        }

        // redis
        if (stringRedisTemplate != null) {
            try {
                String json = stringRedisTemplate.opsForValue().get(key);
                if (json != null) {
                    try {
                        T value = objectMapper.readValue(json, typeRef);
                        cache.put(key, value);
                        System.out.println("✅ [REDIS-CACHE] HIT - Lấy dữ liệu từ Redis: " + key);
                        return value;
                    } catch (Exception e) {
                        System.err.println("❌ [REDIS-CACHE] Tồn tại key nhưng không thể deserialize (TypeReference): " + key + " - " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ [REDIS] Lỗi khi truy vấn Redis cho key=" + key + ": " + e.getMessage());
            }
        }

        // miss
        System.out.println("⚠️  [CACHE] MISS - Không tìm thấy trong cache (local/redis): " + key);
        System.out.println("📊 [DATABASE] Đang load dữ liệu từ database cho key: " + key + " ...");

        long startTime = System.currentTimeMillis();
        T value = loader.get();
        long endTime = System.currentTimeMillis();

        if (value != null) {
            cache.put(key, value);
            System.out.println("💾 [CACHE] Đã lưu vào local cache: " + key + " (Load time: " + (endTime - startTime) + "ms)");

            if (stringRedisTemplate != null) {
                try {
                    String json = objectMapper.writeValueAsString(value);
                    stringRedisTemplate.opsForValue().set(key, json);
                    System.out.println("💾 [REDIS-CACHE] Đã lưu vào Redis: " + key);
                } catch (Exception e) {
                    System.err.println("❌ [REDIS-CACHE] Không thể lưu vào Redis cho key=" + key + ": " + e.getMessage());
                }
            }
        } else {
            System.out.println("❌ [DATABASE] Không tìm thấy dữ liệu cho key: " + key);
        }

        return value;
    }

    // Backward-compatible simplified getOrLoad (keeps previous behavior but cannot read back from Redis reliably)
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, Supplier<T> loader) {
        logInitStatus();

        if (cache.containsKey(key)) {
            System.out.println("✅ [CACHE] LOCAL HIT - Lấy dữ liệu từ local cache: " + key);
            return (T) cache.get(key);
        }

        System.out.println("⚠️  [CACHE] MISS - Không tìm thấy trong local cache: " + key);
        System.out.println("📊 [DATABASE] Đang load dữ liệu từ database cho key: " + key + " ...");

        T value = loader.get();
        if (value != null) {
            cache.put(key, value);
            System.out.println("💾 [CACHE] Đã lưu vào local cache: " + key);

            if (stringRedisTemplate != null) {
                try {
                    String json = objectMapper.writeValueAsString(value);
                    stringRedisTemplate.opsForValue().set(key, json);
                    System.out.println("💾 [REDIS-CACHE] Đã lưu vào Redis: " + key + " (via fallback)");
                } catch (Exception e) {
                    System.err.println("❌ [REDIS-CACHE] Không thể lưu vào Redis cho key=" + key + ": " + e.getMessage());
                }
            }
        }

        return value;
    }

    public void put(String key, Object value) {
        cache.put(key, value);
        System.out.println("💾 [CACHE] Đã lưu/cập nhật local cache: " + key);
        if (stringRedisTemplate != null) {
            try {
                String json = objectMapper.writeValueAsString(value);
                stringRedisTemplate.opsForValue().set(key, json);
                System.out.println("💾 [REDIS-CACHE] Đã lưu/cập nhật Redis: " + key);
            } catch (Exception e) {
                System.err.println("❌ [REDIS-CACHE] Không thể lưu vào Redis cho key=" + key + ": " + e.getMessage());
            }
        }
    }

    public void evict(String key) {
        cache.remove(key);
        System.out.println("🗑️  [CACHE] Đã xóa local cache: " + key);
        if (stringRedisTemplate != null) {
            try {
                stringRedisTemplate.delete(key);
                System.out.println("🗑️  [REDIS-CACHE] Đã xóa Redis key: " + key);
            } catch (Exception e) {
                System.err.println("❌ [REDIS-CACHE] Không thể xóa Redis key=" + key + ": " + e.getMessage());
            }
        }
    }

    public void clear() {
        int size = cache.size();
        cache.clear();
        System.out.println("🧹 [CACHE] Đã xóa toàn bộ local cache (" + size + " entries)");
        // Note: do NOT attempt to clear all Redis keys here to avoid accidental mass delete in production.
    }

    public int size() {
        return cache.size();
    }
}
