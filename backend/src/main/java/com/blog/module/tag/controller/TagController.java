package com.blog.module.tag.controller;

import com.blog.common.Result;
import com.blog.module.tag.dto.TagSaveDTO;
import com.blog.module.tag.service.TagService;
import com.blog.module.tag.vo.TagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public Result<List<TagVO>> listAll() {
        return Result.success(tagService.listAll());
    }

    @GetMapping("/admin/tags")
    public Result<List<TagVO>> listAllAdmin() {
        return Result.success(tagService.listAllAdmin());
    }

    @PostMapping("/admin/tags")
    public Result<?> create(@Valid @RequestBody TagSaveDTO dto) {
        tagService.create(dto);
        return Result.success();
    }

    @PutMapping("/admin/tags/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody TagSaveDTO dto) {
        tagService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/admin/tags/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }
}
