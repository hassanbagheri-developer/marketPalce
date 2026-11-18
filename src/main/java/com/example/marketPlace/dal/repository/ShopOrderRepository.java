package com.example.marketPlace.dal.repository;

import com.example.marketPlace.dal.entity.ShopOrderEntity;
import com.example.marketPlace.dal.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShopOrderRepository extends JpaRepository<ShopOrderEntity, Long> {

    @EntityGraph(attributePaths = "items")
    @Query("select o from ShopOrderEntity o order by o.placedAt desc")
    List<ShopOrderEntity> findAllDetailed();

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<ShopOrderEntity> findById(Long id);

    @EntityGraph(attributePaths = "items")
    List<ShopOrderEntity> findByStatusOrderByPlacedAtDesc(OrderStatus status);
}
