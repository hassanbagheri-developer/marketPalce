package com.example.marketPlace.service.shop;

import com.example.marketPlace.api.model.shop.ShopContracts.OrderItemView;
import com.example.marketPlace.api.model.shop.ShopContracts.OrderView;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductView;
import com.example.marketPlace.api.model.shop.ShopContracts.SpecView;
import com.example.marketPlace.dal.entity.OrderItemEntity;
import com.example.marketPlace.dal.entity.ProductEntity;
import com.example.marketPlace.dal.entity.ShopOrderEntity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public final class ShopViews {

    private ShopViews() {
    }

    public static ProductView product(ProductEntity entity, boolean includeImage) {
        long price = ShopMoney.rialToToman(entity.getPriceRial());
        long sale = ShopMoney.rialToToman(entity.getSaleRial());
        boolean hasImage = entity.getImageData() != null && !entity.getImageData().isBlank();
        List<SpecView> specs = entity.getSpecs().stream()
                .sorted(Comparator.comparingInt(spec -> spec.getSortOrder()))
                .map(spec -> new SpecView(spec.getSpecKey(), spec.getSpecValue()))
                .toList();
        List<String> levels = entity.getLevels().stream().map(Enum::name).sorted().toList();
        return new ProductView(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getBrand(),
                entity.getCategory(),
                price,
                sale,
                ShopCatalog.finalPriceToman(price, sale),
                ShopCatalog.discountPercent(price, sale),
                entity.getStock(),
                entity.getStatus().code(),
                entity.isFeatured(),
                entity.getIcon(),
                entity.getHue(),
                hasImage,
                includeImage ? entity.getImageData() : null,
                entity.getDescription(),
                specs,
                List.copyOf(entity.getActivities()),
                levels,
                entity.getRating(),
                entity.getReviews(),
                entity.getSold()
        );
    }

    public static OrderView order(ShopOrderEntity entity) {
        List<OrderItemView> items = entity.getItems().stream().map(ShopViews::item).toList();
        long itemsTotal = items.stream().mapToLong(OrderItemView::lineTotalToman).sum();
        long shipping = ShopMoney.rialToToman(entity.getShippingRial());
        LocalDate placed = entity.getPlacedAt().toLocalDate();
        long daysAgo = Math.max(0, ChronoUnit.DAYS.between(placed, LocalDate.now()));
        return new OrderView(
                entity.getId(),
                entity.getCustomerName(),
                entity.getCity(),
                entity.getPlacedAt().toString(),
                daysAgo,
                entity.getStatus().code(),
                entity.isFromSite(),
                entity.isFresh(),
                itemsTotal,
                shipping,
                itemsTotal + shipping,
                items
        );
    }

    public static long itemsTotalRial(ShopOrderEntity entity) {
        return entity.getItems().stream()
                .mapToLong(item -> item.getUnitPriceRial() * item.getQuantity())
                .sum();
    }

    private static OrderItemView item(OrderItemEntity entity) {
        long unit = ShopMoney.rialToToman(entity.getUnitPriceRial());
        return new OrderItemView(
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                unit,
                unit * entity.getQuantity()
        );
    }
}
