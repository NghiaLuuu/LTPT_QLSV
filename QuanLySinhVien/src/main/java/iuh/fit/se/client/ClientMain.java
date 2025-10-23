package iuh.fit.se.client;

import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.model.Gender;
import iuh.fit.se.common.model.SinhVienDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class ClientMain {
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8888; // ĐỔI PORT từ 8080 → 8888

        try (NetworkClient client = new NetworkClient(host, port)) {
            // Add a student
            SinhVienDTO sv = new SinhVienDTO("SV001", "Nguyen Van A", LocalDate.of(2000,1,1), Gender.MALE, "CNTT", "KTPM", 8.5);
            Request addReq = new Request(Command.ADD_STUDENT, sv);
            Response addRes = client.sendRequest(addReq);
            logger.info("Add response: {} - {}", addRes.getStatus(), addRes.getMessage());

            // Get all students
            Request allReq = new Request(Command.GET_ALL_STUDENTS, null);
            Response allRes = client.sendRequest(allReq);
            logger.info("Get all response: {} - {}", allRes.getStatus(), allRes.getMessage());
            logger.info("Data: {}", allRes.getData());

            // Find by id
            Request findReq = new Request(Command.FIND_STUDENT_BY_ID, "SV001");
            Response findRes = client.sendRequest(findReq);
            logger.info("Find response: {} - {} - {}", findRes.getStatus(), findRes.getMessage(), findRes.getData());

        } catch (Exception e) {
            logger.error("Client error: {}", e.getMessage(), e);
        }
    }
}
