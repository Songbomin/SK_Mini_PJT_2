package com.example.mypageservice.service;

import com.example.mypageservice.dto.ProductResDto;
import com.example.mypageservice.entity.*;
import com.example.mypageservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyPageService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PdtUrlRepository pdtUrlRepository;
    @Autowired
    private ProductRepository productRepository;

    public Map<String, Object> getSales(String email, int page, int size) {
        System.out.println("현재 이메일: " + email);
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다.") );
        for(ProductEntity productEntity: userEntity.getUserProducts()){
            System.out.println(productEntity.getPdtName() + " " + productEntity.getPdtId());
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductEntity> productEntities = productRepository.findByUserEntity(userEntity, pageable);

        Map<String, Object> response = new HashMap<>();
        List<ProductResDto> productResDtos = new ArrayList<>();
        for(ProductEntity productEntity : productEntities) {
            // 이미지 url들 받아오기
            List<PdtUrlEntity> pdtUrlEntities = pdtUrlRepository.findAllByProductEntity(productEntity);
            List<String> pdtUrls = new ArrayList<>();
            for(PdtUrlEntity pdtUrlEntity : pdtUrlEntities) {
                pdtUrls.add(pdtUrlEntity.getUrl());
            }

            productResDtos.add(ProductResDto.builder()
                            .pdtId(productEntity.getPdtId())
                            .pdtName(productEntity.getPdtName())
                            .price(productEntity.getPrice())
                            .imageUrl(pdtUrls)
                    .build());
        }
        response.put("totalPages", productEntities.getTotalPages());
        response.put("products", productResDtos);
        return response;
    }

    public Map<String, Object> getPurchases(String email, int page, int size) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다.") );
        PageRequest pageable = PageRequest.of(page, size);
        Page<PurchaseEntity> purchaseEntities = purchaseRepository.findAllByUserEntity(userEntity, pageable);

        Map<String, Object> response = new HashMap<>();
        List<ProductResDto> productResDtos = new ArrayList<>();
        for(PurchaseEntity purchaseEntity : purchaseEntities) {
            ProductEntity productEntity = purchaseEntity.getProductEntity();

            // 이미지 url
            List<PdtUrlEntity> pdtUrlEntities = pdtUrlRepository.findAllByProductEntity(productEntity);
            List<String> pdtUrls = new ArrayList<>();
            for(PdtUrlEntity pdtUrlEntity : pdtUrlEntities) {
                pdtUrls.add(pdtUrlEntity.getUrl());
            }

            productResDtos.add(ProductResDto.builder()
                    .pdtId(productEntity.getPdtId())
                    .pdtName(productEntity.getPdtName())
                    .price(productEntity.getPrice())
                    .imageUrl(pdtUrls)
                    .build());
        }
        response.put("totalPages", purchaseEntities.getTotalPages());
        response.put("products", productResDtos);
        return response;
    }

    public Map<String, Object> getWishies(String email, int page, int size) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다.") );
        PageRequest pageable = PageRequest.of(page, size);
        Page<WishEntity> wishEntities = wishRepository.findAllByUserEntity(userEntity, pageable);

        List<ProductResDto> productResDtos = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        for(WishEntity wishEntity : wishEntities) {
            ProductEntity productEntity = wishEntity.getProductEntity();

            // 이미지 url
            List<PdtUrlEntity> pdtUrlEntities = pdtUrlRepository.findAllByProductEntity(productEntity);
            List<String> pdtUrls = new ArrayList<>();
            for(PdtUrlEntity pdtUrlEntity : pdtUrlEntities) {
                pdtUrls.add(pdtUrlEntity.getUrl());
            }
            productResDtos.add(ProductResDto.builder()
                    .pdtId(productEntity.getPdtId())
                    .pdtName(productEntity.getPdtName())
                    .price(productEntity.getPrice())
                            .imageUrl(pdtUrls)
                    .build());
        }
        response.put("totalPages", wishEntities.getTotalPages());
        response.put("products", productResDtos);
        return response;
    }

}
