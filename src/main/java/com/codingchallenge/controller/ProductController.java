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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
        List<Product> products = productService.getAllProducts();

        return ResponseEntity.ok(productMapper.toGetProductDtos(products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GetProductDto> getProductById(
            @PathVariable
            @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
            String productId
    ) {
        Product product = productService.getProductById(productId);

        return ResponseEntity.ok(productMapper.toGetProductDto(product));
    }

    @PostMapping
    public ResponseEntity<GetProductDto> createProduct(
            @RequestBody
            @Valid
            CreateProductDto createProductDto
    ) {
        Product product = productMapper.toProduct(createProductDto);
        Product createdProduct = productService.createProduct(product);

        return ResponseEntity.created(ServletUriComponentsBuilder
                                          .fromCurrentRequest()
                                          .path("/{productId}")
                                          .buildAndExpand(createdProduct.getProductId())
                                          .toUri())
                .body(productMapper.toGetProductDto(createdProduct));
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
        Product incomingProduct = productMapper.toProduct(createProductDto);
        Product dbProduct = productService.getProductById(productId);
        Product updatedProduct = productService.updateProduct(dbProduct, incomingProduct);

        return ResponseEntity.ok(productMapper.toGetProductDto(updatedProduct));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable
            @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
            String productId
    ) {
        Product product = productService.getProductById(productId);
        productService.deleteProduct(product);

        return ResponseEntity.noContent().build();
    }
}
