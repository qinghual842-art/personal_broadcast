package com.blog.module.article.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.module.article.dto.ArticleQueryDTO;
import com.blog.module.article.dto.ArticleSaveDTO;
import com.blog.module.article.service.ArticleService;
import com.blog.module.article.vo.ArticleDetailVO;
import com.blog.module.article.vo.ArticleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/articles")
@RequiredArgsConstructor
public class ArticleAdminController {

    private final ArticleService articleService;

    @GetMapping
    public Result<PageResult<ArticleVO>> list(ArticleQueryDTO query) {
        return Result.success(articleService.adminPageList(query));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody ArticleSaveDTO dto) {
        articleService.create(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getForEdit(@PathVariable Long id) {
        return Result.success(articleService.getForEdit(id));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ArticleSaveDTO dto) {
        articleService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        articleService.batchDelete(ids);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        articleService.updateStatus(id, status);
        return Result.success();
    }
}
