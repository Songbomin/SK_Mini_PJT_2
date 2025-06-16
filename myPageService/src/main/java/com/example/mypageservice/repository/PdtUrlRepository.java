package com.example.mypageservice.repository;

import com.example.mypageservice.entity.PdtUrlEntity;
import com.example.mypageservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdtUrlRepository extends JpaRepository<PdtUrlEntity, Long> {
    List<PdtUrlEntity> findAllByProductEntity(ProductEntity productEntity);
}
