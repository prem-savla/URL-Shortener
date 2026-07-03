package com.urlshortener.util;

import org.sqids.Sqids;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SqidsCodec {

    private static final String ALPHABET =
            "qP7Lm2XkAa8RjD0tWnY5BvC3eFgHhJiKlMoNpQrSsTuVwUxIyZz1496bcdefGOE";

    private static final int MIN_LENGTH = 5;

    private final Sqids sqids;

    public SqidsCodec() {
        this.sqids = Sqids.builder()
                .alphabet(ALPHABET)
                .minLength(MIN_LENGTH)
                .build();
    }

    public String encode(long id) {
        return sqids.encode(List.of(id));
    }

    public long decode(String shortCode) {
        List<Long> ids = sqids.decode(shortCode);

        if (ids.size() != 1) {
            throw new IllegalArgumentException("Invalid short code");
        }

        return ids.get(0);
    }
}