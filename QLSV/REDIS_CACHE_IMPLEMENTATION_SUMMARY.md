# Tóm Tắt Triển Khai Redis Cache Cho GET APIs

## Ngày thực hiện: 03/11/2025

## Mục tiêu
Kiểm tra và thêm Redis caching cho tất cả các API GET với console logs rõ ràng để theo dõi:
- **Lần 1**: Call DB và lưu vào Redis
- **Lần 2**: Lấy từ Redis (HIT) thay vì call DB

---

## Các File Đã Được Cập Nhật

### 1. LocalCacheClient.java
**Đường dẫn**: `src/main/java/iuh/fit/se/util/LocalCacheClient.java`

**Thay đổi**:
- ✅ Thêm `StringRedisTemplate` để kết nối Redis
- ✅ Thêm `ObjectMapper` để serialize/deserialize JSON
- ✅ Thêm 3 overload methods cho `getOrLoad`:
  - `getOrLoad(String key, Class<T> clazz, Supplier<T> loader)` - Cho single object
  - `getOrLoad(String key, TypeReference<T> typeRef, Supplier<T> loader)` - Cho List và generic types
  - `getOrLoad(String key, Supplier<T> loader)` - Backward compatible

**Console Logs**:
- ✅ `[CACHE] LOCAL HIT` - Tìm thấy trong local cache
- ✅ `[REDIS-CACHE] HIT` - Tìm thấy trong Redis
- ✅ `[CACHE] MISS` - Không tìm thấy trong cache
- ✅ `[DATABASE]` - Đang load từ database
- ✅ `[REDIS-CACHE] Đã lưu vào Redis` - Lưu thành công vào Redis
- ❌ Các error logs khi có vấn đề với Redis

### 2. StudentServiceImpl.java
**GET APIs đã được cache**:
- ✅ `getStudentById(Long id)` → Key: `student:id:{id}`
- ✅ `getAllStudents()` → Key: `students:all`
- ✅ `getStudentDashboard(String studentCode)` → Key: `student:dashboard:{studentCode}`

**Cache Eviction**:
- Evict khi `updateStudent()`, `deleteStudent()`

### 3. SubjectServiceImpl.java
**GET APIs đã được cache**:
- ✅ `getSubjectById(Long id)` → Key: `subject:id:{id}`
- ✅ `getAllSubjects()` → Key: `subjects:all`

**Cache Eviction**:
- Evict khi `createSubject()`, `updateSubject()`, `deleteSubject()`

### 4. FacultyServiceImpl.java
**GET APIs đã được cache**:
- ✅ `getFacultyById(Long id)` → Key: `faculty:id:{id}`
- ✅ `getAllFaculties()` → Key: `faculties:all`

**Cache Eviction**:
- Evict khi `createFaculty()`, `updateFaculty()`, `deleteFaculty()`

### 5. ClassServiceImpl.java
**GET APIs đã được cache**:
- ✅ `getClassById(Long id)` → Key: `class:id:{id}`
- ✅ `getAllClasses()` → Key: `classes:all`

**Cache Eviction**:
- Evict khi `createClass()`, `updateClass()`, `deleteClass()`

### 6. LecturerServiceImpl.java
**GET APIs đã được cache**:
- ✅ `getLecturerById(Long id)` → Key: `lecturer:id:{id}`
- ✅ `getAllLecturers()` → Key: `lecturers:all`

**Cache Eviction**:
- Evict khi `createLecturer()`, `updateLecturer()`, `deleteLecturer()`

### 7. EnrollmentServiceImpl.java
**GET APIs đã được cache**:
- ✅ `getEnrollmentById(Long id)` → Key: `enrollment:id:{id}`
- ✅ `getAllEnrollments()` → Key: `enrollments:all`
- ✅ `getEnrollmentsByStudentId(Long studentId)` → Key: `enrollments:student:{studentId}`

**Cache Eviction**:
- Evict khi `createEnrollment()`, `updateEnrollment()`, `deleteEnrollment()`
- Cũng evict `student:dashboard:{studentCode}` vì dashboard phụ thuộc vào enrollments

---

## Cách Hoạt Động

### Lần 1: Call API GET (MISS)
```
⚠️  [CACHE] MISS - Không tìm thấy trong cache (local/redis): student:id:1
📊 [DATABASE] Đang load dữ liệu từ database cho key: student:id:1 ...
💾 [CACHE] Đã lưu vào local cache: student:id:1 (Load time: 45ms)
💾 [REDIS-CACHE] Đã lưu vào Redis: student:id:1
```

### Lần 2: Call API GET (HIT từ Local Cache)
```
✅ [CACHE] LOCAL HIT - Lấy dữ liệu từ local cache: student:id:1
```

### Lần 3: Sau khi restart server (HIT từ Redis)
```
✅ [REDIS-CACHE] HIT - Lấy dữ liệu từ Redis: student:id:1
```

### Khi Update/Delete (Eviction)
```
🗑️  [CACHE] Đã xóa local cache: student:id:1
🗑️  [REDIS-CACHE] Đã xóa Redis key: student:id:1
```

---

## Cấu Trúc Cache Keys

| Entity | GET by ID | GET All | GET by Student |
|--------|-----------|---------|----------------|
| Student | `student:id:{id}` | `students:all` | N/A |
| Subject | `subject:id:{id}` | `subjects:all` | N/A |
| Faculty | `faculty:id:{id}` | `faculties:all` | N/A |
| Class | `class:id:{id}` | `classes:all` | N/A |
| Lecturer | `lecturer:id:{id}` | `lecturers:all` | N/A |
| Enrollment | `enrollment:id:{id}` | `enrollments:all` | `enrollments:student:{studentId}` |
| Dashboard | N/A | N/A | `student:dashboard:{studentCode}` |
| User | `user:{username}` | N/A | N/A |

---

## Kiểm Tra

### 1. Khởi động Redis
```bash
docker run -d --name qlsv-redis -p 6379:6379 redis:7
```

### 2. Kiểm tra Redis đang chạy
```bash
docker ps | grep redis
```

### 3. Test API
```bash
# Lần 1: MISS - Load từ DB
GET http://localhost:8080/api/students/1

# Lần 2: HIT - Load từ cache
GET http://localhost:8080/api/students/1
```

### 4. Xem logs trong console
Kiểm tra terminal output để thấy các log:
- `[CACHE] MISS` hoặc `[CACHE] LOCAL HIT` hoặc `[REDIS-CACHE] HIT`
- Load time từ database
- Thông báo lưu vào Redis

---

## Lưu Ý

### Warning trong IDE
Có thể thấy một số warnings như:
- `Method 'clear()' is never used` - Đây là utility method để dùng sau này
- `Unused import` trong ClassServiceImpl - Có thể remove nếu muốn

### IDE Cache
Nếu IDE báo lỗi compile về `TypeReference`, hãy:
1. Rebuild project (Ctrl + F9 trong IntelliJ)
2. Invalidate Caches and Restart
3. Hoặc build bằng Maven: `mvn clean compile`

### Dependencies
Đảm bảo trong `pom.xml` có:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
</dependency>
```

### Configuration
Trong `application.properties`:
```properties
spring.redis.host=localhost
spring.redis.port=6379
```

---

## Kết Luận

✅ **Hoàn thành**: Tất cả GET APIs đã được tích hợp Redis caching với console logs chi tiết

✅ **Console Logs**: Rõ ràng hiển thị HIT/MISS và load time

✅ **Cache Eviction**: Tự động xóa cache khi có update/delete

✅ **Backward Compatible**: Giữ nguyên code cũ của `UserDetailsServiceImpl`

🔥 **Lưu ý**: Cần restart IDE hoặc rebuild project nếu gặp lỗi compile về `TypeReference`

