package com.example.api.controllers;

import com.example.api.dtos.ProductRecordDto;
import com.example.api.models.ProductModel;
import com.example.api.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto product){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(product, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products/all")
    public ResponseEntity<Page<ProductModel>> getAllProducts(@PageableDefault(page = 0, size = 5) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll(pageable));
    }
}
