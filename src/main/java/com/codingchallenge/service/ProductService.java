package com.codingchallenge.service;

import com.codingchallenge.model.Product;
import com.codingchallenge.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found")); // TODO: handle this exception properly
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product dbProduct, Product product) {
        dbProduct.setProductName(product.getProductName());
        dbProduct.setProductCategory(product.getProductCategory());
        dbProduct.setBrand(product.getBrand());
        dbProduct.setPackageQuantity(product.getPackageQuantity());
        dbProduct.setPackageUnit(product.getPackageUnit());
        return productRepository.save(dbProduct);
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }
}
