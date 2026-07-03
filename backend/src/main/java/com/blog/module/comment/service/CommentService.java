package com.blog.module.comment.service;

import com.blog.common.PageResult;
import com.blog.module.comment.dto.CommentSubmitDTO;
import com.blog.module.comment.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CommentService {
    PageResult<CommentVO> listByArticle(Long articleId, long page, long size);
    void submit(Long articleId, CommentSubmitDTO dto, HttpServletRequest request, Long userId);
    PageResult<CommentVO> listAll(Integer status, long page, long size);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
    void batchDelete(List<Long> ids);
}
