package com.example.project.repository;

import com.example.project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find by name
    Optional<Category> findByName(String name);
    
    // Find active categories
    List<Category> findByIsActiveTrue();
    
    // Check if name exists
    boolean existsByName(String name);
    
    // Find by name containing (case-insensitive)
    List<Category> findByNameContainingIgnoreCase(String name);
}
