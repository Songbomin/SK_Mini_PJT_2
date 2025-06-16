package com.example.mypageservice.repository;

import com.example.mypageservice.entity.ProductEntity;
import com.example.mypageservice.entity.UserEntity;
import com.example.mypageservice.entity.WishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<WishEntity, Long> {
    Page<WishEntity> findAllByUserEntity(UserEntity userEntity, Pageable pageable);

    Optional<WishEntity> findByUserEntityAndProductEntity(UserEntity userEntity, ProductEntity productEntity);
}
