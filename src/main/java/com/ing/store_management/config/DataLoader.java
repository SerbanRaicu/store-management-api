package com.ing.store_management.config;

import com.ing.store_management.model.Product;
import com.ing.store_management.model.User;
import com.ing.store_management.repository.ProductRepository;
import com.ing.store_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadInitialData();
    }

    private void loadInitialData() {
        createDefaultUsers();
        createSampleProducts();
    }

    private void createDefaultUsers() {
        if (userRepository.count() == 0) {

            // Create Admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@store.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);

            // Create Manager user
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@store.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setFirstName("Store");
            manager.setLastName("Manager");
            manager.setRole(User.Role.MANAGER);
            manager.setEnabled(true);
            userRepository.save(manager);

            // Create Employee user
            User employee = new User();
            employee.setUsername("employee");
            employee.setEmail("employee@store.com");
            employee.setPassword(passwordEncoder.encode("employee123"));
            employee.setFirstName("Store");
            employee.setLastName("Employee");
            employee.setRole(User.Role.EMPLOYEE);
            employee.setEnabled(true);
            userRepository.save(employee);

        } else {
            log.info("Users already exist, skipping user creation");
        }
    }

    private void createSampleProducts() {
        if (productRepository.count() == 0) {
            List<Product> sampleProducts = List.of(
                    createProduct("MacBook Pro 16", "High-performance laptop with M2 chip",
                            new BigDecimal("2499.99"), 15, "Electronics"),

                    createProduct("iPhone 15 Pro", "Latest smartphone with advanced camera system",
                            new BigDecimal("999.99"), 25, "Electronics"),

                    createProduct("Samsung 55 OLED TV", "4K Ultra HD Smart TV with HDR",
                            new BigDecimal("1299.99"), 8, "Electronics"),

                    createProduct("Nike Air Max 270", "Comfortable running shoes",
                            new BigDecimal("129.99"), 50, "Footwear"),

                    createProduct("Adidas Ultraboost 22", "Premium running shoes with Boost technology",
                            new BigDecimal("189.99"), 30, "Footwear"),

                    createProduct("The Great Gatsby", "Classic American novel by F. Scott Fitzgerald",
                            new BigDecimal("12.99"), 100, "Books"),

                    createProduct("Clean Code", "A handbook of agile software craftsmanship",
                            new BigDecimal("39.99"), 75, "Books"),

                    createProduct("Wireless Bluetooth Headphones", "Noise-cancelling over-ear headphones",
                            new BigDecimal("199.99"), 40, "Electronics"),

                    createProduct("Gaming Mechanical Keyboard", "RGB backlit mechanical keyboard",
                            new BigDecimal("149.99"), 20, "Electronics"),

                    createProduct("Premium Coffee Blend", "Arabica coffee beans from Ethiopia",
                            new BigDecimal("24.99"), 60, "Food & Beverages"),

                    createProduct("Yoga Mat", "Non-slip exercise mat for yoga and fitness",
                            new BigDecimal("29.99"), 35, "Sports"),

                    createProduct("Stainless Steel Water Bottle", "Insulated water bottle - 750ml",
                            new BigDecimal("19.99"), 80, "Sports"),

                    createProduct("Winter Jacket", "Waterproof and breathable winter jacket",
                            new BigDecimal("89.99"), 25, "Clothing"),

                    createProduct("Office Chair", "Ergonomic office chair with lumbar support",
                            new BigDecimal("299.99"), 12, "Furniture"),

                    createProduct("Desk Lamp", "LED desk lamp with adjustable brightness",
                            new BigDecimal("49.99"), 18, "Furniture")
            );

            productRepository.saveAll(sampleProducts);
        } else {
            log.info("Products already exist, skipping product creation");
        }
    }

    private Product createProduct(String name, String description, BigDecimal price,
                                  Integer quantity, String category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        return product;
    }
}
