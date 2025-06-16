package com.example.mypageservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductReqDto {
    private String email;
    private long pdtId;

    @Builder
    public ProductReqDto(String email, long ptId) {
        this.email = email;
        this.pdtId = ptId;
    }
}
