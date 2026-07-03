package com.blog.module.tag.service;

import com.blog.module.tag.dto.TagSaveDTO;
import com.blog.module.tag.vo.TagVO;
import java.util.List;

public interface TagService {
    List<TagVO> listAll();
    List<TagVO> listAllAdmin();
    void create(TagSaveDTO dto);
    void update(Long id, TagSaveDTO dto);
    void delete(Long id);
}
