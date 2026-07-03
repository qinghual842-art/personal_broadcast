package com.blog.module.article.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.module.article.dto.ArticleQueryDTO;
import com.blog.module.article.dto.ArticleSaveDTO;
import com.blog.module.article.service.ArticleService;
import com.blog.module.article.vo.ArticleDetailVO;
import com.blog.module.article.vo.ArticleVO;
import com.blog.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/articles")
@RequiredArgsConstructor
public class UserArticleController {

    private final ArticleService articleService;

    @GetMapping
    public Result<PageResult<ArticleVO>> list(ArticleQueryDTO query) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        query.setUserId(userId);
        return Result.success(articleService.userPageList(query));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody ArticleSaveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        articleService.createWithUser(dto, userId);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getForEdit(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        ArticleDetailVO vo = articleService.getForEdit(id);
        return Result.success(vo);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ArticleSaveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        articleService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        articleService.delete(id);
        return Result.success();
    }
}
