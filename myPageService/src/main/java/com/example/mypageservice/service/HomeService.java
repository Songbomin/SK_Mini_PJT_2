package com.example.mypageservice.service;

import com.example.mypageservice.dto.ProductResDto;
import com.example.mypageservice.entity.PdtUrlEntity;
import com.example.mypageservice.entity.ProductEntity;
import com.example.mypageservice.repository.PdtUrlRepository;
import com.example.mypageservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PdtUrlRepository pdtUrlRepository;

    public Map<String, Object> getAllPdts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductEntity> productEntities = productRepository.findAllByOrderByIdDesc(pageable);

        List<ProductResDto> productResDtos = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        for (ProductEntity productEntity : productEntities) {
            List<PdtUrlEntity> pdtUrlEntities = pdtUrlRepository.findAllByProductEntity(productEntity);
            List<String> urls = new ArrayList<>();
            for(PdtUrlEntity pdtUrlEntity : pdtUrlEntities){
                urls.add(pdtUrlEntity.getUrl());
            }
            productResDtos.add(ProductResDto.builder()
                            .pdtId(productEntity.getPdtId())
                            .price(productEntity.getPrice())
                            .pdtName(productEntity.getPdtName())
                            .imageUrl(urls)
                    .build());
        }
        response.put("totalPages", productEntities.getTotalPages());
        response.put("products", productResDtos);
        return response;
    }

    public Map<String, Object> searchPdts(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductEntity> productEntities = productRepository.findByKeywordByIdDesc(keyword, pageable);

        List<ProductResDto> productResDtos = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        for (ProductEntity productEntity : productEntities) {
            List<PdtUrlEntity> pdtUrlEntities = pdtUrlRepository.findAllByProductEntity(productEntity);
            List<String> urls = new ArrayList<>();
            for(PdtUrlEntity pdtUrlEntity : pdtUrlEntities){
                urls.add(pdtUrlEntity.getUrl());
            }
            productResDtos.add(ProductResDto.builder()
                    .pdtId(productEntity.getPdtId())
                    .price(productEntity.getPrice())
                    .pdtName(productEntity.getPdtName())
                    .imageUrl(urls)
                    .build());
        }
        response.put("totalPages", productEntities.getTotalPages());
        response.put("products", productResDtos);
        return response;
    }

    public Map<String, Object> pdtCategory(String category, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductEntity> productEntities = productRepository.findByDtypeOrderByIdDesc(category, pageable);

        List<ProductResDto> productResDtos = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        for (ProductEntity productEntity : productEntities) {
            List<PdtUrlEntity> pdtUrlEntities = pdtUrlRepository.findAllByProductEntity(productEntity);
            List<String> urls = new ArrayList<>();
            for(PdtUrlEntity pdtUrlEntity : pdtUrlEntities){
                urls.add(pdtUrlEntity.getUrl());
            }
            productResDtos.add(ProductResDto.builder()
                    .pdtId(productEntity.getPdtId())
                    .price(productEntity.getPrice())
                    .pdtName(productEntity.getPdtName())
                    .imageUrl(urls)
                    .build());
        }
        response.put("totalPages", productEntities.getTotalPages());
        response.put("products", productResDtos);
        return response;
    }
}
