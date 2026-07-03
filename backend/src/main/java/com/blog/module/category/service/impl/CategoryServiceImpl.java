package com.blog.module.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.exception.BusinessException;
import com.blog.module.article.entity.Article;
import com.blog.module.article.mapper.ArticleMapper;
import com.blog.module.category.dto.CategorySaveDTO;
import com.blog.module.category.entity.Category;
import com.blog.module.category.mapper.CategoryMapper;
import com.blog.module.category.service.CategoryService;
import com.blog.module.category.vo.CategoryVO;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<CategoryVO> listAll() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public CategoryVO getById(Long id) {
        if (id == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return toVO(category);
    }

    @Override
    public List<CategoryVO> listAllAdmin() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    @Transactional
    public void create(CategorySaveDTO dto) {
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "分类名称不能为空");
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        category.setArticleCount(0);
        categoryMapper.insert(category);
        evictCache();
    }

    @Override
    @Transactional
    public void update(Long id, CategorySaveDTO dto) {
        if (id == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "分类名称不能为空");
        }

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        BeanUtils.copyProperties(dto, category, "id", "createTime", "articleCount");
        category.setId(id);
        categoryMapper.updateById(category);
        evictCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        long articleCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, id));
        if (articleCount > 0) {
            throw new BusinessException(400, "该分类下存在" + articleCount + "篇文章，无法删除");
        }
        categoryMapper.deleteById(id);
        evictCache();
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private CategoryVO toVO(Category c) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(c, vo);
        return vo;
    }

    private void evictCache() {
        redisTemplate.delete(RedisKeyUtil.categoryListCache());
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
