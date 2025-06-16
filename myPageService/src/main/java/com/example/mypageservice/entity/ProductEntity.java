package com.example.mypageservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 다른 서비스에서 등록한 물품 id 카프카로 받아오기
    @Column(nullable = false)
    private long pdtId;

    // 이름, 가격,
    private String pdtName;

    private Float price;

    private boolean exist;

    private String dtype;

    @ManyToOne
    private UserEntity userEntity;


    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.REMOVE)
    List<WishEntity> pdtWishes;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.REMOVE)
    List<PurchaseEntity> pdtPurchases;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.REMOVE)
    List<PdtUrlEntity> pdtUrls;

    @Builder
    public ProductEntity(long pdtId, String pdtName, Float price, boolean exist, String dtype, UserEntity userEntity) {
        this.pdtId = pdtId;
        this.pdtName = pdtName;
        this.price = price;
        this.exist = exist;
        this.userEntity = userEntity;
        this.dtype = dtype;
    }
}
