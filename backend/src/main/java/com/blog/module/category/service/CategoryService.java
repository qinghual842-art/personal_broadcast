package com.blog.module.category.service;

import com.blog.module.category.dto.CategorySaveDTO;
import com.blog.module.category.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    List<CategoryVO> listAll();
    List<CategoryVO> listAllAdmin();
    CategoryVO getById(Long id);
    void create(CategorySaveDTO dto);
    void update(Long id, CategorySaveDTO dto);
    void delete(Long id);
}
