package com.example.marketPlace.service.shop;

import com.example.marketPlace.api.model.shop.ShopContracts.BulkResult;
import com.example.marketPlace.api.model.shop.ShopContracts.BulkWrite;
import com.example.marketPlace.api.model.shop.ShopContracts.DashboardView;
import com.example.marketPlace.api.model.shop.ShopContracts.DayPoint;
import com.example.marketPlace.api.model.shop.ShopContracts.MetaView;
import com.example.marketPlace.api.model.shop.ShopContracts.CategoryView;
import com.example.marketPlace.api.model.shop.ShopContracts.LevelView;
import com.example.marketPlace.api.model.shop.ShopContracts.OrderView;
import com.example.marketPlace.api.model.shop.ShopContracts.PricePatch;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductView;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductWrite;
import com.example.marketPlace.api.model.shop.ShopContracts.SpecWrite;
import com.example.marketPlace.api.model.shop.ShopContracts.StockPatch;
import com.example.marketPlace.dal.entity.ProductEntity;
import com.example.marketPlace.dal.entity.ProductSpecEntity;
import com.example.marketPlace.dal.entity.ShopOrderEntity;
import com.example.marketPlace.dal.enums.OrderStatus;
import com.example.marketPlace.dal.enums.ProductStatus;
import com.example.marketPlace.dal.enums.TrailLevel;
import com.example.marketPlace.dal.repository.ProductRepository;
import com.example.marketPlace.dal.repository.ShopOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShopAdminService {

    private final ProductRepository products;
    private final ShopOrderRepository orders;

    @Transactional(readOnly = true)
    public MetaView meta() {
        return new MetaView(
                ShopCatalog.CATEGORIES.stream()
                        .map(category -> new CategoryView(category.id(), category.name(), category.icon()))
                        .toList(),
                ShopCatalog.ACTIVITIES,
                ShopCatalog.LEVELS.stream()
                        .map(level -> new LevelView(level.code().name(), level.name()))
                        .toList(),
                ShopCatalog.FREE_SHIPPING_FROM_TOMAN,
                ShopCatalog.SHIPPING_COST_TOMAN,
                "toman",
                "rial"
        );
    }

    @Transactional(readOnly = true)
    public List<ProductView> listProducts(String query, String category, String filter, boolean includeImage) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return products.findAllDetailed().stream()
                .filter(product -> category == null || category.isBlank() || category.equals(product.getCategory()))
                .filter(product -> matchesFilter(product, filter))
                .filter(product -> needle.isEmpty() || matchesQuery(product, needle))
                .map(product -> ShopViews.product(product, includeImage))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductView getProduct(Long id) {
        return ShopViews.product(requireProduct(id), true);
    }

    @Transactional
    public ProductView createProduct(ProductWrite write) {
        ProductEntity entity = new ProductEntity();
        applyWrite(entity, write, true);
        return ShopViews.product(products.save(entity), true);
    }

    @Transactional
    public ProductView updateProduct(Long id, ProductWrite write) {
        ProductEntity entity = requireProduct(id);
        applyWrite(entity, write, false);
        return ShopViews.product(entity, true);
    }

    @Transactional
    public ProductView patchPrice(Long id, PricePatch patch) {
        ProductEntity entity = requireProduct(id);
        if (patch == null || patch.priceToman() == null) {
            throw ShopException.badRequest("قیمت را وارد کن.");
        }
        long price = patch.priceToman();
        long sale = patch.saleToman() == null ? ShopMoney.rialToToman(entity.getSaleRial()) : patch.saleToman();
        applyPrices(entity, price, sale);
        return ShopViews.product(entity, false);
    }

    @Transactional
    public ProductView patchStock(Long id, StockPatch patch) {
        if (patch == null || patch.delta() == null) {
            throw ShopException.badRequest("مقدار تغییر موجودی لازم است");
        }
        ProductEntity entity = requireProduct(id);
        entity.setStock(Math.max(0, entity.getStock() + patch.delta()));
        return ShopViews.product(entity, false);
    }

    @Transactional
    public ProductView patchStatus(Long id, String status) {
        ProductEntity entity = requireProduct(id);
        ProductStatus next = ProductStatus.fromCode(status);
        if (next == ProductStatus.LIVE && entity.getPriceRial() <= 0) {
            throw ShopException.badRequest("اول قیمت را مشخص کن");
        }
        entity.setStatus(next);
        return ShopViews.product(entity, false);
    }

    @Transactional
    public ProductView duplicate(Long id) {
        ProductEntity source = requireProduct(id);
        ProductEntity copy = new ProductEntity();
        copy.setSku(nextCopySku(source.getSku()));
        copy.setName(source.getName() + " (کپی)");
        copy.setBrand(source.getBrand());
        copy.setCategory(source.getCategory());
        copy.setPriceRial(source.getPriceRial());
        copy.setSaleRial(source.getSaleRial());
        copy.setStock(source.getStock());
        copy.setStatus(ProductStatus.DRAFT);
        copy.setFeatured(false);
        copy.setIcon(source.getIcon());
        copy.setHue(source.getHue());
        copy.setImageData(source.getImageData());
        copy.setDescription(source.getDescription());
        copy.setRating(0);
        copy.setReviews(0);
        copy.setSold(0);
        copy.setActivities(new LinkedHashSet<>(source.getActivities()));
        copy.setLevels(new LinkedHashSet<>(source.getLevels()));
        copy.setSpecs(new ArrayList<>());
        source.getSpecs().forEach(spec -> addSpec(copy, spec.getSpecKey(), spec.getSpecValue()));
        return ShopViews.product(products.save(copy), true);
    }

    @Transactional
    public void deleteProduct(Long id) {
        ProductEntity entity = requireProduct(id);
        products.delete(entity);
    }

    @Transactional
    public BulkResult bulk(BulkWrite write) {
        if (write == null || write.ids() == null || write.ids().isEmpty()) {
            throw ShopException.badRequest("کالایی انتخاب نشده");
        }
        String action = write.action() == null ? "" : write.action().trim().toLowerCase(Locale.ROOT);
        List<ProductEntity> selected = write.ids().stream().distinct().map(this::requireProduct).toList();
        int affected = switch (action) {
            case "off" -> applyDiscount(selected, percent(write));
            case "up" -> changePrice(selected, percent(write), true);
            case "down" -> changePrice(selected, percent(write), false);
            case "nooff" -> clearDiscount(selected);
            case "live" -> publish(selected);
            case "draft" -> draft(selected);
            case "delete" -> deleteAll(selected);
            default -> throw ShopException.badRequest("عملیات گروهی نامعتبر است");
        };
        return new BulkResult(affected, "انجام شد");
    }

    @Transactional(readOnly = true)
    public List<OrderView> listOrders(String status) {
        List<ShopOrderEntity> found = status == null || status.isBlank() || "all".equalsIgnoreCase(status)
                ? orders.findAllDetailed()
                : orders.findByStatusOrderByPlacedAtDesc(OrderStatus.fromCode(status));
        return found.stream().map(ShopViews::order).toList();
    }

    @Transactional(readOnly = true)
    public OrderView getOrder(Long id) {
        return ShopViews.order(requireOrder(id));
    }

    @Transactional
    public OrderView patchOrderStatus(Long id, String status) {
        ShopOrderEntity order = requireOrder(id);
        OrderStatus next = OrderStatus.fromCode(status);
        OrderStatus previous = order.getStatus();
        if (previous == next) {
            return ShopViews.order(order);
        }
        if (next == OrderStatus.CANCEL) {
            order.getItems().forEach(item -> adjustStock(item.getProductId(), item.getQuantity()));
        }
        if (previous == OrderStatus.CANCEL) {
            order.getItems().forEach(item -> adjustStock(item.getProductId(), -item.getQuantity()));
        }
        order.setStatus(next);
        order.setFresh(false);
        return ShopViews.order(order);
    }

    @Transactional(readOnly = true)
    public DashboardView dashboard() {
        LocalDate today = LocalDate.now();
        List<ShopOrderEntity> all = orders.findAllDetailed();
        List<ProductEntity> catalog = products.findAllDetailed();

        long recent = salesBetween(all, today.minusDays(6), today);
        long previous = salesBetween(all, today.minusDays(13), today.minusDays(7));
        int delta = previous == 0 ? 0 : (int) Math.round(((recent - previous) * 100d) / previous);

        List<ShopOrderEntity> pending = all.stream()
                .filter(order -> order.getStatus() == OrderStatus.NEW || order.getStatus() == OrderStatus.PREP)
                .toList();
        int newOrders = (int) pending.stream().filter(order -> order.getStatus() == OrderStatus.NEW).count();

        List<ShopOrderEntity> billable = all.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCEL)
                .toList();
        long average = billable.isEmpty() ? 0 : Math.round(billable.stream()
                .mapToLong(order -> ShopMoney.rialToToman(ShopViews.itemsTotalRial(order) + order.getShippingRial()))
                .average()
                .orElse(0));

        List<ProductEntity> out = catalog.stream()
                .filter(product -> product.getStatus() == ProductStatus.LIVE && product.getStock() <= 0)
                .toList();
        List<ProductEntity> low = catalog.stream()
                .filter(product -> product.getStock() > 0 && product.getStock() <= ShopCatalog.LOW_STOCK_AT)
                .toList();
        List<ProductView> alerts = new ArrayList<>();
        out.forEach(product -> alerts.add(ShopViews.product(product, false)));
        low.stream()
                .filter(product -> out.stream().noneMatch(other -> other.getId().equals(product.getId())))
                .forEach(product -> alerts.add(ShopViews.product(product, false)));
        alerts.sort(Comparator.comparingInt(ProductView::stock));

        List<ProductView> best = catalog.stream()
                .filter(product -> product.getSold() > 0)
                .sorted(Comparator.comparingInt(ProductEntity::getSold).reversed())
                .limit(5)
                .map(product -> ShopViews.product(product, false))
                .toList();

        List<DayPoint> daily = new ArrayList<>();
        for (int offset = 13; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            List<ShopOrderEntity> dayOrders = billable.stream()
                    .filter(order -> order.getPlacedAt().toLocalDate().equals(day))
                    .toList();
            long amount = dayOrders.stream()
                    .mapToLong(order -> ShopMoney.rialToToman(ShopViews.itemsTotalRial(order)))
                    .sum();
            daily.add(new DayPoint(day.toString(), amount, dayOrders.size()));
        }

        List<OrderView> recentOrders = all.stream().limit(5).map(ShopViews::order).toList();
        return new DashboardView(
                recent,
                delta,
                pending.size(),
                newOrders,
                pending.size() - newOrders,
                average,
                ShopCatalog.FREE_SHIPPING_FROM_TOMAN,
                ShopCatalog.SHIPPING_COST_TOMAN,
                out.size(),
                low.size(),
                daily,
                recentOrders,
                alerts,
                best
        );
    }

    private void applyWrite(ProductEntity entity, ProductWrite write, boolean creating) {
        if (write == null) {
            throw ShopException.badRequest("بدنه درخواست خالی است");
        }
        String name = write.name() == null ? "" : write.name().trim();
        if (name.length() < 3) {
            throw ShopException.badRequest("نام کالا را بنویس (حداقل ۳ حرف).");
        }
        String sku = write.sku() == null ? "" : write.sku().trim();
        if (sku.isBlank() || sku.length() > 64) {
            throw ShopException.badRequest("کد کالا لازم است");
        }
        boolean skuTaken = creating
                ? products.existsBySkuIgnoreCase(sku)
                : products.existsBySkuIgnoreCaseAndIdNot(sku, entity.getId());
        if (skuTaken) {
            throw ShopException.conflict("این کد کالا قبلاً ثبت شده");
        }
        ShopCatalog.requireCategory(write.category());
        long price = write.priceToman() == null ? 0 : write.priceToman();
        long sale = write.saleToman() == null ? 0 : write.saleToman();
        int stock = write.stock() == null ? 0 : write.stock();
        if (stock < 0) {
            throw ShopException.badRequest("موجودی نمی‌تواند منفی باشد.");
        }
        if (write.imageData() != null && write.imageData().length() > 1_500_000) {
            throw ShopException.badRequest("حجم عکس زیاد است");
        }
        ProductStatus status = write.status() == null || write.status().isBlank()
                ? ProductStatus.DRAFT
                : ProductStatus.fromCode(write.status());
        if (status == ProductStatus.LIVE && price <= 0) {
            throw ShopException.badRequest("اول قیمت را مشخص کن");
        }
        ShopCatalog.requireActivities(write.activities());

        entity.setSku(sku);
        entity.setName(name);
        entity.setBrand(blankToNull(write.brand()));
        entity.setCategory(write.category());
        entity.setStock(stock);
        entity.setStatus(status);
        entity.setFeatured(Boolean.TRUE.equals(write.featured()));
        entity.setIcon(write.icon() == null || write.icon().isBlank() ? "package" : write.icon().trim());
        entity.setHue(write.hue());
        entity.setImageData(blankToNull(write.imageData()));
        entity.setDescription(blankToNull(write.description()));
        if (creating) {
            entity.setRating(0);
            entity.setReviews(0);
            entity.setSold(0);
        }
        applyPrices(entity, price, sale);
        replaceActivities(entity, write.activities());
        replaceLevels(entity, write.levels());
        replaceSpecs(entity, write.specs());
    }

    private void applyPrices(ProductEntity entity, long priceToman, long saleToman) {
        if (priceToman <= 0) {
            throw ShopException.badRequest("قیمت باید بیشتر از صفر باشد.");
        }
        if (saleToman < 0 || (saleToman > 0 && saleToman >= priceToman)) {
            throw ShopException.badRequest("قیمت با تخفیف باید از قیمت اصلی کمتر باشد.");
        }
        entity.setPriceRial(ShopMoney.tomanToRial(priceToman));
        entity.setSaleRial(ShopMoney.tomanToRial(saleToman));
    }

    private void replaceActivities(ProductEntity entity, List<String> activities) {
        entity.getActivities().clear();
        if (activities != null) {
            activities.stream().filter(Objects::nonNull).map(String::trim).filter(value -> !value.isEmpty())
                    .forEach(entity.getActivities()::add);
        }
    }

    private void replaceLevels(ProductEntity entity, List<String> levels) {
        entity.getLevels().clear();
        if (levels != null) {
            levels.stream().filter(Objects::nonNull).map(TrailLevel::fromCode).forEach(entity.getLevels()::add);
        }
    }

    private void replaceSpecs(ProductEntity entity, List<SpecWrite> specs) {
        entity.getSpecs().clear();
        if (specs == null) {
            return;
        }
        for (SpecWrite spec : specs) {
            if (spec == null) {
                continue;
            }
            String key = spec.key() == null ? "" : spec.key().trim();
            String value = spec.value() == null ? "" : spec.value().trim();
            if (key.isEmpty() && value.isEmpty()) {
                continue;
            }
            addSpec(entity, key, value);
        }
    }

    private void addSpec(ProductEntity entity, String key, String value) {
        ProductSpecEntity spec = new ProductSpecEntity();
        spec.setProduct(entity);
        spec.setSortOrder(entity.getSpecs().size());
        spec.setSpecKey(key);
        spec.setSpecValue(value);
        entity.getSpecs().add(spec);
    }

    private int applyDiscount(List<ProductEntity> selected, int percent) {
        selected.forEach(product -> {
            long price = ShopMoney.rialToToman(product.getPriceRial());
            long sale = ShopMoney.roundToThousandToman(Math.round(price * (1d - percent / 100d)));
            if (sale <= 0 || sale >= price) {
                product.setSaleRial(0);
            } else {
                product.setSaleRial(ShopMoney.tomanToRial(sale));
            }
        });
        return selected.size();
    }

    private int changePrice(List<ProductEntity> selected, int percent, boolean up) {
        double factor = up ? 1d + percent / 100d : 1d - percent / 100d;
        selected.forEach(product -> {
            long price = ShopMoney.roundToThousandToman(Math.round(ShopMoney.rialToToman(product.getPriceRial()) * factor));
            if (price <= 0) {
                return;
            }
            long sale = product.getSaleRial() == 0
                    ? 0
                    : ShopMoney.roundToThousandToman(Math.round(ShopMoney.rialToToman(product.getSaleRial()) * factor));
            if (sale >= price) {
                sale = 0;
            }
            product.setPriceRial(ShopMoney.tomanToRial(price));
            product.setSaleRial(ShopMoney.tomanToRial(sale));
        });
        return selected.size();
    }

    private int clearDiscount(List<ProductEntity> selected) {
        selected.forEach(product -> product.setSaleRial(0));
        return selected.size();
    }

    private int publish(List<ProductEntity> selected) {
        int count = 0;
        for (ProductEntity product : selected) {
            if (product.getPriceRial() > 0) {
                product.setStatus(ProductStatus.LIVE);
                count++;
            }
        }
        return count;
    }

    private int draft(List<ProductEntity> selected) {
        selected.forEach(product -> product.setStatus(ProductStatus.DRAFT));
        return selected.size();
    }

    private int deleteAll(List<ProductEntity> selected) {
        products.deleteAll(selected);
        return selected.size();
    }

    private int percent(BulkWrite write) {
        int value = write.percent() == null ? 0 : write.percent();
        if (value < 1 || value > 90) {
            throw ShopException.badRequest("درصد باید بین ۱ تا ۹۰ باشد.");
        }
        return value;
    }

    private void adjustStock(Long productId, int delta) {
        products.findById(productId).ifPresent(product ->
                product.setStock(Math.max(0, product.getStock() + delta)));
    }

    private long salesBetween(List<ShopOrderEntity> all, LocalDate from, LocalDate to) {
        return all.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCEL)
                .filter(order -> {
                    LocalDate day = order.getPlacedAt().toLocalDate();
                    return !day.isBefore(from) && !day.isAfter(to);
                })
                .mapToLong(order -> ShopMoney.rialToToman(ShopViews.itemsTotalRial(order)))
                .sum();
    }

    private boolean matchesFilter(ProductEntity product, String filter) {
        if (filter == null || filter.isBlank() || "all".equalsIgnoreCase(filter)) {
            return true;
        }
        long price = ShopMoney.rialToToman(product.getPriceRial());
        long sale = ShopMoney.rialToToman(product.getSaleRial());
        return switch (filter.toLowerCase(Locale.ROOT)) {
            case "live" -> product.getStatus() == ProductStatus.LIVE;
            case "draft" -> product.getStatus() == ProductStatus.DRAFT;
            case "alert" -> product.getStock() <= ShopCatalog.LOW_STOCK_AT;
            case "sale" -> ShopCatalog.discountPercent(price, sale) > 0;
            default -> throw ShopException.badRequest("فیلتر کالا نامعتبر است");
        };
    }

    private boolean matchesQuery(ProductEntity product, String needle) {
        return contains(product.getName(), needle)
                || contains(product.getBrand(), needle)
                || contains(product.getSku(), needle);
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    private String nextCopySku(String sku) {
        String base = sku + "-C";
        String candidate = base;
        int n = 2;
        while (products.existsBySkuIgnoreCase(candidate)) {
            candidate = base + n;
            n++;
        }
        return candidate;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private ProductEntity requireProduct(Long id) {
        return products.findById(id).orElseThrow(() -> ShopException.notFound("کالا پیدا نشد"));
    }

    private ShopOrderEntity requireOrder(Long id) {
        return orders.findById(id).orElseThrow(() -> ShopException.notFound("سفارش پیدا نشد"));
    }
}
