package com.example.project.controller;

import com.example.project.entity.Product;
import com.example.project.service.ProductService;
import com.example.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Display all products with pagination
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        Page<Product> productPage = productService.getAllProducts(page, size, sortBy, sortDir);
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("categories", categoryService.getActiveCategories());
        
        return "products/list";
    }
    
    // Display product creation form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "products/form";
    }
    
    // Create new product
    @PostMapping
    public String createProduct(@Valid @ModelAttribute Product product, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "products/form";
        }
        
        try {
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product created successfully!");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "products/form";
        }
    }
    
    // Display product edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "products/form";
    }
    
    // Update product
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, 
                              @Valid @ModelAttribute Product product, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "products/form";
        }
        
        try {
            productService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "products/form";
        }
    }
    
    // Delete product
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
    
    // View product details
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        model.addAttribute("product", product);
        return "products/view";
    }
    
    // Search products
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Product> productPage = productService.searchProducts(
            name, category, brand, minPrice, maxPrice, minQuantity, page, size);
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("categories", categoryService.getActiveCategories());
        
        // Add search parameters to model for form persistence
        model.addAttribute("searchName", name);
        model.addAttribute("searchCategory", category);
        model.addAttribute("searchBrand", brand);
        model.addAttribute("searchMinPrice", minPrice);
        model.addAttribute("searchMaxPrice", maxPrice);
        model.addAttribute("searchMinQuantity", minQuantity);
        
        return "products/search";
    }
    
    // Low stock products
    @GetMapping("/low-stock")
    public String lowStockProducts(Model model) {
        List<Product> lowStockProducts = productService.getLowStockProducts();
        model.addAttribute("products", lowStockProducts);
        model.addAttribute("title", "Low Stock Products");
        return "products/low-stock";
    }
    
    // Out of stock products
    @GetMapping("/out-of-stock")
    public String outOfStockProducts(Model model) {
        List<Product> outOfStockProducts = productService.getOutOfStockProducts();
        model.addAttribute("products", outOfStockProducts);
        model.addAttribute("title", "Out of Stock Products");
        return "products/out-of-stock";
    }
    
    // Stock management
    @GetMapping("/{id}/stock")
    public String showStockManagement(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        model.addAttribute("product", product);
        return "products/stock";
    }
    
    // Add stock
    @PostMapping("/{id}/add-stock")
    public String addStock(@PathVariable Long id, 
                          @RequestParam Integer quantity,
                          RedirectAttributes redirectAttributes) {
        try {
            productService.addStock(id, quantity);
            redirectAttributes.addFlashAttribute("success", 
                "Added " + quantity + " units to stock successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products/" + id + "/stock";
    }
    
    // Remove stock
    @PostMapping("/{id}/remove-stock")
    public String removeStock(@PathVariable Long id, 
                             @RequestParam Integer quantity,
                             RedirectAttributes redirectAttributes) {
        try {
            productService.removeStock(id, quantity);
            redirectAttributes.addFlashAttribute("success", 
                "Removed " + quantity + " units from stock successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products/" + id + "/stock";
    }
    
    // Dashboard (optional)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts().size());
        model.addAttribute("outOfStockProducts", productService.getOutOfStockProducts().size());
        model.addAttribute("totalInventoryValue", productService.getTotalInventoryValue());
        model.addAttribute("categoryStats", productService.getProductCountByCategory());
        
        return "index";
    }
}
