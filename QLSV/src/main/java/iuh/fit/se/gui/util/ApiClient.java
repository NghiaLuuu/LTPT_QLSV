package iuh.fit.se.gui.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import iuh.fit.se.dto.response.JwtResponse;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * API Client để gọi REST API Backend
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

    private static String jwtToken = null;
    private static String refreshToken = null; // decrypted refresh token in memory
    private static String currentUsername = null;
    private static String currentRole = null;

    // Preferences node to persist encrypted refresh token (and optionally username)
    private static final Preferences prefs = Preferences.userRoot().node("iuh.fit.se.app");

    // Encryption key for persisting refresh token
    private static final Path KEY_PATH = Paths.get(System.getProperty("user.home"), ".qlsv", "secret.key");
    private static SecretKey secretKey = null;

    static {
        // Try to load/create AES key
        try {
            secretKey = loadOrCreateKey();
        } catch (Exception e) {
            secretKey = null;
            System.err.println("Failed to initialize encryption key for refresh token persistence: " + e.getMessage());
        }

        // Load persisted refresh token and username on startup (if any)
        try {
            String persisted = prefs.get("refreshToken", null);
            if (persisted != null && !persisted.isEmpty()) {
                try {
                    if (secretKey != null) {
                        refreshToken = decrypt(persisted);
                    } else {
                        // fallback: stored in plaintext
                        refreshToken = persisted;
                    }
                } catch (Exception e) {
                    // failed to decrypt -> ignore persisted value
                    refreshToken = null;
                }
            }
            String persistedUser = prefs.get("currentUsername", null);
            if (persistedUser != null && !persistedUser.isEmpty()) {
                currentUsername = persistedUser;
            }

            // If we have a persisted refresh token, try to refresh access token asynchronously
            if (refreshToken != null && !refreshToken.isEmpty()) {
                new Thread(() -> {
                    try {
                        // attempt to obtain a fresh access token; ignore failures
                        refresh();
                    } catch (Exception e) {
                        // notify user that background refresh failed and they need to login
                        showUserNotification("Phiên đăng nhập đã hết hạn hoặc không thể tự động cập nhật. Vui lòng đăng nhập lại.");
                    }
                }, "ApiClient-Refresh-Init").start();
            }
        } catch (SecurityException ignored) {
            // If Preferences not allowed, just ignore and operate in-memory
            System.err.println("Preferences unavailable: refresh token won't be persisted");
        }
    }

    // Show a simple Swing dialog to notify the user (runs on EDT)
    private static void showUserNotification(String message) {
        try {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Phiên đăng nhập", JOptionPane.WARNING_MESSAGE));
        } catch (Exception e) {
            // If Swing not available, fallback to stderr
            System.err.println(message);
        }
    }

    // Getter methods
    public static String getJwtToken() {
        return jwtToken;
    }

    public static String getRefreshToken() { return refreshToken; }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static boolean isLoggedIn() {
        return jwtToken != null && !jwtToken.isEmpty();
    }

    public static void logout() {
        // Attempt to notify backend to revoke tokens if we have an access token
        if (jwtToken != null && !jwtToken.isEmpty()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/auth/logout"))
                        .header("Authorization", "Bearer " + jwtToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
                // ignore non-200 responses but log for debugging
                if (response.statusCode() != 200) {
                    System.err.println("Logout endpoint responded with status " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                System.err.println("Failed to call logout endpoint: " + e.getMessage());
            }
        }

        // Clear local tokens regardless of backend call result
        jwtToken = null;
        refreshToken = null;
        currentUsername = null;
        currentRole = null;

        // Remove persisted refresh token and username
        try {
            prefs.remove("refreshToken");
            prefs.remove("currentUsername");
            prefs.flush();
        } catch (Exception ignored) {}

        // Also try to delete key file? No - keep key for future runs
    }

    public static void clearToken() {
        logout();
    }

    /**
     * Login và lưu JWT token và refresh token (persist encrypted refresh token)
     */
    public static JwtResponse login(String username, String password) throws IOException, InterruptedException {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║ 🔐 [CLIENT-AUTH] Đang gửi yêu cầu đăng nhập");
        System.out.println("║ 👤 Username: " + username);
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JwtResponse jwtResponse = objectMapper.readValue(response.body(), JwtResponse.class);
            jwtToken = jwtResponse.getToken();
            refreshToken = jwtResponse.getRefreshToken();
            currentUsername = jwtResponse.getUsername();
            currentRole = jwtResponse.getRole();

            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║ ✅ [CLIENT-AUTH] Đăng nhập thành công");
            System.out.println("║ 👤 Username: " + currentUsername);
            System.out.println("║ 🎭 Role: " + currentRole);
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

            // persist refresh token and username (encrypt refresh token if key available)
            try {
                if (refreshToken != null) {
                    String toStore = refreshToken;
                    if (secretKey != null) {
                        toStore = encrypt(refreshToken);
                    }
                    prefs.put("refreshToken", toStore);
                }
                if (currentUsername != null) prefs.put("currentUsername", currentUsername);
                prefs.flush();
            } catch (Exception ignored) {}
            return jwtResponse;
        } else {
            // Parse error message from backend
            String errorMessage = "Đăng nhập thất bại";
            try {
                com.fasterxml.jackson.databind.JsonNode errorNode = objectMapper.readTree(response.body());
                if (errorNode.has("message")) {
                    errorMessage = errorNode.get("message").asText();
                } else if (errorNode.has("error")) {
                    errorMessage = errorNode.get("error").asText();
                }
            } catch (Exception e) {
                // If can't parse, use the raw response
                errorMessage = response.body();
            }

            System.err.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.err.println("║ ❌ [CLIENT-AUTH] Đăng nhập thất bại");
            System.err.println("║ 🔢 Status: " + response.statusCode());
            System.err.println("║ 📝 Message: " + errorMessage);
            System.err.println("╚════════════════════════════════════════════════════════════════╝\n");

            throw new IOException(errorMessage);
        }
    }

    /**
     * Call refresh endpoint to obtain new access token (and possibly rotated refresh token)
     */
    public static synchronized JwtResponse refresh() throws IOException, InterruptedException {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IOException("No refresh token available. Please login again.");
        }

        String json = String.format("{\"refreshToken\":\"%s\"}", refreshToken);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/refresh"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JwtResponse jwtResponse = objectMapper.readValue(response.body(), JwtResponse.class);
            // update tokens
            jwtToken = jwtResponse.getToken();
            // backend may or may not rotate refresh token; update if provided
            if (jwtResponse.getRefreshToken() != null && !jwtResponse.getRefreshToken().isEmpty()) {
                refreshToken = jwtResponse.getRefreshToken();
                // persist rotated refresh token (encrypted if possible)
                try {
                    String toStore = refreshToken;
                    if (secretKey != null) {
                        toStore = encrypt(refreshToken);
                    }
                    prefs.put("refreshToken", toStore);
                    prefs.flush();
                } catch (Exception ignored) {}
            }
            currentUsername = jwtResponse.getUsername();
            currentRole = jwtResponse.getRole();
            return jwtResponse;
        } else {
            // Refresh failed -> clear tokens to force re-login
            logout();
            throw new IOException("Refresh failed: " + response.body());
        }
    }

    // ---------------------- Encryption helpers ----------------------
    private static SecretKey loadOrCreateKey() throws Exception {
        if (Files.exists(KEY_PATH)) {
            byte[] encoded = Files.readAllBytes(KEY_PATH);
            String b64 = new String(encoded, StandardCharsets.UTF_8).trim();
            byte[] keyBytes = Base64.getDecoder().decode(b64);
            return new SecretKeySpec(keyBytes, "AES");
        }

        // create parent dir
        Path dir = KEY_PATH.getParent();
        if (dir != null && !Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (Exception ignored) {}
        }

        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        SecretKey k = kg.generateKey();
        byte[] keyBytes = k.getEncoded();
        String b64 = Base64.getEncoder().encodeToString(keyBytes);
        try {
            Files.write(KEY_PATH, b64.getBytes(StandardCharsets.UTF_8));
            // Try to set file permissions to owner-only where supported
            try {
                // On POSIX systems
                Set<java.nio.file.attribute.PosixFilePermission> perms = java.util.EnumSet.of(
                        java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                        java.nio.file.attribute.PosixFilePermission.OWNER_WRITE
                );
                Files.setPosixFilePermissions(KEY_PATH, perms);
            } catch (UnsupportedOperationException ignored) {
                // Windows or unsupported FS: ignore
            } catch (Exception ignored) {
                // Other exceptions: ignore
            }
        } catch (Exception e) {
            // If write fails, still return the generated key (but won't persist)
        }
        return k;
    }

    private static String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[12]; // 96-bit nonce for GCM
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + ct.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ct, 0, combined, iv.length, ct.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    private static String decrypt(String b64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(b64);
        if (combined.length < 12) throw new IllegalArgumentException("Invalid ciphertext");
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, 12);
        byte[] ct = new byte[combined.length - 12];
        System.arraycopy(combined, 12, ct, 0, ct.length);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        byte[] pt = cipher.doFinal(ct);
        return new String(pt, StandardCharsets.UTF_8);
    }

    // ---------------------- End encryption helpers ----------------------

    /**
     * Helper to check HTTP success codes
     */
    private static boolean isSuccess(int status, int... successCodes) {
        if (successCodes == null || successCodes.length == 0) {
            return status == 200;
        }
        for (int c : successCodes) if (status == c) return true;
        return false;
    }

    /**
     * Generic sender that retries once after attempting refresh on 401.
     * The requestSupplier must build a HttpRequest using current jwtToken (so retry will include updated jwtToken).
     */
    private static String sendWithAuth(Supplier<HttpRequest> requestSupplier, int... successCodes) throws IOException, InterruptedException {
        HttpRequest req = requestSupplier.get();

        // Log API request
        System.out.println("🌐 [API-CALL] " + req.method() + " " + req.uri().getPath());

        HttpResponse<String> resp = httpClient.send(req, BodyHandlers.ofString());

        if (isSuccess(resp.statusCode(), successCodes)) {
            System.out.println("✅ [API-SUCCESS] " + req.method() + " " + req.uri().getPath() + " - Status: " + resp.statusCode());
            return resp.body();
        }

        // Handle Unauthorized: try refresh once then retry
        if (resp.statusCode() == 401) {
            System.err.println("\n⚠️  [API-401] Token expired, attempting refresh...");
            try {
                refresh();
                System.out.println("✅ [API-REFRESH] Token refreshed successfully");
            } catch (IOException | InterruptedException e) {
                System.err.println("\n╔════════════════════════════════════════════════════════════════╗");
                System.err.println("║ ❌ [API-REFRESH-FAILED] Không thể refresh token");
                System.err.println("║ 📝 Error: " + e.getMessage());
                System.err.println("╚════════════════════════════════════════════════════════════════╝\n");
                showUserNotification("Phiên đăng nhập không thể tự động gia hạn. Vui lòng đăng nhập lại.");
                throw new IOException("Unauthorized and refresh failed: " + e.getMessage());
            }
            HttpRequest retry = requestSupplier.get();
            HttpResponse<String> resp2 = httpClient.send(retry, BodyHandlers.ofString());
            if (isSuccess(resp2.statusCode(), successCodes)) {
                System.out.println("✅ [API-RETRY-SUCCESS] " + req.method() + " " + req.uri().getPath());
                return resp2.body();
            }
            if (resp2.statusCode() == 401) {
                showUserNotification("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
            }

            System.err.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.err.println("║ ❌ [API-FAILED] Request failed after refresh");
            System.err.println("║ 🔢 Status: " + resp2.statusCode());
            System.err.println("║ 📝 Response: " + resp2.body());
            System.err.println("╚═══════════════════════════════���════════════════════════════════╝\n");

            throw new IOException("Request failed after refresh: " + resp2.body());
        }

        // Handle Too Many Requests (429): exponential backoff + respect Retry-After header
        if (resp.statusCode() == 429) {
            System.err.println("\n⚠️  [API-429] Too many requests, retrying with backoff...");

            // determine retry-after from header if present (seconds)
            int maxRetries = 3;
            long baseDelayMs = 500; // initial backoff
            String ra = resp.headers().firstValue("Retry-After").orElse(null);
            long retryAfterMs = -1;
            if (ra != null) {
                try {
                    retryAfterMs = Long.parseLong(ra) * 1000L;
                } catch (NumberFormatException ignored) {
                    // could be HTTP-date; ignore and use backoff
                }
            }

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                long delay = (retryAfterMs > 0) ? retryAfterMs : (baseDelayMs * (1L << (attempt - 1)));
                System.out.println("⏳ [API-RETRY] Attempt " + attempt + "/" + maxRetries + " after " + delay + "ms delay");

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }

                HttpRequest retryReq = requestSupplier.get();
                HttpResponse<String> retryResp = httpClient.send(retryReq, BodyHandlers.ofString());
                if (isSuccess(retryResp.statusCode(), successCodes)) {
                    System.out.println("✅ [API-RETRY-SUCCESS] Request succeeded after " + attempt + " retries");
                    return retryResp.body();
                }
                if (retryResp.statusCode() != 429) {
                    // if other error returned, stop retrying; and if 401, let the outer logic handle
                    if (retryResp.statusCode() == 401) {
                        // attempt token refresh then retry once
                        try {
                            refresh();
                        } catch (Exception e) {
                            showUserNotification("Phiên đăng nhập không thể tự động gia hạn. Vui lòng đăng nhập lại.");
                            throw new IOException("Unauthorized and refresh failed after 429 retries: " + e.getMessage());
                        }
                        HttpRequest retryAfterRefresh = requestSupplier.get();
                        HttpResponse<String> afterRefreshResp = httpClient.send(retryAfterRefresh, BodyHandlers.ofString());
                        if (isSuccess(afterRefreshResp.statusCode(), successCodes)) return afterRefreshResp.body();
                        throw new IOException("Request failed after refresh: " + afterRefreshResp.body());
                    }
                    throw new IOException("Request failed during 429 retry: " + retryResp.body());
                }
                // else continue retry loop
            }

            // All retries exhausted
            System.err.println("\n╔═══════════════════════════════════════════════════════���════════╗");
            System.err.println("║ ❌ [API-429-EXHAUSTED] Too many requests");
            System.err.println("║ 📝 All retries exhausted");
            System.err.println("╚═══════════════════════════════════════════════���════════════════╝\n");

            showUserNotification("Quá nhiều yêu cầu. Vui lòng thử lại sau vài phút.");
            throw new IOException("Too Many Requests after retries: " + resp.body());
        }

        System.err.println("\n╔═══════════════════════���═══════════════════════════════���════════╗");
        System.err.println("║ ❌ [API-ERROR] Request failed");
        System.err.println("║ 🔢 Status: " + resp.statusCode());
        System.err.println("║ 🌐 URL: " + req.uri());
        System.err.println("║ 📝 Response: " + resp.body());
        System.err.println("╚═══════════════════════════════════════���═══════════════════════���╝\n");

        throw new IOException("Request failed: " + resp.body());
    }

    /**
     * GET request với JWT token (auto-refresh on 401)
     */
    public static String get(String endpoint) throws IOException, InterruptedException {
        Supplier<HttpRequest> supplier = () -> HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + (jwtToken == null ? "" : jwtToken))
                .GET()
                .build();
        return sendWithAuth(supplier, 200);
    }

    /**
     * POST request với JWT token (auto-refresh on 401)
     */
    public static String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        Supplier<HttpRequest> supplier = () -> HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + (jwtToken == null ? "" : jwtToken))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendWithAuth(supplier, 200, 201);
    }

    /**
     * PUT request với JWT token (auto-refresh on 401)
     */
    public static String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
        Supplier<HttpRequest> supplier = () -> HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + (jwtToken == null ? "" : jwtToken))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendWithAuth(supplier, 200);
    }

    /**
     * DELETE request với JWT token (auto-refresh on 401)
     */
    public static String delete(String endpoint) throws IOException, InterruptedException {
        Supplier<HttpRequest> supplier = () -> HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + (jwtToken == null ? "" : jwtToken))
                .DELETE()
                .build();
        return sendWithAuth(supplier, 200);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
