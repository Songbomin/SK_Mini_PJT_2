package com.example.mypageservice.service;

import com.example.mypageservice.dto.ProductReqDto;
import com.example.mypageservice.dto.SendPdtDto;
import com.example.mypageservice.entity.*;
import com.example.mypageservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PdtUrlRepository pdtUrlRepository;

    public void createPdt(SendPdtDto sendPdtDto) {
        UserEntity userEntity = userRepository.findByEmail(sendPdtDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        ProductEntity productEntity = ProductEntity.builder()
                .pdtId(sendPdtDto.getPdtId())
                .pdtName(sendPdtDto.getPdtName())
                .price(sendPdtDto.getPrice())
                .exist(true)
                .userEntity(userEntity)
                .dtype(sendPdtDto.getDtype())
                .build();
        productRepository.save(productEntity);
        if(sendPdtDto.getImageUrl() != null) {
            for(String pdturl : sendPdtDto.getImageUrl()){
                pdtUrlRepository.save(PdtUrlEntity.builder()
                        .url(pdturl)
                        .productEntity(productEntity)
                        .build());
            }
        }
    }

    public void deletePdt(ProductReqDto productReqDto) {
        ProductEntity productEntity = productRepository.findByPdtId(productReqDto.getPdtId()).orElseThrow(() -> new IllegalArgumentException("상품 정보가 없습니다."));
        productRepository.delete(productEntity);
    }

    public void createWish(ProductReqDto productReqDto) {
        UserEntity userEntity = userRepository.findByEmail(productReqDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        ProductEntity productEntity = productRepository.findByPdtId(productReqDto.getPdtId()).orElseThrow(() -> new IllegalArgumentException("상품 정보가 없습니다."));
        wishRepository.save(WishEntity.builder()
                        .productEntity(productEntity)
                        .userEntity(userEntity)
                        .build());
    }

    public void deleteWish(ProductReqDto productReqDto) {
        UserEntity userEntity = userRepository.findByEmail(productReqDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        ProductEntity productEntity = productRepository.findByPdtId(productReqDto.getPdtId()).orElseThrow(() -> new IllegalArgumentException("상품 정보가 없습니다."));
        WishEntity wishEntity = wishRepository.findByUserEntityAndProductEntity(userEntity, productEntity).orElseThrow(() -> new IllegalArgumentException("찜 정보가 없습니다."));

        wishRepository.delete(wishEntity);
    }

    public void purchase(ProductReqDto productReqDto) {
        UserEntity userEntity = userRepository.findByEmail(productReqDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        ProductEntity productEntity = productRepository.findByPdtId(productReqDto.getPdtId()).orElseThrow(() -> new IllegalArgumentException("상품 정보가 없습니다."));
        productEntity.setExist(false);
        productRepository.save(productEntity);
        purchaseRepository.save(PurchaseEntity.builder()
                        .productEntity(productEntity)
                        .userEntity(userEntity)
                        .build());
    }
}
