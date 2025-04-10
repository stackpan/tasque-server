package io.github.stackpan.tasque.util;

import java.security.SecureRandom;

public class StringGenerators {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        var sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            var index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

}
