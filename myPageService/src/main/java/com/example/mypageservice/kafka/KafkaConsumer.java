package com.example.mypageservice.kafka;

import com.example.mypageservice.dto.ChangeUserDto;
import com.example.mypageservice.dto.ProductReqDto;
import com.example.mypageservice.dto.SendPdtDto;
import com.example.mypageservice.dto.SendUserDto;
import com.example.mypageservice.service.MyPageService;
import com.example.mypageservice.service.ProductService;
import com.example.mypageservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MyPageService myPageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    // 유저 회원가입
    @KafkaListener(topics = "user-signup", groupId = "team5")
    public void userSignUp(String message) {
        try{
            SendUserDto sendUserDto = objectMapper.readValue(message, SendUserDto.class);
            logger.info("유저 회원가입 정보: " + sendUserDto.toString());
            userService.createUser(sendUserDto);
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 유저 회원 탈퇴 -> 유저 이메일을 받아 -> 유저 엔티티를 통해 유저 삭제, 카사드 테이블 컬럼들 삭제
    @KafkaListener(topics = "user-delete", groupId = "team5")
    public void userUpdate(String message) {
        try{
            SendUserDto sendUserDto = objectMapper.readValue(message, SendUserDto.class);
            logger.info("유저 정보: " + sendUserDto.toString());
            userService.deleteUser(sendUserDto);
            logger.info("유저 탈퇴");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 유저가 이메일 변경 -> 기존 이메일로 엔티티 찾고 -> 해당 엔티티의 이메일을 새로운 이메일로 업데이트
    @KafkaListener(topics = "user-update", groupId = "team5")
    public void userDelete(String message) {
        try{
            ChangeUserDto changeUserDto = objectMapper.readValue(message, ChangeUserDto.class);
            logger.info("유저 정보: " +changeUserDto.toString());
            userService.updateUser(changeUserDto);
            logger.info("유저 업데이트 성공");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // 유저가 상품 등록 -> 유저 이메일과 상품 디티오를 통해서 유저, 상품 엔티티 뽑아서 세일즈, 상품 엔티티 생성
    // 파라미터 : 유저 id, 상품 디티오  생성 : 세일즈 엔티티, 상품 엔티티
    @KafkaListener(topics = "pdt-create", groupId = "team5")
    public void pdtCreate(String message) {
        try{
            SendPdtDto sendPdtDto = objectMapper.readValue(message, SendPdtDto.class);
            logger.info("상품 정보: " + sendPdtDto.toString());
            productService.createPdt(sendPdtDto);
            logger.info("상품 등록 완료");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 유저가 상품 삭제 -> 상품 아이디를 전달 받아 해당하는 상품 엔티티 삭제 -> 세일즈 엔티티도 카사드로 삭제
    @KafkaListener(topics = "pdt-delete", groupId = "team5")
    public void pdtDelete(String message) {
        try{
            ProductReqDto productReqDto = objectMapper.readValue(message, ProductReqDto.class);
            logger.info("상품 정보: " + productReqDto.toString());
            productService.deletePdt(productReqDto);
            logger.info("상품 삭제 완료");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // 유저가 찜 -> 유저 이메일과 상품 아이디를 전달 -> 위시 엔티티 생성
    // 유저 이메일과 상품 아이디를 통해서 각각 유저 엔티티랑 상품 엔티티를 생성 후, 이를 통해 위시 엔티티 생성
    // 파라미터 : 유저 이메일, 상품 id -> 셍상 : 위시 엔티티
    @KafkaListener(topics = "wish-pdt", groupId = "team5")
    public void wishPdt(String message) {
        try{
            ProductReqDto productReqDto = objectMapper.readValue(message, ProductReqDto.class);
            logger.info("상품 정보: " + productReqDto.toString());
            productService.createWish(productReqDto);
            logger.info("찜 등록 완료");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 유저가 찜 취소 -> 유저 이메일과 상품 아이디를 전달 -> 위시 레포지를 통해 해당하는 위시 엔티티 찾아서 삭제
    @KafkaListener(topics = "wish-pdt-delete", groupId = "team5")
    public void wishPdtDelete(String message) {
        try{
            ProductReqDto productReqDto = objectMapper.readValue(message, ProductReqDto.class);
            logger.info("상품 정보: " + productReqDto.toString());
            productService.deleteWish(productReqDto);
            logger.info("찜 삭제 완료");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // 유저가 상품을 구매 -> 유저 이메일과 상품 아이디를 전달 -> 싱품 엔티티의 exist를 false로 하고,
    // 상품 엔티티와 유저 엔티티를 통해 purchase 엔티티 생성 후 저장
    @KafkaListener(topics = "pdt-purchase", groupId = "team5")
    public void pdtPurchase(String message) {
        try{
            ProductReqDto productReqDto = objectMapper.readValue(message, ProductReqDto.class);
            logger.info("상품 정보: " + productReqDto.toString());
            productService.purchase(productReqDto);
            logger.info("물품 구매 완료");
        } catch (Exception e) {
            logger.error("카프카 메세지 프로세싱 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
