package com.example.mypageservice.controller;


import com.example.mypageservice.dto.ProductResDto;
import com.example.mypageservice.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/myPage")
public class MyPageController {
    @Autowired
    private MyPageService myPageService;


    @GetMapping("/mySale")
    public ResponseEntity<?> mySale(@RequestHeader("X-Auth-User") String email, @RequestParam("page") int page, @RequestParam("size") int size) {
        try{
            Map<String, Object> response = myPageService.getSales(email, page, size);
            return ResponseEntity.ok(Map.of("totalPages", response.get("totalPages"),"sales",response.get("products")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 에러: " + e.getMessage());
        }

    }

    @GetMapping("/myPurchase")
    public ResponseEntity<?> myPurchase(@RequestHeader("X-Auth-User") String email, @RequestParam("page") int page, @RequestParam("size") int size) {
        try{
            Map<String, Object> response = myPageService.getPurchases(email, page, size);
            return ResponseEntity.ok(Map.of("totalPages", response.get("totalPages"),"purchases",response.get("products")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 에러: " + e.getMessage());
        }
    }

    @GetMapping("/wishList")
    public ResponseEntity<?> wishList(@RequestHeader("X-Auth-User") String email, @RequestParam("page") int page, @RequestParam("size") int size) {
        try{
            Map<String, Object> response = myPageService.getWishies(email, page, size);
            return ResponseEntity.ok(Map.of("totalPages", response.get("totalPages"),"wishlist",response.get("products")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 에러: " + e.getMessage());
        }
    }
}
