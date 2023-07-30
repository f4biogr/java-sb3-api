package com.example.api.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.api.dtos.ProductRecordDto;
import com.example.api.models.ProductModel;
import com.example.api.repositories.ProductRepository;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
  final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @PostMapping("/products")
  public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto product) {
    var productModel = new ProductModel();
    BeanUtils.copyProperties(product, productModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
  }

  @GetMapping("/products")
  public ResponseEntity<Page<ProductModel>> getAllProducts(
      @PageableDefault(size = 5) Pageable pageable) {
    Page<ProductModel> products = productRepository.findAll(pageable);
    if (!products.isEmpty()) {
      for (ProductModel product : products) {
        UUID id = product.getId();
        product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
      }
    }
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<EntityModel<ProductModel>> getOneProduct(
      @PathVariable(value = "id") UUID id) {
    Optional<ProductModel> productOptional = productRepository.findById(id);
    if (productOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    EntityModel<ProductModel> entityModel = EntityModel.of(productOptional.get());
    Link selfLink = linkTo(ProductController.class).slash("products").slash(id).withSelfRel();
    Link productListLink =
        linkTo(ProductController.class).slash("products").withRel("Products List");
    entityModel.add(selfLink, productListLink);

    return ResponseEntity.status(HttpStatus.OK).body(entityModel);
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<Object> updateProduct(
      @PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {
    Optional<ProductModel> productOptional = productRepository.findById(id);
    if (productOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    var productModel = productOptional.get();
    BeanUtils.copyProperties(productRecordDto, productModel);
    return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
    Optional<ProductModel> producOptional = productRepository.findById(id);
    if (producOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }
    productRepository.delete(producOptional.get());
    return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
  }
}
