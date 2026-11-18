package com.example.marketPlace.dal.entity;

import com.example.marketPlace.dal.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shop_order")
public class ShopOrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String customerName;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false)
    private LocalDateTime placedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrderStatus status;

    @Column(nullable = false)
    private long shippingRial;

    @Column(nullable = false)
    private boolean fromSite;

    @Column(nullable = false)
    private boolean fresh;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<OrderItemEntity> items = new ArrayList<>();
}
