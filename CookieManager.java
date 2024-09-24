import java.util.HashMap;
import java.util.Map;

public class CookieManager {
    private Map<String, String> sessionCookies = new HashMap<>();

    public String createSession(String username) {
        String sessionId = generateSessionId();
        sessionCookies.put(sessionId, username);
        return sessionId;
    }

    public boolean validateSession(String sessionId) {
        return sessionCookies.containsKey(sessionId);
    }

    private String generateSessionId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
