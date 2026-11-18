package com.example.marketPlace.service.shop;

import com.example.marketPlace.dal.entity.OrderItemEntity;
import com.example.marketPlace.dal.entity.ProductEntity;
import com.example.marketPlace.dal.entity.ProductSpecEntity;
import com.example.marketPlace.dal.entity.ShopOrderEntity;
import com.example.marketPlace.dal.enums.OrderStatus;
import com.example.marketPlace.dal.enums.ProductStatus;
import com.example.marketPlace.dal.enums.TrailLevel;
import com.example.marketPlace.dal.repository.ProductRepository;
import com.example.marketPlace.dal.repository.ShopOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShopSeedRunner implements ApplicationRunner {

    private final ProductRepository products;
    private final ShopOrderRepository orders;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (products.count() > 0) {
            return;
        }
        List<ProductEntity> saved = products.saveAll(sampleProducts());
        Map<String, ProductEntity> bySku = saved.stream()
                .collect(Collectors.toMap(ProductEntity::getSku, Function.identity()));
        orders.saveAll(sampleOrders(bySku));
    }

    private List<ProductEntity> sampleProducts() {
        return List.of(
                product("BAG-045", "کوله‌پشتی کوهنوردی ۴۵ لیتری", "کوه‌سار", "bag", 4_850_000, 4_190_000, 12, ProductStatus.LIVE, true, "backpack", 145, 4.8, 126, 412,
                        List.of("کوهنوردی", "کوهپیمایی", "سنگ‌نوردی"), List.of(TrailLevel.MEDIUM, TrailLevel.HARD),
                        "کوله‌ای برای صعودهای یک تا دو روزه؛ پشتی تنفس‌پذیر، کمربند بارگیر و جای مخصوص کیسه آب.",
                        spec("حجم", "۴۵ لیتر"), spec("وزن", "۱٫۴ کیلوگرم")),
                product("BAG-022", "کوله روزانه ۲۲ لیتری", "دامنه", "bag", 1_950_000, 0, 30, ProductStatus.LIVE, false, "backpack", 210, 4.6, 88, 530,
                        List.of("کوهپیمایی", "طبیعت‌گردی", "پیاده‌روی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM),
                        "سبک و جمع‌وجور برای پیاده‌روی‌های یک‌روزه.",
                        spec("حجم", "۲۲ لیتر"), spec("وزن", "۵۸۰ گرم")),
                product("WER-310", "کفش کوهپیمایی ضدآب", "ردپا", "wear", 6_900_000, 5_990_000, 4, ProductStatus.LIVE, true, "footprints", 30, 4.7, 203, 389,
                        List.of("کوهنوردی", "کوهپیمایی", "طبیعت‌گردی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "ساق‌بلند با رویه ضدآب و زیره آج‌دار.",
                        spec("سایز", "۳۸ تا ۴۶"), spec("رویه", "چرم نبوک + غشای ضدآب")),
                product("WER-120", "کاپشن پر سبک", "سپیدار", "wear", 7_400_000, 0, 8, ProductStatus.LIVE, false, "shirt", 250, 4.9, 64, 140,
                        List.of("کوهنوردی", "کمپینگ"), List.of(TrailLevel.MEDIUM, TrailLevel.HARD),
                        "پر طبیعی ۸۰۰، داخل کیسه خودش جمع می‌شود.",
                        spec("پرکننده", "پر اردک ۸۰۰FP"), spec("وزن", "۳۲۰ گرم")),
                product("CLB-002", "باتوم کوهنوردی (جفت)", "کوه‌سار", "climb", 1_250_000, 0, 25, ProductStatus.LIVE, false, "mountain", 175, 4.5, 151, 610,
                        List.of("کوهنوردی", "کوهپیمایی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "آلومینیومی سه‌تکه با قفل سریع.",
                        spec("طول", "۶۵ تا ۱۳۵ سانتی‌متر"), spec("وزن هر عدد", "۲۴۰ گرم")),
                product("CLB-012", "کرامپون ۱۲ دندانه", "یخ‌شکن", "climb", 3_600_000, 0, 6, ProductStatus.LIVE, false, "snowflake", 220, 4.8, 47, 98,
                        List.of("کوهنوردی", "سنگ‌نوردی"), List.of(TrailLevel.HARD),
                        "برای آبشار یخی و یخچال.",
                        spec("دندانه", "۱۲"), spec("جنس", "فولاد کروم‌مولیبدن")),
                product("CMP-204", "چادر دو نفره چهارفصل", "دامنه", "camp", 9_800_000, 8_900_000, 3, ProductStatus.LIVE, true, "tent", 100, 4.7, 72, 120,
                        List.of("کوهنوردی", "کمپینگ", "طبیعت‌گردی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "دو پوش، مقاوم در برابر باد خط‌الرأس.",
                        spec("ظرفیت", "۲ نفر"), spec("وزن", "۲٫۶ کیلوگرم")),
                product("CMP-310", "کیسه‌خواب پر (۱۰- درجه)", "سپیدار", "camp", 5_200_000, 0, 0, ProductStatus.LIVE, false, "bed", 280, 4.6, 39, 77,
                        List.of("کوهنوردی", "کمپینگ"), List.of(TrailLevel.MEDIUM, TrailLevel.HARD),
                        "برای شب‌مانی در پناهگاه.",
                        spec("دمای راحتی", "۱۰- درجه"), spec("وزن", "۱٫۱ کیلوگرم")),
                product("NAV-400", "هدلامپ شارژی ۴۰۰ لومن", "سپیده", "nav", 980_000, 0, 40, ProductStatus.LIVE, false, "flashlight", 60, 4.7, 310, 1240,
                        ShopCatalog.ACTIVITIES, List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "برای حرکت قبل از طلوع؛ شارژ USB-C.",
                        spec("نور", "۴۰۰ لومن"), spec("ضدآب", "IPX4")),
                product("NAV-010", "قطب‌نمای نقشه‌خوان", "راهی", "nav", 450_000, 0, 18, ProductStatus.LIVE, false, "compass", 15, 4.4, 58, 210,
                        List.of("کوهنوردی", "کوهپیمایی", "طبیعت‌گردی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "صفحه شفاف با خط‌کش مقیاس.",
                        spec("دقت", "۲ درجه")),
                product("KIT-100", "قمقمه استیل ۱ لیتری", "چشمه", "kitchen", 620_000, 520_000, 50, ProductStatus.LIVE, false, "droplet", 195, 4.6, 190, 980,
                        ShopCatalog.ACTIVITIES, List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "دوجداره؛ آب را خنک و چای را گرم نگه می‌دارد.",
                        spec("حجم", "۱ لیتر"), spec("جنس", "استیل ۱۸/۸")),
                product("KIT-220", "سرشعله و ظرف کمپینگ", "کوه‌سار", "kitchen", 1_450_000, 0, 14, ProductStatus.LIVE, false, "flame", 35, 4.5, 66, 230,
                        List.of("کوهنوردی", "کمپینگ", "طبیعت‌گردی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "سرشعله تاشو با ظرف آلومینیومی.",
                        spec("سوخت", "کپسول گاز پیچی"), spec("وزن", "۳۸۰ گرم")),
                product("CLB-300", "هارنس صخره‌نوردی", "یخ‌شکن", "climb", 3_200_000, 0, 9, ProductStatus.LIVE, false, "anchor", 330, 4.8, 41, 85,
                        List.of("سنگ‌نوردی"), List.of(TrailLevel.MEDIUM, TrailLevel.HARD),
                        "چهار حلقه ابزار و بند پای قابل تنظیم.",
                        spec("سایز", "S تا XL"), spec("استاندارد", "UIAA / CE")),
                product("CLB-360", "طناب دینامیک ۶۰ متری", "یخ‌شکن", "climb", 8_500_000, 0, 5, ProductStatus.DRAFT, false, "cable", 0, 0, 0, 0,
                        List.of("سنگ‌نوردی"), List.of(TrailLevel.HARD),
                        "قطر ۹٫۸ میلی‌متر. هنوز منتشر نشده.",
                        spec("طول", "۶۰ متر"), spec("قطر", "۹٫۸ میلی‌متر")),
                product("WER-400", "عینک آفتابی کوهستان UV400", "سپیده", "wear", 1_100_000, 890_000, 22, ProductStatus.LIVE, false, "glasses", 45, 4.4, 97, 360,
                        List.of("کوهنوردی", "طبیعت‌گردی", "پیاده‌روی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM, TrailLevel.HARD),
                        "برای برف و نور شدید ارتفاع.",
                        spec("فیلتر", "UV400 دسته ۳")),
                product("WER-050", "کلاه آفتابی لبه‌دار", "دامنه", "wear", 380_000, 0, 35, ProductStatus.LIVE, false, "sun", 80, 4.3, 52, 420,
                        List.of("طبیعت‌گردی", "پیاده‌روی", "کوهپیمایی"), List.of(TrailLevel.EASY, TrailLevel.MEDIUM),
                        "پارچه خنک و بند زیر چانه.",
                        spec("محافظت", "UPF 50+"))
        );
    }

    private List<ShopOrderEntity> sampleOrders(Map<String, ProductEntity> bySku) {
        return List.of(
                order("مریم احمدی", "تهران", 0, LocalTime.of(10, 42), OrderStatus.NEW, true,
                        line(bySku, "WER-310", 1), line(bySku, "KIT-100", 2)),
                order("رضا کریمی", "اصفهان", 0, LocalTime.of(9, 15), OrderStatus.NEW, true,
                        line(bySku, "BAG-045", 1)),
                order("نگار صالحی", "شیراز", 1, LocalTime.of(21, 8), OrderStatus.PREP, false,
                        line(bySku, "CMP-204", 1), line(bySku, "NAV-400", 2)),
                order("امیر حسینی", "مشهد", 1, LocalTime.of(18, 30), OrderStatus.PREP, false,
                        line(bySku, "CLB-002", 1), line(bySku, "WER-400", 1)),
                order("سارا محمدی", "تهران", 2, LocalTime.of(12, 5), OrderStatus.SENT, false,
                        line(bySku, "WER-120", 1)),
                order("حامد رحیمی", "رشت", 3, LocalTime.of(16, 44), OrderStatus.SENT, false,
                        line(bySku, "BAG-022", 1), line(bySku, "WER-050", 1)),
                order("لیلا نوری", "کرج", 4, LocalTime.of(8, 50), OrderStatus.DONE, false,
                        line(bySku, "CLB-012", 1), line(bySku, "NAV-400", 1)),
                order("کاوه مرادی", "تبریز", 5, LocalTime.of(20, 12), OrderStatus.DONE, false,
                        line(bySku, "KIT-220", 1), line(bySku, "KIT-100", 1)),
                order("پریسا جعفری", "تهران", 6, LocalTime.of(11, 37), OrderStatus.CANCEL, false,
                        line(bySku, "CMP-310", 1))
        );
    }

    private ProductEntity product(String sku, String name, String brand, String category, long priceToman, long saleToman,
                                  int stock, ProductStatus status, boolean featured, String icon, int hue,
                                  double rating, int reviews, int sold, List<String> activities, List<TrailLevel> levels,
                                  String description, String[]... specs) {
        ProductEntity entity = new ProductEntity();
        entity.setSku(sku);
        entity.setName(name);
        entity.setBrand(brand);
        entity.setCategory(category);
        entity.setPriceRial(ShopMoney.tomanToRial(priceToman));
        entity.setSaleRial(ShopMoney.tomanToRial(saleToman));
        entity.setStock(stock);
        entity.setStatus(status);
        entity.setFeatured(featured);
        entity.setIcon(icon);
        entity.setHue(hue);
        entity.setDescription(description);
        entity.setRating(rating);
        entity.setReviews(reviews);
        entity.setSold(sold);
        entity.setActivities(new LinkedHashSet<>(activities));
        entity.setLevels(new LinkedHashSet<>(levels));
        for (String[] spec : specs) {
            ProductSpecEntity row = new ProductSpecEntity();
            row.setProduct(entity);
            row.setSortOrder(entity.getSpecs().size());
            row.setSpecKey(spec[0]);
            row.setSpecValue(spec[1]);
            entity.getSpecs().add(row);
        }
        return entity;
    }

    private ShopOrderEntity order(String name, String city, int daysAgo, LocalTime time, OrderStatus status,
                                  boolean fresh, OrderItemEntity... items) {
        ShopOrderEntity entity = new ShopOrderEntity();
        entity.setCustomerName(name);
        entity.setCity(city);
        entity.setPlacedAt(LocalDateTime.of(LocalDate.now().minusDays(daysAgo), time));
        entity.setStatus(status);
        entity.setFromSite(true);
        entity.setFresh(fresh);
        long total = 0;
        for (OrderItemEntity item : items) {
            item.setOrder(entity);
            entity.getItems().add(item);
            total += ShopMoney.rialToToman(item.getUnitPriceRial()) * item.getQuantity();
        }
        entity.setShippingRial(ShopMoney.tomanToRial(ShopCatalog.shippingToman(total)));
        return entity;
    }

    private OrderItemEntity line(Map<String, ProductEntity> bySku, String sku, int quantity) {
        ProductEntity product = bySku.get(sku);
        long unit = ShopCatalog.finalPriceToman(
                ShopMoney.rialToToman(product.getPriceRial()),
                ShopMoney.rialToToman(product.getSaleRial()));
        OrderItemEntity item = new OrderItemEntity();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setQuantity(quantity);
        item.setUnitPriceRial(ShopMoney.tomanToRial(unit));
        return item;
    }

    private static String[] spec(String key, String value) {
        return new String[]{key, value};
    }
}
