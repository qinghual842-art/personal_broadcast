package com.blog.module.article.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.PageResult;
import com.blog.exception.BusinessException;
import com.blog.module.article.dto.ArticleQueryDTO;
import com.blog.module.article.dto.ArticleSaveDTO;
import com.blog.module.article.entity.Article;
import com.blog.module.article.entity.ArticleLike;
import com.blog.module.article.entity.ArticleTagRelation;
import com.blog.module.article.mapper.ArticleLikeMapper;
import com.blog.module.article.mapper.ArticleMapper;
import com.blog.module.article.mapper.ArticleTagRelationMapper;
import com.blog.module.article.service.ArticleService;
import com.blog.module.article.vo.ArticleArchiveVO;
import com.blog.module.article.vo.ArticleDetailVO;
import com.blog.module.article.vo.ArticleVO;
import com.blog.module.category.entity.Category;
import com.blog.module.category.mapper.CategoryMapper;
import com.blog.module.tag.entity.Tag;
import com.blog.module.tag.mapper.TagMapper;
import com.blog.module.user.service.UserService;
import com.blog.module.user.vo.UserVO;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagRelationMapper articleTagRelationMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final StringRedisTemplate redisTemplate;
    private final ArticleLikeMapper articleLikeMapper;
    private final UserService userService;

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;
    private static final int MAX_HOT_LIMIT = 50;
    private static final int VIEW_COUNT_TTL_HOURS = 24;

    // ─── Public APIs ────────────────────────────────────────────

    @Override
    public PageResult<ArticleVO> pageList(ArticleQueryDTO query) {
        requireNonNull(query, "查询参数不能为空");
        sanitizePagination(query);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreateTime);

        if (query.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, query.getCategoryId());
        }
        if (query.getTagId() != null) {
            List<Long> articleIds = articleTagRelationMapper.selectList(
                    new LambdaQueryWrapper<ArticleTagRelation>().eq(ArticleTagRelation::getTagId, query.getTagId()))
                    .stream().map(ArticleTagRelation::getArticleId).toList();
            if (articleIds.isEmpty()) {
                return new PageResult<>(0, query.getPage(), query.getSize(), List.of());
            }
            wrapper.in(Article::getId, articleIds);
        }
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(Article::getTitle, query.getKeyword())
                    .or().like(Article::getSummary, query.getKeyword()));
        }

        return buildPageResult(articleMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper));
    }

    @Override
    public ArticleDetailVO getDetail(Long id) {
        requireNonNull(id, "文章ID不能为空");

        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (article.getStatus() == 0) {
            throw new BusinessException(404, "该文章为草稿状态");
        }

        // Increment view count in Redis with TTL
        String viewKey = RedisKeyUtil.articleViewCount(id);
        redisTemplate.opsForValue().increment(viewKey);
        redisTemplate.expire(viewKey, Duration.ofHours(VIEW_COUNT_TTL_HOURS));

        // Also update the DB view count atomically
        articleMapper.update(null,
                new LambdaUpdateWrapper<Article>()
                        .setSql("view_count = view_count + 1")
                        .eq(Article::getId, id));

        article.setViewCount(article.getViewCount() + 1);

        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        enrichArticleVO(vo, article);

        // Populate author info
        Long authorUserId = article.getUserId();
        if (authorUserId != null && authorUserId > 0) {
            UserVO author = userService.getUserById(authorUserId);
            if (author != null) {
                vo.setUserId(author.getId());
                vo.setAuthorName(author.getNickname() != null ? author.getNickname() : author.getUsername());
                vo.setAuthorAvatar(author.getAvatar());
            }
        }

        vo.setPrev(getPrevArticle(id));
        vo.setNext(getNextArticle(id));
        return vo;
    }

    @Override
    public List<ArticleVO> hotList(int limit) {
        if (limit <= 0 || limit > MAX_HOT_LIMIT) {
            limit = 10;
        }
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getViewCount)
                        .last("LIMIT " + limit));
        return toArticleVOList(articles);
    }

    @Override
    public List<ArticleArchiveVO> archive() {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getCreateTime));
        if (articles.isEmpty()) {
            return List.of();
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        return articles.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(a -> a.getCreateTime().format(fmt), LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream().map(e -> {
                    ArticleArchiveVO vo = new ArticleArchiveVO();
                    vo.setYearMonth(e.getKey());
                    vo.setArticles(toArticleVOList(e.getValue()));
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public ArticleDetailVO.ArticleNavVO getPrevNext(Long id) {
        requireNonNull(id, "文章ID不能为空");
        return getPrevArticle(id);
    }

    // ─── Admin APIs ─────────────────────────────────────────────

    @Override
    public PageResult<ArticleVO> adminPageList(ArticleQueryDTO query) {
        requireNonNull(query, "查询参数不能为空");
        sanitizePagination(query);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime);
        if (query.getStatus() != null) {
            wrapper.eq(Article::getStatus, query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Article::getTitle, query.getKeyword());
        }
        return buildPageResult(articleMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper));
    }

    @Override
    public PageResult<ArticleVO> userPageList(ArticleQueryDTO query) {
        requireNonNull(query, "查询参数不能为空");
        requireNonNull(query.getUserId(), "用户ID不能为空");
        sanitizePagination(query);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getUserId, query.getUserId())
                .orderByDesc(Article::getCreateTime);
        if (query.getStatus() != null) {
            wrapper.eq(Article::getStatus, query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Article::getTitle, query.getKeyword());
        }
        return buildPageResult(articleMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper));
    }

    @Override
    public ArticleDetailVO getForEdit(Long id) {
        requireNonNull(id, "文章ID不能为空");

        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);

        List<Long> tagIds = articleTagRelationMapper.selectList(
                new LambdaQueryWrapper<ArticleTagRelation>().eq(ArticleTagRelation::getArticleId, id))
                .stream().map(ArticleTagRelation::getTagId).toList();
        if (!tagIds.isEmpty()) {
            vo.setTags(tagIds.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return vo;
    }

    @Override
    @Transactional
    public void create(ArticleSaveDTO dto) {
        requireNonNull(dto, "文章数据不能为空");
        requireNotBlank(dto.getTitle(), "文章标题不能为空");
        requireNotBlank(dto.getContent(), "文章内容不能为空");

        Article article = new Article();
        BeanUtils.copyProperties(dto, article, "id", "createTime", "updateTime", "viewCount", "commentCount");
        article.setSlug(generateSlug(dto.getTitle()));
        article.setContentHtml(renderMarkdown(dto.getContent()));
        if (article.getStatus() == null) article.setStatus(0);
        if (article.getIsTop() == null) article.setIsTop(0);
        article.setViewCount(0);
        article.setCommentCount(0);
        articleMapper.insert(article);

        saveTagsBatch(article.getId(), dto.getTagIds());
        updateCategoryCount(dto.getCategoryId());
        evictArticleCache();
    }

    @Override
    @Transactional
    public void update(Long id, ArticleSaveDTO dto) {
        requireNonNull(id, "文章ID不能为空");
        requireNonNull(dto, "文章数据不能为空");
        requireNotBlank(dto.getTitle(), "文章标题不能为空");

        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        Long oldCategoryId = article.getCategoryId();
        BeanUtils.copyProperties(dto, article, "id", "createTime", "viewCount", "commentCount");
        article.setId(id);
        article.setContentHtml(renderMarkdown(dto.getContent()));
        if (article.getSlug() == null || article.getSlug().isBlank()) {
            article.setSlug(generateSlug(dto.getTitle()));
        }
        articleMapper.updateById(article);

        // Update tags atomically
        articleTagRelationMapper.delete(
                new LambdaQueryWrapper<ArticleTagRelation>().eq(ArticleTagRelation::getArticleId, id));
        saveTagsBatch(id, dto.getTagIds());

        if (!Objects.equals(oldCategoryId, dto.getCategoryId())) {
            updateCategoryCount(oldCategoryId);
            updateCategoryCount(dto.getCategoryId());
        }
        evictArticleCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireNonNull(id, "文章ID不能为空");

        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        articleMapper.deleteById(id);
        articleTagRelationMapper.delete(
                new LambdaQueryWrapper<ArticleTagRelation>().eq(ArticleTagRelation::getArticleId, id));
        updateCategoryCount(article.getCategoryId());
        evictArticleCache();
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        // Inline batch delete to avoid self-invocation transaction issue
        List<Article> articles = articleMapper.selectBatchIds(ids);
        Set<Long> categoryIds = new HashSet<>();
        for (Article article : articles) {
            if (article.getCategoryId() != null) {
                categoryIds.add(article.getCategoryId());
            }
        }

        articleMapper.deleteBatchIds(ids);
        articleTagRelationMapper.delete(
                new LambdaQueryWrapper<ArticleTagRelation>().in(ArticleTagRelation::getArticleId, ids));

        for (Long catId : categoryIds) {
            updateCategoryCount(catId);
        }
        evictArticleCache();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        requireNonNull(id, "文章ID不能为空");
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "文章状态值无效，允许的值: 0(草稿) / 1(发布)");
        }
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        article.setStatus(status);
        articleMapper.updateById(article);
        evictArticleCache();
    }

    @Override
    @Transactional
    public void createWithUser(ArticleSaveDTO dto, Long userId) {
        requireNonNull(dto, "文章数据不能为空");
        requireNotBlank(dto.getTitle(), "文章标题不能为空");
        requireNotBlank(dto.getContent(), "文章内容不能为空");
        requireNonNull(userId, "用户ID不能为空");

        Article article = new Article();
        BeanUtils.copyProperties(dto, article, "id", "createTime", "updateTime", "viewCount", "commentCount", "likeCount", "userId");
        article.setUserId(userId);
        article.setSlug(generateSlug(dto.getTitle()));
        article.setContentHtml(renderMarkdown(dto.getContent()));
        if (article.getStatus() == null) article.setStatus(0);
        if (article.getIsTop() == null) article.setIsTop(0);
        article.setViewCount(0);
        article.setCommentCount(0);
        article.setLikeCount(0);
        articleMapper.insert(article);

        saveTagsBatch(article.getId(), dto.getTagIds());
        updateCategoryCount(dto.getCategoryId());
        evictArticleCache();
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        requireNonNull(articleId, "文章ID不能为空");
        requireNonNull(userId, "用户ID不能为空");

        Article article = articleMapper.selectById(articleId);
        if (article == null) throw new BusinessException(404, "文章不存在");

        boolean alreadyLiked = articleLikeMapper.exists(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, articleId)
                        .eq(ArticleLike::getUserId, userId));

        Map<String, Object> result = new HashMap<>();

        if (alreadyLiked) {
            articleLikeMapper.delete(
                    new LambdaQueryWrapper<ArticleLike>()
                            .eq(ArticleLike::getArticleId, articleId)
                            .eq(ArticleLike::getUserId, userId));
            int count = Math.max(0, (article.getLikeCount() != null ? article.getLikeCount() : 0) - 1);
            article.setLikeCount(count);
            result.put("liked", false);
            result.put("likeCount", count);
        } else {
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            articleLikeMapper.insert(like);
            int count = (article.getLikeCount() != null ? article.getLikeCount() : 0) + 1;
            article.setLikeCount(count);
            result.put("liked", true);
            result.put("likeCount", count);
        }

        articleMapper.updateById(article);
        return result;
    }

    // ─── Private helpers ────────────────────────────────────────

    private PageResult<ArticleVO> buildPageResult(Page<Article> page) {
        List<ArticleVO> records = toArticleVOList(page.getRecords());
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    private List<ArticleVO> toArticleVOList(List<Article> articles) {
        if (articles.isEmpty()) return List.of();
        List<Long> articleIds = articles.stream().map(Article::getId).toList();

        // Batch load tag relations
        List<ArticleTagRelation> allRels = articleTagRelationMapper.selectList(
                new LambdaQueryWrapper<ArticleTagRelation>().in(ArticleTagRelation::getArticleId, articleIds));
        Map<Long, List<Long>> articleTagMap = allRels.stream()
                .collect(Collectors.groupingBy(ArticleTagRelation::getArticleId,
                        Collectors.mapping(ArticleTagRelation::getTagId, Collectors.toList())));

        // Build tag name map
        final Map<Long, String> tagIdNameMap;
        if (!allRels.isEmpty()) {
            List<Long> allTagIds = allRels.stream().map(ArticleTagRelation::getTagId).distinct().toList();
            tagIdNameMap = tagMapper.selectBatchIds(allTagIds).stream()
                    .collect(Collectors.toMap(Tag::getId, Tag::getName));
        } else {
            tagIdNameMap = Map.of();
        }

        // Build category name map
        final Map<Long, String> categoryIdNameMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        return articles.stream().map(a -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(a, vo);
            List<Long> tagIds = articleTagMap.getOrDefault(a.getId(), List.of());
            vo.setTags(tagIds.stream()
                    .map(tagIdNameMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            vo.setCategoryName(categoryIdNameMap.getOrDefault(a.getCategoryId(), ""));
            return vo;
        }).collect(Collectors.toList());
    }

    private void enrichArticleVO(ArticleDetailVO vo, Article article) {
        List<Long> tagIds = articleTagRelationMapper.selectList(
                new LambdaQueryWrapper<ArticleTagRelation>().eq(ArticleTagRelation::getArticleId, article.getId()))
                .stream().map(ArticleTagRelation::getTagId).toList();
        if (!tagIds.isEmpty()) {
            vo.setTags(tagMapper.selectBatchIds(tagIds).stream()
                    .map(Tag::getName).collect(Collectors.toList()));
        }
        if (article.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(article.getCategoryId());
            if (cat != null) {
                vo.setCategoryName(cat.getName());
            }
        }
    }

    private ArticleDetailVO.ArticleNavVO getPrevArticle(Long id) {
        Article prev = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .lt(Article::getId, id)
                        .orderByDesc(Article::getId)
                        .last("LIMIT 1"));
        return toNavVO(prev);
    }

    private ArticleDetailVO.ArticleNavVO getNextArticle(Long id) {
        Article next = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .gt(Article::getId, id)
                        .orderByAsc(Article::getId)
                        .last("LIMIT 1"));
        return toNavVO(next);
    }

    private ArticleDetailVO.ArticleNavVO toNavVO(Article article) {
        if (article == null) return null;
        ArticleDetailVO.ArticleNavVO nav = new ArticleDetailVO.ArticleNavVO();
        nav.setId(article.getId());
        nav.setTitle(article.getTitle());
        nav.setSlug(article.getSlug());
        return nav;
    }

    private void saveTagsBatch(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;

        for (Long tagId : tagIds) {
            ArticleTagRelation rel = new ArticleTagRelation();
            rel.setArticleId(articleId);
            rel.setTagId(tagId);
            articleTagRelationMapper.insert(rel);
        }
    }

    private void updateCategoryCount(Long categoryId) {
        if (categoryId == null) return;
        long count = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getCategoryId, categoryId)
                        .eq(Article::getStatus, 1));
        Category category = categoryMapper.selectById(categoryId);
        if (category != null) {
            category.setArticleCount((int) count);
            categoryMapper.updateById(category);
        }
    }

    private String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            return "article-" + System.currentTimeMillis();
        }
        String slug = title.toLowerCase().replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            slug = "article-" + System.currentTimeMillis();
        }
        if (articleMapper.exists(new LambdaQueryWrapper<Article>().eq(Article::getSlug, slug))) {
            slug += "-" + System.currentTimeMillis();
        }
        return slug;
    }

    private String renderMarkdown(String markdown) {
        if (markdown == null) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    // ─── Validation helpers ─────────────────────────────────────

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

    private void sanitizePagination(ArticleQueryDTO query) {
        // page and size are long primitives (default 1 and 10) - only need range check
        if (query.getPage() < 1) {
            query.setPage(DEFAULT_PAGE);
        }
        if (query.getSize() < 1) {
            query.setSize(DEFAULT_SIZE);
        }
        if (query.getSize() > MAX_SIZE) {
            query.setSize(MAX_SIZE);
        }
    }

    private void evictArticleCache() {
        Set<String> keys = redisTemplate.keys(RedisKeyUtil.articleListPrefix() + "*");
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
        keys = redisTemplate.keys(RedisKeyUtil.articleDetailPrefix() + "*");
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
        redisTemplate.delete(RedisKeyUtil.articleHotCache());
        redisTemplate.delete(RedisKeyUtil.articleArchiveCache());
    }
}
