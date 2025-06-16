package com.example.mypageservice.repository;


import com.example.mypageservice.entity.PurchaseEntity;
import com.example.mypageservice.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {
    Page<PurchaseEntity> findAllByUserEntity(UserEntity userEntity, Pageable pageable);
}
