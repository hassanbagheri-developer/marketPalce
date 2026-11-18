package com.example.marketPlace.dal.entity;

import com.example.marketPlace.dal.enums.ProductStatus;
import com.example.marketPlace.dal.enums.TrailLevel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "shop_product")
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 120)
    private String brand;

    @Column(nullable = false, length = 32)
    private String category;

    @Column(nullable = false)
    private long priceRial;

    @Column(nullable = false)
    private long saleRial;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProductStatus status;

    @Column(nullable = false)
    private boolean featured;

    @Column(length = 40)
    private String icon;

    private Integer hue;

    @Column(columnDefinition = "LONGTEXT")
    private String imageData;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private int reviews;

    @Column(nullable = false)
    private int sold;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "shop_product_activity", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "activity", nullable = false, length = 40)
    private Set<String> activities = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "shop_product_level", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "level_name", nullable = false, length = 16)
    private Set<TrailLevel> levels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc")
    private List<ProductSpecEntity> specs = new ArrayList<>();
}
