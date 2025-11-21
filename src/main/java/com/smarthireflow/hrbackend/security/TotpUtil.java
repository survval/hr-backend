package com.smarthireflow.hrbackend.security;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Minimal TOTP (RFC 6238) utility for 2FA.
 * Uses HMAC-SHA1, 30 second period, 6 digits.
 */
public final class TotpUtil {

    private static final String HMAC_ALGO = "HmacSHA1";
    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final SecureRandom RNG = new SecureRandom();

    private TotpUtil() {}

    public static String generateBase32Secret(int numBytes) {
        byte[] buffer = new byte[numBytes];
        RNG.nextBytes(buffer);
        return new Base32().encodeToString(buffer).replace("=", "");
    }

    public static boolean verifyCode(String base32Secret, String code) {
        if (base32Secret == null || base32Secret.isBlank() || code == null || code.length() < 6) return false;
        try {
            long currentBucket = System.currentTimeMillis() / 1000L / TIME_STEP_SECONDS;
            // allow small time drift: current, -1, +1 windows
            for (long offset = -1; offset <= 1; offset++) {
                String expected = generateCode(base32Secret, currentBucket + offset);
                if (constantTimeEquals(expected, normalize(code))) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String buildOtpAuthUrl(String issuer, String accountName, String base32Secret) {
        String label = urlEncode(issuer) + ":" + urlEncode(accountName);
        String params = "secret=" + urlEncode(base32Secret)
                + "&issuer=" + urlEncode(issuer)
                + "&digits=" + CODE_DIGITS
                + "&period=" + TIME_STEP_SECONDS
                + "&algorithm=SHA1";
        return "otpauth://totp/" + label + "?" + params;
    }

    private static String generateCode(String base32Secret, long timeBucket) throws Exception {
        Base32 base32 = new Base32();
        byte[] key = base32.decode(base32Secret);
        byte[] data = ByteBuffer.allocate(8).putLong(timeBucket).array();
        Mac mac = Mac.getInstance(HMAC_ALGO);
        mac.init(new SecretKeySpec(key, HMAC_ALGO));
        byte[] hmac = mac.doFinal(data);
        int offset = hmac[hmac.length - 1] & 0xF;
        int binary = ((hmac[offset] & 0x7F) << 24)
                | ((hmac[offset + 1] & 0xFF) << 16)
                | ((hmac[offset + 2] & 0xFF) << 8)
                | (hmac[offset + 3] & 0xFF);
        int otp = binary % (int) Math.pow(10, CODE_DIGITS);
        return String.format("%0" + CODE_DIGITS + "d", otp);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String normalize(String code) {
        return code.trim().replace(" ", "");
    }
}
