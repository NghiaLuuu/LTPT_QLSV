package iuh.fit.se;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.model.Lecturer;

public class Test {
    public static void main(String[] args) {
        try {
            // ID giảng viên muốn lấy
            Long lecturerId = 1L;

            // Gọi API (giả sử ApiClient.get trả về JSON)
            String json = ApiClient.get("/lecturers/" + lecturerId);

            // Chuyển JSON sang object Lecturer
            ObjectMapper mapper = new ObjectMapper();
            Lecturer lecturer = mapper.readValue(json, Lecturer.class);

            // In thông tin giảng viên
            System.out.println("Thông tin giảng viên:");
            System.out.println(lecturer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
