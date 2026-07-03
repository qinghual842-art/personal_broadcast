package com.blog.module.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.exception.BusinessException;
import com.blog.module.article.entity.ArticleTagRelation;
import com.blog.module.article.mapper.ArticleTagRelationMapper;
import com.blog.module.tag.dto.TagSaveDTO;
import com.blog.module.tag.entity.Tag;
import com.blog.module.tag.mapper.TagMapper;
import com.blog.module.tag.service.TagService;
import com.blog.module.tag.vo.TagVO;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagRelationMapper articleTagRelationMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<TagVO> listAll() {
        List<Tag> list = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getArticleCount));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public List<TagVO> listAllAdmin() {
        List<Tag> list = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getArticleCount));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    @Transactional
    public void create(TagSaveDTO dto) {
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "标签名称不能为空");
        }

        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        tag.setArticleCount(0);
        tagMapper.insert(tag);
        evictCache();
    }

    @Override
    @Transactional
    public void update(Long id, TagSaveDTO dto) {
        if (id == null) {
            throw new BusinessException(400, "标签ID不能为空");
        }
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "标签名称不能为空");
        }

        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }
        BeanUtils.copyProperties(dto, tag, "id", "createTime", "articleCount");
        tag.setId(id);
        tagMapper.updateById(tag);
        evictCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(400, "标签ID不能为空");
        }
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }

        // Clean up article-tag relations
        articleTagRelationMapper.delete(
                new LambdaQueryWrapper<ArticleTagRelation>().eq(ArticleTagRelation::getTagId, id));
        tagMapper.deleteById(id);
        evictCache();
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private TagVO toVO(Tag t) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(t, vo);
        return vo;
    }

    private void evictCache() {
        redisTemplate.delete(RedisKeyUtil.tagListCache());
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
