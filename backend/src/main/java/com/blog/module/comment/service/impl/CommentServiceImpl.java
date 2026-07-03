package com.blog.module.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.PageResult;
import com.blog.exception.BusinessException;
import com.blog.module.article.entity.Article;
import com.blog.module.article.mapper.ArticleMapper;
import com.blog.module.comment.dto.CommentSubmitDTO;
import com.blog.module.comment.entity.Comment;
import com.blog.module.comment.mapper.CommentMapper;
import com.blog.module.comment.service.CommentService;
import com.blog.module.comment.vo.CommentVO;
import com.blog.module.user.service.UserService;
import com.blog.module.user.vo.UserVO;
import com.blog.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final UserService userService;

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;
    private static final int MAX_COMMENT_LENGTH = 1000;

    @Override
    public PageResult<CommentVO> listByArticle(Long articleId, long page, long size) {
        requireNonNull(articleId, "文章ID不能为空");
        if (page < 1) page = DEFAULT_PAGE;
        if (size < 1 || size > MAX_SIZE) size = DEFAULT_SIZE;

        Page<Comment> p = commentMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByDesc(Comment::getCreateTime));

        // Get article author to mark author comments
        Article article = articleMapper.selectById(articleId);
        Long authorUserId = article != null ? article.getUserId() : null;

        List<CommentVO> records = p.getRecords().stream()
                .map(c -> toCommentVO(c, authorUserId))
                .collect(Collectors.toList());
        return new PageResult<>(p.getTotal(), p.getCurrent(), p.getSize(), records);
    }

    @Override
    @Transactional
    public void submit(Long articleId, CommentSubmitDTO dto, HttpServletRequest request, Long userId) {
        requireNonNull(articleId, "文章ID不能为空");
        requireNonNull(dto, "评论数据不能为空");
        requireNotBlank(dto.getContent(), "评论内容不能为空");
        requireNonNull(userId, "请先登录");

        if (dto.getContent().length() > MAX_COMMENT_LENGTH) {
            throw new BusinessException(400, "评论内容过长，最多允许" + MAX_COMMENT_LENGTH + "个字符");
        }

        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getStatus() == 0) {
            throw new BusinessException(404, "文章不存在");
        }

        UserVO user = userService.getUserById(userId);
        if (user == null) throw new BusinessException(401, "用户不存在");

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
        comment.setAuthorEmail(user.getEmail());
        comment.setContent(dto.getContent().trim());
        comment.setParentId(dto.getParentId());
        comment.setIpAddress(request != null ? IpUtil.getClientIp(request) : "");
        comment.setStatus(1);
        commentMapper.insert(comment);

        // Update comment count atomically
        articleMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Article>()
                        .setSql("comment_count = comment_count + 1")
                        .eq(Article::getId, articleId));
    }

    @Override
    public PageResult<CommentVO> listAll(Integer status, long page, long size) {
        if (page < 1) page = DEFAULT_PAGE;
        if (size < 1 || size > MAX_SIZE) size = DEFAULT_SIZE;

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreateTime);
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        Page<Comment> p = commentMapper.selectPage(new Page<>(page, size), wrapper);

        List<CommentVO> records = p.getRecords().stream().map(c -> toCommentVO(c, null)).collect(Collectors.toList());
        return new PageResult<>(p.getTotal(), p.getCurrent(), p.getSize(), records);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        requireNonNull(id, "评论ID不能为空");
        if (status == null || (status != 0 && status != 1 && status != 2)) {
            throw new BusinessException(400, "评论状态值无效，允许的值: 0(待审) / 1(通过) / 2(拒绝)");
        }
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireNonNull(id, "评论ID不能为空");
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        commentMapper.deleteById(id);

        // Sync article comment count
        if (comment.getArticleId() != null) {
            articleMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Article>()
                            .setSql("comment_count = GREATEST(comment_count - 1, 0)")
                            .eq(Article::getId, comment.getArticleId()));
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        // Collect article IDs for comment count sync
        List<Comment> comments = commentMapper.selectBatchIds(ids);
        commentMapper.deleteBatchIds(ids);

        // Sync article comment counts
        List<Long> articleIds = comments.stream()
                .map(Comment::getArticleId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        for (Long articleId : articleIds) {
            long count = commentMapper.selectCount(
                    new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getArticleId, articleId)
                            .eq(Comment::getStatus, 1));
            articleMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Article>()
                            .set(Article::getCommentCount, (int) count)
                            .eq(Article::getId, articleId));
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private CommentVO toCommentVO(Comment c, Long authorUserId) {
        CommentVO vo = new CommentVO();
        vo.setId(c.getId());
        vo.setArticleId(c.getArticleId());
        vo.setParentId(c.getParentId());
        vo.setAuthorName(c.getAuthorName());
        vo.setContent(c.getContent());
        vo.setStatus(c.getStatus());
        vo.setCreateTime(c.getCreateTime());
        if (c.getUserId() != null) {
            UserVO user = userService.getUserById(c.getUserId());
            if (user != null && user.getAvatar() != null) {
                vo.setAuthorAvatar(user.getAvatar());
            }
            vo.setIsAuthor(c.getUserId().equals(authorUserId));
        }
        return vo;
    }

    private void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(400, message);
        }
    }

    private void requireNotBlank(String str, String message) {
        if (str == null || str.isBlank()) {
            throw new BusinessException(400, message);
        }
    }
}
