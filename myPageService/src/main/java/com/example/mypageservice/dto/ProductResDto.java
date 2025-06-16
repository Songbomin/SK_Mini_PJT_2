package com.example.mypageservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ProductResDto {
    // 사진 불러오는 용
    private long pdtId;

    private String pdtName;

    private Float price;

    private List<String> imageUrl;

    @Builder
    public ProductResDto(long pdtId, String pdtName, Float price, List<String> imageUrl) {
        this.pdtId = pdtId;
        this.pdtName = pdtName;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
