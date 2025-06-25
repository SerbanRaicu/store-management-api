package com.ing.store_management.service;

import com.ing.store_management.dto.ProductDto;
import com.ing.store_management.exception.DuplicateProductException;
import com.ing.store_management.exception.ProductNotFoundException;
import com.ing.store_management.model.Product;
import com.ing.store_management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setCategory("Electronics");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productDto = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("Electronics")
                .build();
    }

    @Test
    void createProduct_Success() {
        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.createProduct(productDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("99.99"));
        verify(productRepository).findByNameIgnoreCase("Test Product");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_DuplicateName_ThrowsException() {
        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.createProduct(productDto))
                .isInstanceOf(DuplicateProductException.class)
                .hasMessage("Product with name 'Test Product' already exists");

        verify(productRepository).findByNameIgnoreCase("Test Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void findProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto result = productService.findProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(1L);
    }

    @Test
    void findProductById_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findProductById(1L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with ID: 1");

        verify(productRepository).findById(1L);
    }

    @Test
    void findAllProducts_Success() {
        List<Product> products = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductDto> result = productService.findAllProducts(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).findAll(pageable);
    }

    @Test
    void findAvailableProducts_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAvailableProducts()).thenReturn(products);

        List<ProductDto> result = productService.findAvailableProducts();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).findAvailableProducts();
    }

    @Test
    void findProductsByName_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(products);

        List<ProductDto> result = productService.findProductsByName("Test");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void findProductsByCategory_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByCategory("Electronics")).thenReturn(products);

        List<ProductDto> result = productService.findProductsByCategory("Electronics");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Electronics");
        verify(productRepository).findByCategory("Electronics");
    }

    @Test
    void updateProduct_Success() {
        ProductDto updateDto = ProductDto.builder()
                .name("Updated Product")
                .price(new BigDecimal("149.99"))
                .quantity(20)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.updateProduct(1L, updateDto);

        assertThat(result).isNotNull();
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(1L, productDto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with ID: 1");

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_PartialUpdate_Success() {
        ProductDto updateDto = ProductDto.builder()
                .name("  ")
                .price(new BigDecimal("199.99"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateProduct(1L, updateDto);

        verify(productRepository).save(argThat(p ->
                p.getName().equals("Test Product") && // Name not changed
                        p.getPrice().equals(new BigDecimal("199.99")) // Price updated
        ));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with ID: 1");

        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}

