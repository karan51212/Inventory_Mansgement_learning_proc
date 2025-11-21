package com.example.project.service;

import com.example.project.entity.Product;
import com.example.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Create a new product
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Product with SKU " + product.getSku() + " already exists");
        }
        return productRepository.save(product);
    }
    
    // Get all products with pagination
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }
    
    // Get all products without pagination
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Get product by SKU
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }
    
    // Update product
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Check if SKU is being changed and if new SKU already exists
        if (!product.getSku().equals(productDetails.getSku()) && 
            productRepository.existsBySku(productDetails.getSku())) {
            throw new RuntimeException("Product with SKU " + productDetails.getSku() + " already exists");
        }
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setSku(productDetails.getSku());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setMinQuantity(productDetails.getMinQuantity());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        product.setUnit(productDetails.getUnit());
        product.setIsActive(productDetails.getIsActive());
        
        return productRepository.save(product);
    }
    
    // Delete product (soft delete)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    // Hard delete product
    public void hardDeleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    // Search products
    public Page<Product> searchProducts(String name, String category, String brand, 
                                     BigDecimal minPrice, BigDecimal maxPrice, 
                                     Integer minQuantity, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchProducts(name, category, brand, minPrice, maxPrice, minQuantity, pageable);
    }
    
    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // Get products by brand
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }
    
    // Get low stock products
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }
    
    // Get out of stock products
    public List<Product> getOutOfStockProducts() {
        return productRepository.findByQuantity(0);
    }
    
    // Get active products
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }
    
    // Update product quantity (for stock management)
    public Product updateProductQuantity(Long id, Integer newQuantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
        
        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }
    
    // Add stock to product
    public Product addStock(Long id, Integer quantityToAdd) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (quantityToAdd < 0) {
            throw new RuntimeException("Quantity to add cannot be negative");
        }
        
        product.setQuantity(product.getQuantity() + quantityToAdd);
        return productRepository.save(product);
    }
    
    // Remove stock from product
    public Product removeStock(Long id, Integer quantityToRemove) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (quantityToRemove < 0) {
            throw new RuntimeException("Quantity to remove cannot be negative");
        }
        
        if (product.getQuantity() < quantityToRemove) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getQuantity());
        }
        
        product.setQuantity(product.getQuantity() - quantityToRemove);
        return productRepository.save(product);
    }
    
    // Get total inventory value
    public BigDecimal getTotalInventoryValue() {
        BigDecimal totalValue = productRepository.getTotalInventoryValue();
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }
    
    // Get product count by category
    public List<Object[]> getProductCountByCategory() {
        return productRepository.countProductsByCategory();
    }
    
    // Check if product exists by SKU
    public boolean productExistsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }
    
    // Get products by name containing
    public List<Product> getProductsByNameContaining(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
