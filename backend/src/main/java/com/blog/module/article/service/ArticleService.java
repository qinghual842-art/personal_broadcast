package com.blog.module.article.service;

import com.blog.common.PageResult;
import com.blog.module.article.dto.ArticleQueryDTO;
import com.blog.module.article.dto.ArticleSaveDTO;
import com.blog.module.article.vo.ArticleArchiveVO;
import com.blog.module.article.vo.ArticleDetailVO;
import com.blog.module.article.vo.ArticleVO;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    PageResult<ArticleVO> pageList(ArticleQueryDTO query);
    ArticleDetailVO getDetail(Long id);
    List<ArticleVO> hotList(int limit);
    List<ArticleArchiveVO> archive();
    ArticleDetailVO.ArticleNavVO getPrevNext(Long id);
    PageResult<ArticleVO> adminPageList(ArticleQueryDTO query);
    PageResult<ArticleVO> userPageList(ArticleQueryDTO query);
    ArticleDetailVO getForEdit(Long id);
    void create(ArticleSaveDTO dto);
    void createWithUser(ArticleSaveDTO dto, Long userId);
    void update(Long id, ArticleSaveDTO dto);
    void delete(Long id);
    void batchDelete(List<Long> ids);
    void updateStatus(Long id, Integer status);
    Map<String, Object> toggleLike(Long articleId, Long userId);
}
