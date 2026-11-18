package com.example.marketPlace.api.controller;

import com.example.marketPlace.api.model.shop.ShopContracts.BulkResult;
import com.example.marketPlace.api.model.shop.ShopContracts.BulkWrite;
import com.example.marketPlace.api.model.shop.ShopContracts.DashboardView;
import com.example.marketPlace.api.model.shop.ShopContracts.MetaView;
import com.example.marketPlace.api.model.shop.ShopContracts.OrderView;
import com.example.marketPlace.api.model.shop.ShopContracts.PricePatch;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductView;
import com.example.marketPlace.api.model.shop.ShopContracts.ProductWrite;
import com.example.marketPlace.api.model.shop.ShopContracts.SessionView;
import com.example.marketPlace.api.model.shop.ShopContracts.StatusPatch;
import com.example.marketPlace.api.model.shop.ShopContracts.StockPatch;
import com.example.marketPlace.config.security.ShopSecurity;
import com.example.marketPlace.service.shop.ShopAdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shop")
@PreAuthorize(ShopSecurity.HAS_MANAGER)
@SecurityRequirement(name = "bearerAuth")
public class ShopAdminController {

    private final ShopAdminService shopAdminService;

    public ShopAdminController(ShopAdminService shopAdminService) {
        this.shopAdminService = shopAdminService;
    }

    @GetMapping("/session")
    public SessionView session(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return new SessionView(username == null || username.isBlank() ? jwt.getSubject() : username,
                ShopSecurity.MANAGER);
    }

    @GetMapping("/meta")
    public MetaView meta() {
        return shopAdminService.meta();
    }

    @GetMapping("/dashboard")
    public DashboardView dashboard() {
        return shopAdminService.dashboard();
    }

    @GetMapping("/products")
    public List<ProductView> products(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false, defaultValue = "all") String filter,
                                      @RequestParam(required = false, defaultValue = "false") boolean includeImage) {
        return shopAdminService.listProducts(q, category, filter, includeImage);
    }

    @GetMapping("/products/{id}")
    public ProductView product(@PathVariable Long id) {
        return shopAdminService.getProduct(id);
    }

    @PostMapping("/products")
    public ProductView create(@RequestBody ProductWrite write) {
        return shopAdminService.createProduct(write);
    }

    @PutMapping("/products/{id}")
    public ProductView update(@PathVariable Long id, @RequestBody ProductWrite write) {
        return shopAdminService.updateProduct(id, write);
    }

    @PatchMapping("/products/{id}/price")
    public ProductView price(@PathVariable Long id, @RequestBody PricePatch patch) {
        return shopAdminService.patchPrice(id, patch);
    }

    @PatchMapping("/products/{id}/stock")
    public ProductView stock(@PathVariable Long id, @RequestBody StockPatch patch) {
        return shopAdminService.patchStock(id, patch);
    }

    @PatchMapping("/products/{id}/status")
    public ProductView status(@PathVariable Long id, @RequestBody StatusPatch patch) {
        return shopAdminService.patchStatus(id, patch == null ? null : patch.status());
    }

    @PostMapping("/products/{id}/duplicate")
    public ProductView duplicate(@PathVariable Long id) {
        return shopAdminService.duplicate(id);
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable Long id) {
        shopAdminService.deleteProduct(id);
    }

    @PostMapping("/products/bulk")
    public BulkResult bulk(@RequestBody BulkWrite write) {
        return shopAdminService.bulk(write);
    }

    @GetMapping("/orders")
    public List<OrderView> orders(@RequestParam(required = false, defaultValue = "all") String status) {
        return shopAdminService.listOrders(status);
    }

    @GetMapping("/orders/{id}")
    public OrderView order(@PathVariable Long id) {
        return shopAdminService.getOrder(id);
    }

    @PatchMapping("/orders/{id}/status")
    public OrderView orderStatus(@PathVariable Long id, @RequestBody StatusPatch patch) {
        return shopAdminService.patchOrderStatus(id, patch == null ? null : patch.status());
    }
}
