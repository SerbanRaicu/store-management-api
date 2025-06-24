package com.ing.store_management.service;

import com.ing.store_management.dto.ProductDto;
import com.ing.store_management.exception.ProductNotFoundException;
import com.ing.store_management.model.Product;
import com.ing.store_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDto createProduct(ProductDto productDto) {

        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);

        return mapToDTO(savedProduct);
    }

    public Page<ProductDto> findAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToDTO);
    }

    public List<ProductDto> findAvailableProducts() {
        List<Product> products = productRepository.findAvailableProducts();

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProductDto findProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        return mapToDTO(product);
    }

    public List<ProductDto> findProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ProductDto> findProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProductDto updateProduct(Long productId, ProductDto productDto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (productDto.getName() != null && !productDto.getName().trim().isEmpty()) {
            product.setName(productDto.getName());
        }

        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }

        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }

        if (productDto.getQuantity() != null) {
            product.setQuantity(productDto.getQuantity());
        }

        if (productDto.getCategory() != null && !productDto.getCategory().trim().isEmpty()) {
            product.setCategory(productDto.getCategory());
        }

        Product updatedProduct = productRepository.save(product);

        return mapToDTO(updatedProduct);
    }

    public void deleteProduct(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }

        productRepository.deleteById(productId);
    }


    private Product mapToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setCategory(productDto.getCategory());
        return product;
    }

    private ProductDto mapToDTO(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
