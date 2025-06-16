package com.example.mypageservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ProductEntity productEntity;

    @ManyToOne
    private UserEntity userEntity;

    @Builder
    public PurchaseEntity(ProductEntity productEntity, UserEntity userEntity) {
        this.productEntity = productEntity;
        this.userEntity = userEntity;
    }
}
