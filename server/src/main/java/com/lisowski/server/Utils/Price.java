package com.lisowski.server.Utils;

public class Price {

    public static float calculatePrice(Long duration, Long userDistance) {
        float price = (float) (userDistance / 1000 * 2 + duration / 60 * 0.2 + 5);
        return (float) (Math.round(price * 100.0) / 100.0);
    }
}
