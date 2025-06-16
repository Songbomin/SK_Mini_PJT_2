package com.example.mypageservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SendUserDto {
    String email;

    @Builder
    public SendUserDto(String email) {
        this.email = email;
    }
}
