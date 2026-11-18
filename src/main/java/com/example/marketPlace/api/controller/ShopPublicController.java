package com.example.marketPlace.api.controller;

import com.example.marketPlace.api.model.shop.ShopContracts.CreateOrder;
import com.example.marketPlace.api.model.shop.ShopContracts.OrderView;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductView;
import com.example.marketPlace.service.shop.ShopPublicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/shop")
public class ShopPublicController {

    private final ShopPublicService shopPublicService;

    public ShopPublicController(ShopPublicService shopPublicService) {
        this.shopPublicService = shopPublicService;
    }

    @GetMapping("/products")
    public List<ProductView> products() {
        return shopPublicService.liveProducts();
    }

    @GetMapping("/products/{id}")
    public ProductView product(@PathVariable Long id) {
        return shopPublicService.liveProduct(id);
    }

    @PostMapping("/orders")
    public OrderView placeOrder(@RequestBody CreateOrder request) {
        return shopPublicService.placeOrder(request);
    }
}
