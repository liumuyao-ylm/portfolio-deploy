package com.pharmacy.util;

import com.pharmacy.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class AuthTokenUtil {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_TTL_SECONDS = 8 * 60 * 60;
    private static final String DEFAULT_SECRET = "pharmacy-admin-demo-readonly-secret";

    public String createToken(User user) {
        long expiresAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;
        String role = normalizeRole(user.getRole());
        String payload = user.getUserId() + "|" + safe(user.getUsername()) + "|" + role + "|" + expiresAt;
        String encodedPayload = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + sign(encodedPayload);
    }

    public TokenClaims parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        String rawToken = token.trim();
        if (rawToken.startsWith("Bearer ")) {
            rawToken = rawToken.substring(7).trim();
        }

        String[] parts = rawToken.split("\\.");
        if (parts.length != 2 || !MessageDigest.isEqual(sign(parts[0]).getBytes(StandardCharsets.UTF_8), parts[1].getBytes(StandardCharsets.UTF_8))) {
            return null;
        }

        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String[] values = payload.split("\\|", -1);
            if (values.length != 4) {
                return null;
            }
            long expiresAt = Long.parseLong(values[3]);
            if (expiresAt < Instant.now().getEpochSecond()) {
                return null;
            }
            return new TokenClaims(Integer.parseInt(values[0]), values[1], normalizeRole(values[2]), expiresAt);
        } catch (Exception e) {
            return null;
        }
    }

    public String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return "";
        }
        return role.trim().toUpperCase();
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return base64Url(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign auth token", e);
        }
    }

    private String secret() {
        String envSecret = System.getenv("PHARMACY_AUTH_SECRET");
        return StringUtils.hasText(envSecret) ? envSecret : DEFAULT_SECRET;
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("|", "");
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record TokenClaims(Integer userId, String username, String role, long expiresAt) {
        public boolean isDemo() {
            return "DEMO".equals(role);
        }
    }
}
