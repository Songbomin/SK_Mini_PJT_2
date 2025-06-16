package com.example.mypageservice.dto;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangeUserDto {
    String prevEmail;
    String newEmail;

    @Builder
    public ChangeUserDto(String prevEmail, String newEmail) {
        this.prevEmail = prevEmail;
        this.newEmail = newEmail;
    }
}
