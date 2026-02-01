//package com.example.moment_forever.admin.controller;
//
//import com.example.demo.admin.services.CategoryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/categories")
//public class AdminCategoryController {
//
//    private final CategoryService categoryService;
//
//    public AdminCategoryController(@Autowired CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }
//
////    @PostMapping
////    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
////            @Valid @RequestBody CategoryDto categoryDto) {
////        CategoryResponse response = categoryService.createCategory(categoryDto);
////        return ResponseEntity
////                .status(HttpStatus.CREATED)
////                .body(ApiResponse.success("Category created successfully", response));
////    }
////
////    @GetMapping("/{id}")
////    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
////        CategoryResponse response = categoryService.getCategoryById(id);
////        return ResponseEntity.ok(ApiResponse.success(response));
////    }
//}