package com.example.project.controller;

import com.example.project.service.ProductService;
import com.example.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class MainController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Home page
    @GetMapping("/")
    public String home(Model model) {
        // Get dashboard statistics
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts().size());
        model.addAttribute("outOfStockProducts", productService.getOutOfStockProducts().size());
        model.addAttribute("totalInventoryValue", productService.getTotalInventoryValue());
        model.addAttribute("categoryStats", productService.getProductCountByCategory());
        model.addAttribute("recentProducts", productService.getAllProducts().subList(0, Math.min(5, productService.getAllProducts().size())));
        
        return "index";
    }
    
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts().size());
        model.addAttribute("outOfStockProducts", productService.getOutOfStockProducts().size());
        model.addAttribute("totalInventoryValue", productService.getTotalInventoryValue());
        model.addAttribute("categoryStats", productService.getProductCountByCategory());
        model.addAttribute("totalCategories", categoryService.getCategoryCount());
        
        return "redirect:/";
    }
}
