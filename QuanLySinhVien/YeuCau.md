

## Mô tả Dự án Chi tiết (Nâng cao): Quản lý Sinh viên

**(JPA + Socket + SQL Server + Thread Pool)**

### 1\. Tổng quan & Cấu trúc Dự án

Xây dựng hệ thống quản lý sinh viên theo mô hình Client-Server, áp dụng các kỹ thuật lập trình Java nâng cao.

* **Server:** Ứng dụng Java đa luồng (sử dụng **Thread Pool**) làm nhiệm vụ xử lý nghiệp vụ. Tương tác với CSDL **SQL Server** thông qua **JPA (Hibernate)**. Giao tiếp với Client qua **Socket**.
* **Client:** Ứng dụng Java (Console) cung cấp giao diện người dùng, gửi yêu cầu (Request) và nhận phản hồi (Response) từ Server qua Socket.
* **Common Library:** Một module thư viện chung (`.jar`) chứa các lớp Entity, Enum, và đối tượng Giao thức (Protocol) mà cả Client và Server đều sử dụng.

**Cấu trúc dự án (Khuyến nghị Maven Multi-Module):**

```
student-management/
├── pom.xml           (Parent POM)
├── common-lib/       (Module Chung)
│   ├── src/main/java/com/example/common/model/
│   │   ├── SinhVien.java
│   │   ├── Gender.java
│   │   └── ...
│   └── src/main/java/com/example/common/protocol/
│       ├── Request.java
│       ├── Response.java
│       ├── Command.java
│       └── Status.java
├── server-app/       (Module Server)
│   ├── src/main/java/com/example/server/
│   │   ├── Server.java           (Main class, Thread Pool)
│   │   ├── ClientHandler.java    (Runnable xử lý client)
│   │   └── service/StudentService.java
│   └── src/main/resources/
│       ├── META-INF/persistence.xml (Cấu hình JPA cho SQL Server)
│       └── logback.xml              (Cấu hình Logging)
└── client-app/       (Module Client)
    ├── src/main/java/com/example/client/
    │   ├── Client.java           (Main class, UI Console)
    │   └── net/NetworkClient.java
    └── src/main/resources/
        └── logback.xml
```

-----

### 2\. Module `common-lib` (Thư viện Chung)

Module này định nghĩa "hợp đồng" (contract) dữ liệu. Mọi lớp trong đây phải `implements Serializable`.

#### 2.1. Enum `Gender.java`

Quản lý giới tính một cách tường minh.

```java
// package com.example.common.model;
import java.io.Serializable;
public enum Gender implements Serializable {
    MALE, FEMALE, OTHER
}
```

#### 2.2. Entity `SinhVien.java` (với Validation)

Đối tượng dữ liệu chính, được ánh xạ JPA và tích hợp **Bean Validation API**.

```java
// package com.example.common.model;

import javax.persistence.*;
import javax.validation.constraints.*; // Thêm thư viện validation (ví dụ: hibernate-validator)
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "sinh_vien")
public class SinhVien implements Serializable {
    private static final long serialVersionUID = 1L; 

    @Id
    @Column(name = "ma_sv", length = 20)
    @NotBlank(message = "Mã SV không được để trống")
    @Size(min = 5, max = 20, message = "Mã SV phải từ 5 đến 20 ký tự")
    private String maSV;

    @Column(name = "ho_ten", nullable = false, length = 100)
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String hoTen;

    @Column(name = "ngay_sinh")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;
    
    @Column(name = "gioi_tinh", length = 10)
    @Enumerated(EnumType.STRING) // Lưu trữ tên Enum (MALE, FEMALE) thay vì số
    @NotNull(message = "Giới tính không được để trống")
    private Gender gioiTinh;

    @Column(name = "chuyen_nganh", length = 50)
    private String chuyenNganh;

    @Column(name = "lop", length = 20)
    private String lop;

    @Column(name = "diem_tb")
    @Min(value = 0, message = "Điểm phải >= 0")
    @Max(value = 10, message = "Điểm phải <= 10")
    private double diemTB;

    // Constructors (default và full), Getters, Setters, toString()
    // ...
}
```

#### 2.3. Giao thức (Protocol Objects)

Các đối tượng dùng để đóng gói và gửi qua `ObjectInputStream` / `ObjectOutputStream`.

* `Command.java` (Enum): `ADD_STUDENT`, `UPDATE_STUDENT`, `DELETE_STUDENT`, `FIND_STUDENT_BY_ID`, `GET_ALL_STUDENTS`.
* `Status.java` (Enum): `SUCCESS`, `ERROR`, `NOT_FOUND`, `VALIDATION_ERROR`.
* `Request.java`: Chứa `Command command` và `Object data`.
* `Response.java`: Chứa `Status status`, `String message`, và `Object data`.

-----

### 3\. Module `server-app` (Ứng dụng Server)

#### 3.1. Cấu hình JPA (`persistence.xml`) - Dùng **SQL Server**

Đặt tại `server-app/src/main/resources/META-INF/persistence.xml`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">

    <persistence-unit name="StudentPU_SQLServer" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>com.example.common.model.SinhVien</class>
        <class>com.example.common.model.Gender</class>

        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.microsoft.sqlserver.jdbc.SQLServerDriver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:sqlserver://localhost:1433;databaseName=db_quanlysinhvien;encrypt=true;trustServerCertificate=true;" />
            <property name="javax.persistence.jdbc.user" value="sa" />
            <property name="javax.persistence.jdbc.password" value="YourStrong@Password" />

            <property name="hibernate.dialect" value="org.hibernate.dialect.SQLServer2012Dialect" />

            <property name="hibernate.hbm2ddl.auto" value="update" />
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
        </properties>
    </persistence-unit>
</persistence>
```

#### 3.2. Cấu hình Logging (SLF4J + Logback)

Thêm file `logback.xml` vào `src/main/resources` để quản lý log thay vì `System.out.println`.

#### 3.3. Lớp Dịch vụ `StudentService.java`

Quản lý logic nghiệp vụ và giao dịch CSDL. **Thread-safe** vì mỗi phương thức tạo một `EntityManager` riêng.

```java
// package com.example.server.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//... (các import khác)

public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private EntityManagerFactory emf;

    public StudentService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // CREATE (Tích hợp Validation)
    public boolean addStudent(SinhVien sv) {
        // (Thêm logic kiểm tra validation ở đây nếu cần, 
        //  mặc dù DB cũng sẽ báo lỗi nếu vi phạm constraint)
        if (findStudentById(sv.getMaSV()) != null) {
            logger.warn("Thêm thất bại: Trùng mã SV {}", sv.getMaSV());
            return false;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(sv);
            tx.commit();
            logger.info("Đã thêm SV: {}", sv.getMaSV());
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Lỗi khi thêm SV: {}", e.getMessage(), e);
            return false;
        } finally {
            em.close();
        }
    }

    // ... (Các hàm findById, getAll, update, delete tương tự,
    //      thay e.printStackTrace() bằng logger.error() hoặc logger.warn())
}
```

#### 3.4. Lớp Xử lý `ClientHandler.java` (Sử dụng `Runnable`)

Xử lý logic giao tiếp cho *một* client, được thiết kế để chạy trong Thread Pool.

```java
// package com.example.server.net;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//...

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private Socket clientSocket;
    private StudentService studentService;
    private String clientAddress;

    public ClientHandler(Socket socket, StudentService service) {
        this.clientSocket = socket;
        this.studentService = service;
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        logger.info("Đang xử lý client: {}", clientAddress);
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()))
        {
            Object input;
            while ((input = in.readObject()) != null) {
                if (input instanceof Request) {
                    Request req = (Request) input;
                    logger.debug("Nhận [{}]: {}", clientAddress, req.getCommand());

                    Response res = handleRequest(req); // Xử lý nghiệp vụ

                    out.writeObject(res);
                    out.flush();
                    logger.debug("Gửi [{}]: {}", clientAddress, res.getStatus());
                }
            }
        } catch (Exception e) {
            logger.warn("Client {} đã ngắt kết nối (hoặc lỗi): {}", clientAddress, e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) { /* Bỏ qua */ }
            logger.info("Đã đóng kết nối cho client: {}", clientAddress);
        }
    }

    // Hàm handleRequest(Request req) ... (Logic switch-case như cũ)
}
```

#### 3.5. Lớp `Server.java` (Sử dụng `ExecutorService` & `ShutdownHook`)

Lớp `main` khởi động Server, quản lý Thread Pool và đóng tài nguyên an toàn.

```java
// package com.example.server;

import com.example.server.net.ClientHandler;
import com.example.server.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10; // Quản lý tối đa 10 client cùng lúc
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static EntityManagerFactory emf;
    private static ExecutorService executorService;

    public static void main(String[] args) {
        logger.info("Đang khởi tạo Server...");

        // 1. Khởi tạo EntityManagerFactory
        try {
            emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
        } catch (Exception e) {
            logger.error("Không thể khởi tạo EntityManagerFactory! Thoát.", e);
            return;
        }

        // 2. Khởi tạo Service
        StudentService studentService = new StudentService(emf);

        // 3. Khởi tạo Thread Pool
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 4. Đăng ký Shutdown Hook (chạy khi Ctrl+C hoặc tắt ứng dụng)
        registerShutdownHook();

        logger.info("Server đang khởi động trên cổng {}", PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server đã sẵn sàng. Đang chờ Client...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Chấp nhận kết nối
                logger.info("Client mới đã kết nối: {}", clientSocket.getInetAddress());

                // 5. Giao nhiệm vụ xử lý client cho Thread Pool
                executorService.submit(new ClientHandler(clientSocket, studentService));
            }

        } catch (Exception e) {
            logger.error("Lỗi ServerSocket: {}", e.getMessage(), e);
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Server đang tắt...");

            // 1. Ngừng nhận request mới và chờ các task hoàn thành
            executorService.shutdown();
            // (Có thể thêm logic chờ executorService.awaitTermination() nếu cần)

            // 2. Đóng EntityManagerFactory
            if (emf != null && emf.isOpen()) {
                emf.close();
                logger.info("Đã đóng EntityManagerFactory.");
            }
            logger.info("Server đã tắt.");
        }));
    }
}
```

-----

### 4\. Module `client-app` (Ứng dụng Client)

#### 4.1. Lớp `NetworkClient.java`

Đóng gói logic kết nối và giao tiếp Socket.

```java
// package com.example.client.net;
import org.slf4j.Logger;
// ... (tương tự như mô tả trước)

public class NetworkClient {
    // ...
    // Thêm Logger
    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);

    public Response sendRequest(Request request) {
        try {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        } catch (Exception e) { // Cụ thể là SocketException hoặc EOFException
            logger.error("Mất kết nối đến server: {}", e.getMessage());
            // Trả về một Response lỗi để Client UI biết và xử lý
            return new Response(Status.ERROR, "Mất kết nối đến server. Vui lòng thử lại.", null);
        }
    }
    // ... (connect, disconnect)
}
```

#### 4.2. Lớp `Client.java` (UI Console với **Input Validation**)

Giao diện chính, hoàn thiện logic CRUD và xử lý lỗi nhập liệu.

```java
// package com.example.client;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
// ... (import các lớp protocol và model)

public class Client {
    // ... (SERVER_HOST, SERVER_PORT, networkClient)
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // ... (Logic kết nối ban đầu)
        // ... (Vòng lặp menu chính)
    }

    // ... (showMenu)

    // --- Helper functions cho Input Validation ---

    private static String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.println("Không được để trống. Vui lòng nhập lại.");
            System.out.print(prompt);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double val = Double.parseDouble(scanner.nextLine());
                if (val < min || val > max) {
                    System.out.printf("Giá trị phải nằm trong khoảng [%.1f, %.1f].\n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Nhập sai định dạng số. Vui lòng nhập lại.");
            }
        }
    }

    private static LocalDate readLocalDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine()); // Format: YYYY-MM-DD
            } catch (DateTimeParseException e) {
                System.out.println("Nhập sai định dạng ngày (YYYY-MM-DD). Vui lòng nhập lại.");
            }
        }
    }

    private static Gender readGender(String prompt) {
        while (true) {
            System.out.print(prompt + " (MALE, FEMALE, OTHER): ");
            try {
                return Gender.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Giá trị không hợp lệ. Vui lòng chọn MALE, FEMALE, hoặc OTHER.");
            }
        }
    }

    // --- Các hàm chức năng CRUD ---

    private static void themSinhVien() {
        String maSV = readString("Nhập mã SV (vd: SV001): ");
        String hoTen = readString("Nhập họ tên: ");
        LocalDate ngaySinh = readLocalDate("Nhập ngày sinh (YYYY-MM-DD): ");
        Gender gioiTinh = readGender("Nhập giới tính");
        String chuyenNganh = readString("Nhập chuyên ngành: ");
        String lop = readString("Nhập lớp: ");
        double diemTB = readDouble("Nhập điểm TB (0-10): ", 0.0, 10.0);

        SinhVien sv = new SinhVien(maSV, hoTen, ngaySinh, gioiTinh, chuyenNganh, lop, diemTB);

        Request req = new Request(Command.ADD_STUDENT, sv);
        Response res = networkClient.sendRequest(req);
        System.out.println("Server: " + res.getMessage());
    }

    private static void capNhatSinhVien() {
        // 1. Tìm SV trước
        String maSV = readString("Nhập mã SV cần cập nhật: ");
        Request findReq = new Request(Command.FIND_STUDENT_BY_ID, maSV);
        Response findRes = networkClient.sendRequest(findReq);

        if (findRes.getStatus() != Status.SUCCESS) {
            System.out.println("Server: " + findRes.getMessage());
            return;
        }

        SinhVien sv = (SinhVien) findRes.getData();
        System.out.println("Tìm thấy sinh viên. Thông tin hiện tại:");
        System.out.println(sv);
        System.out.println("(Nhấn Enter để bỏ qua, không thay đổi thông tin)");

        // 2. Cho phép cập nhật từng trường
        System.out.print("Nhập họ tên mới: ");
        String hoTen = scanner.nextLine();
        if (!hoTen.isBlank()) sv.setHoTen(hoTen);

        // (Tương tự cho các trường khác: ngaySinh, gioiTinh, chuyenNganh, lop, diemTB)
        // Ví dụ cho điểm TB:
        System.out.print("Nhập điểm TB mới: ");
        String diemTBStr = scanner.nextLine();
        if (!diemTBStr.isBlank()) {
            try {
                sv.setDiemTB(Double.parseDouble(diemTBStr)); // Cần validation
            } catch (NumberFormatException e) {
                System.out.println("Bỏ qua cập nhật điểm vì nhập sai định dạng.");
            }
        }

        // 3. Gửi request UPDATE
        Request updateReq = new Request(Command.UPDATE_STUDENT, sv);
        Response updateRes = networkClient.sendRequest(updateReq);
        System.out.println("Server: " + updateRes.getMessage());
    }

    // ... (Các hàm timSinhVien, xoaSinhVien, xemTatCaSinhVien như cũ)
}
```

-----

### 5\. Yêu cầu Mở rộng & Tối ưu (Nâng cao)

Bản mô tả này đã tích hợp các yêu cầu cơ bản, dưới đây là các điểm nâng cao cần lưu ý khi triển khai:

1.  **Thread-safe Service:** Thiết kế `StudentService` hiện tại là thread-safe vì mỗi request (trong một `ClientHandler`) đều tạo ra một `EntityManager` mới. Đây là cách làm chuẩn trong JPA. *Không* nên dùng `EntityManager` như một biến instance chung.
2.  **Sử dụng DTO (Data Transfer Object):** Thay vì gửi trực tiếp Entity `SinhVien` qua mạng, nên tạo một lớp `SinhVienDTO` (cũng `Serializable`) trong `common-lib`. Server sẽ chuyển đổi `SinhVien` (Entity) sang `SinhVienDTO` trước khi gửi, và Client chỉ làm việc với DTO. Điều này giúp tách biệt lớp CSDL khỏi lớp giao tiếp mạng.
3.  **Generic Service/DAO:** Nếu dự án mở rộng (quản lý `LopHoc`, `MonHoc`), nên xây dựng một lớp `GenericDAO<T, ID>` để tái sử dụng logic `persist`, `merge`, `find`, `remove`.
4.  **Logging Request/Response:** Sử dụng SLF4J (như đã tích hợp) để log chi tiết nội dung `Request` (command) và `Response` (status) giúp gỡ lỗi (debug) cực kỳ hiệu quả.
5.  **Protocol Versioning:** Nếu giao thức (các `Command`) thay đổi, Client cũ sẽ không tương thích. Có thể thêm một trường `int protocolVersion` vào `Request` để Server kiểm tra và từ chối nếu phiên bản quá cũ.