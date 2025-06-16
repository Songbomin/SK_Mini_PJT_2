package com.example.mypageservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PdtUrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ProductEntity productEntity;

    private String url;

    @Builder
    public PdtUrlEntity(ProductEntity productEntity, String url) {
        this.productEntity = productEntity;
        this.url = url;
    }

}
