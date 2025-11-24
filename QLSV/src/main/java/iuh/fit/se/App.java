package iuh.fit.se;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//
//        // Hỗ trợ LocalDate, LocalDateTime
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        // Giới hạn đọc stream
//        mapper.getFactory().setStreamReadConstraints(
//                StreamReadConstraints.builder()
//                        .maxNestingDepth(Integer.MAX_VALUE) // hoặc 5000
//                        .build()
//        );
//
//        return mapper;
//    }
}
