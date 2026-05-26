package com.urlshortener.util;

public class Base62Encoder {

    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int BASE = 62;

    public static String encode(long value) {

        if (value == 0) {
            return String.valueOf(BASE62.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();

        while (value > 0) {
            encoded.append(
                    BASE62.charAt(
                            (int)(value % BASE)
                    )
            );

            value /= BASE;
        }

        return encoded.reverse().toString();
    }

    public static long decode(String shortCode) {

        long value = 0;

        for (char c : shortCode.toCharArray()) {

            int index = BASE62.indexOf(c);

            if (index == -1) {
                throw new IllegalArgumentException(
                        "Invalid Base62 character: " + c
                );
            }

            value = value * BASE + index;
        }

        return value;
    }
}