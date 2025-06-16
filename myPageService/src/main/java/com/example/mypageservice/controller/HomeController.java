package com.example.mypageservice.controller;

import com.example.mypageservice.dto.ProductResDto;
import com.example.mypageservice.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    //
    @GetMapping("/all")
    public ResponseEntity<?> all(@RequestParam("page") int page, @RequestParam("size") int size) {
        try{
            Map<String, Object> response = homeService.getAllPdts(page, size);
            return ResponseEntity.ok(Map.of("totalPages", response.get("totalPages"),"products",response.get("products")));

        }
        catch(Exception e){
            return ResponseEntity.status(500).body("서버 측 에러: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("keyword") String keyword ,@RequestParam("page") int page, @RequestParam("size") int size) {
        try{
            Map<String, Object> response = homeService.searchPdts(keyword,page, size);
            return ResponseEntity.ok(Map.of("totalPages", response.get("totalPages"),"products",response.get("products")));
        }catch(Exception e){
            return ResponseEntity.status(500).body("서버 측 에러: " + e.getMessage());
        }

    }

    @GetMapping("/category")
    public ResponseEntity<?> category(@RequestParam("category") String category, @RequestParam("page") int page, @RequestParam("size") int size) {
        try{
            Map<String, Object> response = homeService.pdtCategory(category,page, size);
            return ResponseEntity.ok(Map.of("totalPages", response.get("totalPages"),"products",response.get("products")));
        }catch(Exception e){
            return ResponseEntity.status(500).body("서버 측 에러: " + e.getMessage());
        }

    }
}
