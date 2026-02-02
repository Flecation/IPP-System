package IPPSystem.OTP;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OtpStore {
    private static final SecureRandom RNG = new SecureRandom();
    private static final Map<String, OtpEntry> STORE = new ConcurrentHashMap<>();

    private static final int TTL_SECONDS = 300; // 5 minutes
    private static final int MAX_ATTEMPTS = 5;

    private OtpStore() {}

    private record OtpEntry(String code, long expiresAtMs, int attempts) {}

    public static String create(String key) {
        String otp = String.valueOf(RNG.nextInt(900_000) + 100_000);
        long expires = System.currentTimeMillis() + TTL_SECONDS * 1000L;
        STORE.put(key, new OtpEntry(otp, expires, 0));
        return otp;
    }

    public static boolean verifyOnce(String key, String entered) {
        OtpEntry e = STORE.get(key);
        if (e == null) return false;

        if (System.currentTimeMillis() > e.expiresAtMs) {
            STORE.remove(key);
            return false;
        }
        if (e.attempts >= MAX_ATTEMPTS) {
            STORE.remove(key);
            return false;
        }

        boolean ok = e.code.equals(entered);
        if (ok) {
            STORE.remove(key); // ✅ one-time
            return true;
        }

        STORE.put(key, new OtpEntry(e.code, e.expiresAtMs, e.attempts + 1));
        return false;
    }

    public static void clear(String key) {
        STORE.remove(key);
    }
}
