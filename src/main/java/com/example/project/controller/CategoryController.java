package com.example.project.controller;

import com.example.project.entity.Category;
import com.example.project.service.CategoryService;
import com.example.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;
    
    // Display all categories
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        Map<Long, Integer> categoryCounts = new HashMap<>();
        categories.forEach(cat -> categoryCounts.put(cat.getId(), productService.getProductsByCategory(cat.getName()).size()));
        model.addAttribute("categories", categories);
        model.addAttribute("categoryCounts", categoryCounts);
        return "categories/list";
    }
    
    // Display category creation form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }
    
    // Create new category
    @PostMapping
    public String createCategory(@Valid @ModelAttribute Category category, 
                               BindingResult result, 
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "categories/form";
        }
        
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully!");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "categories/form";
        }
    }
    
    // Display category edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        model.addAttribute("category", category);
        return "categories/form";
    }
    
    // Update category
    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id, 
                               @Valid @ModelAttribute Category category, 
                               BindingResult result, 
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "categories/form";
        }
        
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "categories/form";
        }
    }
    
    // Delete category
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
    
    // View category details
    @GetMapping("/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        model.addAttribute("category", category);
        model.addAttribute("products", productService.getProductsByCategory(category.getName()));
        return "categories/view";
    }
}
