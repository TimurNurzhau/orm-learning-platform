package com.example.ormlearning.web;

import com.example.ormlearning.dto.TagRequest;
import com.example.ormlearning.dto.TagResponse;
import com.example.ormlearning.entity.Tag;
import com.example.ormlearning.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Course tags")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @Operation(summary = "Create a tag")
    public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {
        Tag tag = Tag.builder().name(request.name()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(TagResponse.from(tagService.create(tag)));
    }

    @GetMapping
    @Operation(summary = "List tags")
    public List<TagResponse> list() {
        return tagService.getAll().stream().map(TagResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tag")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}