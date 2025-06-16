package com.example.mypageservice.service;

import com.example.mypageservice.dto.ChangeUserDto;
import com.example.mypageservice.dto.SendUserDto;
import com.example.mypageservice.entity.UserEntity;
import com.example.mypageservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public void createUser(SendUserDto sendUserDto) {
        userRepository.save(UserEntity.builder().email(sendUserDto.getEmail()).build());
    }

    public void deleteUser(SendUserDto sendUserDto) {
        UserEntity userEntity = userRepository.findByEmail(sendUserDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다.") );
        userRepository.delete(userEntity);
    }

    public void updateUser(ChangeUserDto changeUserDto) {
        UserEntity userEntity = userRepository.findByEmail(changeUserDto.getPrevEmail()).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다.") );
        userEntity.setEmail(changeUserDto.getNewEmail());
        userRepository.save(userEntity);
    }
}
