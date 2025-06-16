package com.example.mypageservice.repository;

import com.example.mypageservice.entity.ProductEntity;
import com.example.mypageservice.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByPdtId(long pdtId);


    Page<ProductEntity> findAllByOrderByIdDesc(Pageable pageable);

    @Query( "SELECT p " +
            "FROM ProductEntity p " +
            "WHERE p.pdtName LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY p.id DESC ")
    Page<ProductEntity> findByKeywordByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    Page<ProductEntity> findByUserEntity(UserEntity userEntity, Pageable pageable);

    Page<ProductEntity> findByDtypeOrderByIdDesc(String dtype, Pageable pageable);
}
