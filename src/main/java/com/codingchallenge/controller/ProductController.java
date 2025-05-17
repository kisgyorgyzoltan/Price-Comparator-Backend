package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.CreateProductDto;
import com.codingchallenge.dto.outgoing.GetProductDto;
import com.codingchallenge.mapper.ProductMapper;
import com.codingchallenge.model.Product;
import com.codingchallenge.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService productService;

    private final ProductMapper productMapper;

    public ProductController(ProductService productService,
                             ProductMapper productMapper
    ) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetProductDto>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(productMapper.toGetProductDtos(products));
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch products: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GetProductDto> getProductById(
            @PathVariable
            @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
            String productId
    ) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(productMapper.toGetProductDto(product));
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch product with ID {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<GetProductDto> createProduct(
            @RequestBody
            @Valid
            CreateProductDto createProductDto
    ) {
        try {
            Product product = productMapper.toProduct(createProductDto);
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.ok(productMapper.toGetProductDto(createdProduct));
        } catch (IllegalArgumentException e) {
            log.error("Failed to create product: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<GetProductDto> updateProduct(
            @PathVariable
            @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
            String productId,
            @RequestBody
            @Valid
            CreateProductDto createProductDto
    ) {
        try {
            Product incomingProduct = productMapper.toProduct(createProductDto);
            Product dbProduct = productService.getProductById(productId);
            Product updatedProduct = productService.updateProduct(dbProduct, incomingProduct);
            return ResponseEntity.ok(productMapper.toGetProductDto(updatedProduct));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update product with ID {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable
            @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
            String productId
    ) {
        try {
            Product product = productService.getProductById(productId);
            productService.deleteProduct(product);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete product with ID {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
