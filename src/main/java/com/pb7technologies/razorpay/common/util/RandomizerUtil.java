package com.pb7technologies.razorpay.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomizerUtil {

    //SecureRandom is thread-safe
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String randomBase64(int length) {

        byte[] buf = new byte[length];
        SECURE_RANDOM.nextBytes(buf); // bytes array with random numbers
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
