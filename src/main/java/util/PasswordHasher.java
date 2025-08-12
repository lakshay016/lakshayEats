package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Utility class for hashing passwords using SHA-256. */
public final class PasswordHasher {
    private PasswordHasher() {}

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                String hexPart = Integer.toHexString(0xff & b);
                if (hexPart.length() == 1) {
                    hex.append('0');
                }
                hex.append(hexPart);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
