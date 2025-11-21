package com.example.project.repository;

import com.example.project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find by SKU
    Optional<Product> findBySku(String sku);
    
    // Find by name containing (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find by category
    List<Product> findByCategory(String category);
    
    // Find by brand
    List<Product> findByBrand(String brand);
    
    // Find active products
    List<Product> findByIsActiveTrue();
    
    // Find products with low stock (quantity <= minQuantity)
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minQuantity AND p.minQuantity > 0")
    List<Product> findLowStockProducts();
    
    // Find products with zero stock
    List<Product> findByQuantity(Integer quantity);
    
    // Find products by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products by price less than
    List<Product> findByPriceLessThan(BigDecimal price);
    
    // Find products by price greater than
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    // Search products by multiple criteria
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:brand IS NULL OR p.brand = :brand) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minQuantity IS NULL OR p.quantity >= :minQuantity)")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minQuantity") Integer minQuantity,
            Pageable pageable
    );
    
    // Count products by category
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countProductsByCategory();
    
    // Get total inventory value
    @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
    BigDecimal getTotalInventoryValue();
    
    // Check if SKU exists
    boolean existsBySku(String sku);
    
    // Find products expiring soon (if you add expiry date later)
    // @Query("SELECT p FROM Product p WHERE p.expiryDate <= :date")
    // List<Product> findExpiringProducts(@Param("date") LocalDate date);
}
