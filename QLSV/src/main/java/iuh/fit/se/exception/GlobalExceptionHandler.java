package iuh.fit.se.exception;

import iuh.fit.se.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void logError(String errorType, String message, Exception ex) {
        System.err.println("\n╔═══════════════════════════════════════════════════════���════════╗");
        System.err.println("║ ❌ [ERROR] " + errorType);
        System.err.println("║ ⏰ Time: " + LocalDateTime.now().format(formatter));
        System.err.println("║ 📝 Message: " + message);
        if (ex != null) {
            System.err.println("║ 🔍 Exception: " + ex.getClass().getSimpleName());
            System.err.println("║ 📍 Location: " + (ex.getStackTrace().length > 0 ? ex.getStackTrace()[0] : "Unknown"));
        }
        System.err.println("╚═══════════════════════════════════════════════���════════════════╝\n");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logError("RESOURCE_NOT_FOUND", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException ex) {
        logError("CONFLICT", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        logError("BAD_REQUEST", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        logError("BAD_CREDENTIALS", "Tên đăng nhập hoặc mật khẩu không đúng", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Tên đăng nhập hoặc mật khẩu không đúng"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        System.err.println("\n╔═══════════════════════════════════════════════���════════════════╗");
        System.err.println("║ ⚠️  [VALIDATION_ERROR]");
        System.err.println("║ ⏰ Time: " + LocalDateTime.now().format(formatter));
        errors.forEach((field, message) -> {
            System.err.println("║ 📋 Field '" + field + "': " + message);
        });
        System.err.println("╚═══════════════════════���════════════════════════════════════════╝\n");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        logError("INTERNAL_SERVER_ERROR", ex.getMessage(), ex);
        ex.printStackTrace(); // Print full stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Lỗi hệ thống: " + ex.getMessage()));
    }
}
