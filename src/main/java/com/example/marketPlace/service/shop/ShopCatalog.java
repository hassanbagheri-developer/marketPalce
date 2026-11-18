package com.example.marketPlace.service.shop;

import com.example.marketPlace.dal.enums.TrailLevel;

import java.util.List;
import java.util.Set;

public final class ShopCatalog {

    public static final long FREE_SHIPPING_FROM_TOMAN = 5_000_000L;
    public static final long SHIPPING_COST_TOMAN = 95_000L;
    public static final int LOW_STOCK_AT = 5;

    public static final List<Category> CATEGORIES = List.of(
            new Category("bag", "کوله‌پشتی", "backpack"),
            new Category("wear", "کفش و پوشاک", "footprints"),
            new Category("camp", "چادر و کمپ", "tent"),
            new Category("climb", "صخره و یخ", "mountain"),
            new Category("nav", "نور و ناوبری", "compass"),
            new Category("kitchen", "آب و آشپزی", "flame")
    );

    public static final List<String> ACTIVITIES = List.of(
            "کوهنوردی", "کوهپیمایی", "سنگ‌نوردی", "طبیعت‌گردی", "پیاده‌روی", "کمپینگ"
    );

    public static final List<LevelLabel> LEVELS = List.of(
            new LevelLabel(TrailLevel.EASY, "آسان"),
            new LevelLabel(TrailLevel.MEDIUM, "متوسط"),
            new LevelLabel(TrailLevel.HARD, "سخت")
    );

    private static final Set<String> CATEGORY_IDS = Set.of("bag", "wear", "camp", "climb", "nav", "kitchen");
    private static final Set<String> ACTIVITY_SET = Set.copyOf(ACTIVITIES);

    private ShopCatalog() {
    }

    public static void requireCategory(String category) {
        if (category == null || !CATEGORY_IDS.contains(category)) {
            throw ShopException.badRequest("دسته کالا نامعتبر است");
        }
    }

    public static void requireActivities(List<String> activities) {
        if (activities == null) {
            return;
        }
        for (String activity : activities) {
            if (!ACTIVITY_SET.contains(activity)) {
                throw ShopException.badRequest("فعالیت نامعتبر است: " + activity);
            }
        }
    }

    public static long shippingToman(long itemsTotalToman) {
        if (itemsTotalToman <= 0 || itemsTotalToman >= FREE_SHIPPING_FROM_TOMAN) {
            return 0L;
        }
        return SHIPPING_COST_TOMAN;
    }

    public static int discountPercent(long priceToman, long saleToman) {
        if (saleToman <= 0 || priceToman <= 0 || saleToman >= priceToman) {
            return 0;
        }
        return (int) Math.round((1d - (double) saleToman / priceToman) * 100d);
    }

    public static long finalPriceToman(long priceToman, long saleToman) {
        return saleToman > 0 && saleToman < priceToman ? saleToman : priceToman;
    }

    public record Category(String id, String name, String icon) {
    }

    public record LevelLabel(TrailLevel code, String name) {
    }
}
