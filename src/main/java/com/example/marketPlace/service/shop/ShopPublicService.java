package com.example.marketPlace.service.shop;

import com.example.marketPlace.api.model.shop.ShopContracts.CreateOrder;
import com.example.marketPlace.api.model.shop.ShopContracts.CreateOrderItem;
import com.example.marketPlace.api.model.shop.ShopContracts.OrderView;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductView;
import com.example.marketPlace.dal.entity.OrderItemEntity;
import com.example.marketPlace.dal.entity.ProductEntity;
import com.example.marketPlace.dal.entity.ShopOrderEntity;
import com.example.marketPlace.dal.enums.OrderStatus;
import com.example.marketPlace.dal.enums.ProductStatus;
import com.example.marketPlace.dal.repository.ProductRepository;
import com.example.marketPlace.dal.repository.ShopOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopPublicService {

    private final ProductRepository products;
    private final ShopOrderRepository orders;

    @Transactional(readOnly = true)
    public List<ProductView> liveProducts() {
        return products.findAllDetailed().stream()
                .filter(product -> product.getStatus() == ProductStatus.LIVE)
                .map(product -> ShopViews.product(product, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductView liveProduct(Long id) {
        ProductEntity product = products.findById(id)
                .filter(item -> item.getStatus() == ProductStatus.LIVE)
                .orElseThrow(() -> ShopException.notFound("کالا پیدا نشد"));
        return ShopViews.product(product, true);
    }

    @Transactional
    public OrderView placeOrder(CreateOrder request) {
        if (request == null || request.customerName() == null || request.customerName().isBlank()) {
            throw ShopException.badRequest("نام مشتری لازم است");
        }
        if (request.city() == null || request.city().isBlank()) {
            throw ShopException.badRequest("شهر لازم است");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw ShopException.badRequest("سفارش بدون کالا است");
        }

        ShopOrderEntity order = new ShopOrderEntity();
        order.setCustomerName(request.customerName().trim());
        order.setCity(request.city().trim());
        order.setPlacedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setFromSite(true);
        order.setFresh(true);

        long itemsTotalToman = 0;
        for (CreateOrderItem line : request.items()) {
            if (line == null || line.productId() == null || line.quantity() == null || line.quantity() < 1) {
                throw ShopException.badRequest("تعداد کالا نامعتبر است");
            }
            ProductEntity product = products.findById(line.productId())
                    .orElseThrow(() -> ShopException.notFound("کالا پیدا نشد"));
            if (product.getStatus() != ProductStatus.LIVE) {
                throw ShopException.badRequest("این کالا در فروشگاه منتشر نشده");
            }
            if (product.getStock() < line.quantity()) {
                throw ShopException.badRequest("موجودی «" + product.getName() + "» کافی نیست");
            }
            long unitToman = ShopCatalog.finalPriceToman(
                    ShopMoney.rialToToman(product.getPriceRial()),
                    ShopMoney.rialToToman(product.getSaleRial()));
            product.setStock(product.getStock() - line.quantity());
            product.setSold(product.getSold() + line.quantity());

            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(line.quantity());
            item.setUnitPriceRial(ShopMoney.tomanToRial(unitToman));
            order.getItems().add(item);
            itemsTotalToman += unitToman * line.quantity();
        }
        order.setShippingRial(ShopMoney.tomanToRial(ShopCatalog.shippingToman(itemsTotalToman)));
        return ShopViews.order(orders.save(order));
    }
}
