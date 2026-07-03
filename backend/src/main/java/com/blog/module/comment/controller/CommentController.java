package com.blog.module.comment.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.module.comment.dto.CommentSubmitDTO;
import com.blog.module.comment.service.CommentService;
import com.blog.module.comment.vo.CommentVO;
import com.blog.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/articles/{articleId}/comments")
    public Result<PageResult<CommentVO>> listByArticle(@PathVariable Long articleId,
                                                        @RequestParam(defaultValue = "1") long page,
                                                        @RequestParam(defaultValue = "10") long size) {
        return Result.success(commentService.listByArticle(articleId, page, size));
    }

    @PostMapping("/articles/{articleId}/comments")
    public Result<?> submit(@PathVariable Long articleId,
                            @Valid @RequestBody CommentSubmitDTO dto,
                            HttpServletRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        commentService.submit(articleId, dto, request, userId);
        return Result.success();
    }

    @GetMapping("/admin/comments")
    public Result<PageResult<CommentVO>> listAll(@RequestParam(required = false) Integer status,
                                                  @RequestParam(defaultValue = "1") long page,
                                                  @RequestParam(defaultValue = "10") long size) {
        return Result.success(commentService.listAll(status, page, size));
    }

    @PutMapping("/admin/comments/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        commentService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/admin/comments/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/admin/comments/batch")
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        commentService.batchDelete(ids);
        return Result.success();
    }
}
