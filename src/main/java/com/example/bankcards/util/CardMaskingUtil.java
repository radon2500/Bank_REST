package com.example.bankcards.util;

public final class CardMaskingUtil {
    private CardMaskingUtil() {}

    public static String maskFromLast4(String last4) {
        return "**** **** **** " + last4;
    }
}
