package com.example.marketPlace.api.model.shop;

import java.util.List;

public final class ShopContracts {

    private ShopContracts() {
    }

    public record SpecWrite(String key, String value) {
    }

    public record ProductWrite(
            String sku,
            String name,
            String brand,
            String category,
            Long priceToman,
            Long saleToman,
            Integer stock,
            String status,
            Boolean featured,
            String icon,
            Integer hue,
            String imageData,
            String description,
            List<SpecWrite> specs,
            List<String> activities,
            List<String> levels
    ) {
    }

    public record PricePatch(Long priceToman, Long saleToman) {
    }

    public record StockPatch(Integer delta) {
    }

    public record StatusPatch(String status) {
    }

    public record BulkWrite(List<Long> ids, String action, Integer percent) {
    }

    public record SpecView(String key, String value) {
    }

    public record ProductView(
            Long id,
            String sku,
            String name,
            String brand,
            String category,
            long priceToman,
            long saleToman,
            long finalPriceToman,
            int discountPercent,
            int stock,
            String status,
            boolean featured,
            String icon,
            Integer hue,
            boolean hasImage,
            String imageData,
            String description,
            List<SpecView> specs,
            List<String> activities,
            List<String> levels,
            double rating,
            int reviews,
            int sold
    ) {
    }

    public record OrderItemView(
            Long productId,
            String productName,
            int quantity,
            long unitPriceToman,
            long lineTotalToman
    ) {
    }

    public record OrderView(
            Long id,
            String customerName,
            String city,
            String placedAt,
            long daysAgo,
            String status,
            boolean fromSite,
            boolean fresh,
            long itemsTotalToman,
            long shippingToman,
            long totalToman,
            List<OrderItemView> items
    ) {
    }

    public record CreateOrderItem(Long productId, Integer quantity) {
    }

    public record CreateOrder(String customerName, String city, List<CreateOrderItem> items) {
    }

    public record DayPoint(String date, long salesToman, int orders) {
    }

    public record DashboardView(
            long sales7DaysToman,
            int salesDeltaPercent,
            int pendingOrders,
            int newOrders,
            int preparingOrders,
            long averageOrderToman,
            long freeShippingFromToman,
            long shippingCostToman,
            int outOfStock,
            int lowStock,
            List<DayPoint> daily,
            List<OrderView> recentOrders,
            List<ProductView> stockAlerts,
            List<ProductView> bestsellers
    ) {
    }

    public record CategoryView(String id, String name, String icon) {
    }

    public record LevelView(String code, String name) {
    }

    public record MetaView(
            List<CategoryView> categories,
            List<String> activities,
            List<LevelView> levels,
            long freeShippingFromToman,
            long shippingCostToman,
            String priceUnit,
            String storedUnit
    ) {
    }

    public record SessionView(String username, String role) {
    }

    public record BulkResult(int affected, String message) {
    }
}
