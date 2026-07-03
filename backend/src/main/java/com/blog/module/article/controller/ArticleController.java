package com.blog.module.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.module.article.dto.ArticleQueryDTO;
import com.blog.module.article.entity.ArticleLike;
import com.blog.module.article.mapper.ArticleLikeMapper;
import com.blog.module.article.service.ArticleService;
import com.blog.module.article.vo.ArticleArchiveVO;
import com.blog.module.article.vo.ArticleDetailVO;
import com.blog.module.article.vo.ArticleVO;
import com.blog.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleLikeMapper articleLikeMapper;

    @GetMapping
    public Result<PageResult<ArticleVO>> list(ArticleQueryDTO query) {
        return Result.success(articleService.pageList(query));
    }

    @GetMapping("/{slugOrId}")
    public Result<ArticleDetailVO> detail(@PathVariable String slugOrId) {
        Long id = null;
        try { id = Long.parseLong(slugOrId); } catch (NumberFormatException ignored) {}
        ArticleDetailVO vo;
        if (id != null) {
            vo = articleService.getDetail(id);
        } else {
            ArticleQueryDTO query = new ArticleQueryDTO();
            query.setKeyword(slugOrId); query.setSize(50);
            PageResult<ArticleVO> result = articleService.pageList(query);
            Long matchedId = result.getRecords().stream()
                    .filter(a -> a.getSlug().equals(slugOrId)).findFirst()
                    .orElseThrow(() -> new RuntimeException("文章不存在")).getId();
            vo = articleService.getDetail(matchedId);
        }
        // Attach like info for logged-in user
        Long userId = UserContext.getUserId();
        if (userId != null) {
            boolean liked = articleLikeMapper.exists(
                    new LambdaQueryWrapper<ArticleLike>()
                            .eq(ArticleLike::getArticleId, vo.getId())
                            .eq(ArticleLike::getUserId, userId));
            vo.setLiked(liked);
        }
        return Result.success(vo);
    }

    @GetMapping("/search")
    public Result<PageResult<ArticleVO>> search(@RequestParam String keyword,
                                                 @RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long size) {
        ArticleQueryDTO query = new ArticleQueryDTO();
        query.setKeyword(keyword); query.setPage(page); query.setSize(size);
        return Result.success(articleService.pageList(query));
    }

    @GetMapping("/hot")
    public Result<List<ArticleVO>> hot(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(articleService.hotList(limit));
    }

    @GetMapping("/archive")
    public Result<List<ArticleArchiveVO>> archive() {
        return Result.success(articleService.archive());
    }

    // ─── Like ─────────────────────────────────────────────────
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        return Result.success(articleService.toggleLike(id, userId));
    }

    @GetMapping("/{id}/like")
    public Result<Boolean> checkLiked(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.success(false);
        boolean liked = articleLikeMapper.exists(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, id)
                        .eq(ArticleLike::getUserId, userId));
        return Result.success(liked);
    }
}
