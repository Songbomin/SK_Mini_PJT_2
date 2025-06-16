package com.example.mypageservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class UserEntity {
    // 새로운 유저가 회원가입하면 -> 이 서비스에서 가입한 유저의 이메일을 저장.

    // 유저가 상품을 등록하면 -> 이 서비스에서 해당 유저의 등록 물품에 해당 물품 추가
    // 셀러 id로 유저 엔티티 찾고, 해당 엔티티의 등록 물품에 해당 물품 추가

    // 유저가 찜을 하면 -> 이 서비스로 찜 정보 보내줌.
    // 찜한 아이템 id와 찜한 유저 id로 해당 유저 엔티티의 찜한 아이템에 해당 아이템 추가

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    String email;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    List<ProductEntity> userProducts;


    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    List<WishEntity> userWishes;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    List<PurchaseEntity> userPurchases;

    @Builder
    public UserEntity(String email) {
        this.email = email;
    }
}
