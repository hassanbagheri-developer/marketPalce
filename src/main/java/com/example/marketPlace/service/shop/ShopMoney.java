package com.example.marketPlace.service.shop;

public final class ShopMoney {

    public static final long RIAL_PER_TOMAN = 10L;

    private ShopMoney() {
    }

    public static long tomanToRial(long toman) {
        return toman * RIAL_PER_TOMAN;
    }

    public static long rialToToman(long rial) {
        return rial / RIAL_PER_TOMAN;
    }

    public static long roundToThousandToman(long toman) {
        return Math.round(toman / 1000.0d) * 1000L;
    }
}
