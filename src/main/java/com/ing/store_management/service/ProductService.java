package com.ing.store_management.service;

import com.ing.store_management.dto.ProductDto;
import com.ing.store_management.exception.DuplicateProductException;
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
        log.info("Creating new product: {}", productDto.getName());

        if (productRepository.findByNameIgnoreCase(productDto.getName()).isPresent()) {
            log.error("Product with name '{}' already exists", productDto.getName());
            throw new DuplicateProductException("Product with name '" + productDto.getName() + "' already exists");
        }

        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return mapToDTO(savedProduct);
    }

    public ProductDto findProductById(Long id) {
        log.info("Finding product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });

        return mapToDTO(product);
    }

    public Page<ProductDto> findAllProducts(Pageable pageable) {
        log.info("Finding all products with pagination");

        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToDTO);
    }

    public List<ProductDto> findAvailableProducts() {
        log.info("Finding all available products (quantity > 0)");

        List<Product> products = productRepository.findAvailableProducts();
        log.info("Found {} available products", products.size());

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ProductDto> findProductsByName(String name) {
        log.info("Searching products by name containing: {}", name);

        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        log.info("Found {} products matching name: {}", products.size(), name);

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ProductDto> findProductsByCategory(String category) {
        log.info("Finding products by category: {}", category);

        List<Product> products = productRepository.findByCategory(category);
        log.info("Found {} products in category: {}", products.size(), category);

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        log.info("Updating product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new ProductNotFoundException("Product not found with ID: " + productId);
                });

        String oldName = product.getName();
        String oldDescription = product.getDescription();
        BigDecimal oldPrice = product.getPrice();
        Integer oldQuantity = product.getQuantity();
        String oldCategory = product.getCategory();

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

        log.info("Product '{}' updated successfully. Changes: name: {} -> {}, description: {} -> {}, price: {} -> {}, quantity: {} -> {}, category: {} -> {}",
                product.getName(),
                oldName, product.getName(),
                oldDescription, product.getDescription(),
                oldPrice, product.getPrice(),
                oldQuantity, product.getQuantity(),
                oldCategory, product.getCategory());

        return mapToDTO(updatedProduct);
    }

    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.error("Product not found with ID: {}", productId);
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }

        productRepository.deleteById(productId);
        log.info("Product deleted successfully with ID: {}", productId);
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
