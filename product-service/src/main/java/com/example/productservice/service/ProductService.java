package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest payload) {
        Product productModel = Product.builder().
                name(payload.getName()).
                description(payload.getDescription()).
                price(payload.getPrice()).
                build();

        productRepository.save(productModel);
        log.info("Product {} has been saved", productModel.getId());
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder().
                id(product.getId()).
                name(product.getName()).
                description(product.getDescription()).
                price(product.getPrice()).build();
    }
}
