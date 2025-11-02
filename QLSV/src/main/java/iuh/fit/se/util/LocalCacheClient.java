package iuh.fit.se.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class LocalCacheClient {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, Supplier<T> loader) {
        if (cache.containsKey(key)) {
            System.out.println("✅ [REDIS-CACHE] HIT - Lấy dữ liệu từ cache: " + key);
            return (T) cache.get(key);
        }

        System.out.println("⚠️  [REDIS-CACHE] MISS - Không tìm thấy trong cache: " + key);
        System.out.println("📊 [DATABASE] Đang load dữ liệu từ database...");

        long startTime = System.currentTimeMillis();
        T value = loader.get();
        long endTime = System.currentTimeMillis();

        if (value != null) {
            cache.put(key, value);
            System.out.println("💾 [REDIS-CACHE] Đã lưu vào cache: " + key + " (Load time: " + (endTime - startTime) + "ms)");
        } else {
            System.out.println("❌ [DATABASE] Không tìm thấy dữ liệu cho key: " + key);
        }

        return value;
    }

    public void put(String key, Object value) {
        cache.put(key, value);
        System.out.println("💾 [REDIS-CACHE] Đã lưu/cập nhật cache: " + key);
    }

    public void evict(String key) {
        cache.remove(key);
        System.out.println("🗑️  [REDIS-CACHE] Đã xóa cache: " + key);
    }

    public void clear() {
        int size = cache.size();
        cache.clear();
        System.out.println("🧹 [REDIS-CACHE] Đã xóa toàn bộ cache (" + size + " entries)");
    }

    public int size() {
        return cache.size();
    }
}
