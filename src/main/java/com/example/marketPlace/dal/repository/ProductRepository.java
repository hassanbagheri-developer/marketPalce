package com.example.marketPlace.dal.repository;

import com.example.marketPlace.dal.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @EntityGraph(attributePaths = {"specs", "activities", "levels"})
    @Query("select p from ProductEntity p order by p.id desc")
    List<ProductEntity> findAllDetailed();

    @Override
    @EntityGraph(attributePaths = {"specs", "activities", "levels"})
    Optional<ProductEntity> findById(Long id);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);
}
