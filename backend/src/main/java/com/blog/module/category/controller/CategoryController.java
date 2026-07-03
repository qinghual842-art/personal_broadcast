package com.blog.module.category.controller;

import com.blog.common.Result;
import com.blog.module.category.dto.CategorySaveDTO;
import com.blog.module.category.service.CategoryService;
import com.blog.module.category.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public Result<List<CategoryVO>> listAll() {
        return Result.success(categoryService.listAll());
    }

    @GetMapping("/admin/categories")
    public Result<List<CategoryVO>> listAllAdmin() {
        return Result.success(categoryService.listAllAdmin());
    }

    @PostMapping("/admin/categories")
    public Result<?> create(@Valid @RequestBody CategorySaveDTO dto) {
        categoryService.create(dto);
        return Result.success();
    }

    @PutMapping("/admin/categories/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody CategorySaveDTO dto) {
        categoryService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/admin/categories/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
