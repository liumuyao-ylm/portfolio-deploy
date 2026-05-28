package com.pharmacy.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encrypt(String password) {
        return encoder.encode(password);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            return false;
        }
        
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return encoder.matches(rawPassword, encodedPassword);
        } else if (encodedPassword.length() == 32 && encodedPassword.matches("[0-9a-fA-F]+")) {
            String md5Password = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
            return md5Password.equals(encodedPassword);
        } else {
            return rawPassword.equals(encodedPassword);
        }
    }
}
