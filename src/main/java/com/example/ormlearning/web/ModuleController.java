package com.example.ormlearning.web;

import com.example.ormlearning.dto.ModuleRequest;
import com.example.ormlearning.dto.ModuleResponse;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Course modules")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    @Operation(summary = "Create a module")
    public ResponseEntity<ModuleResponse> create(@Valid @RequestBody ModuleRequest request) {
        Module module = Module.builder()
                .title(request.title())
                .description(request.description())
                .orderIndex(request.orderIndex())
                .build();
        Module saved = moduleService.create(module, request.courseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ModuleResponse.from(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a module by id")
    public ModuleResponse get(@PathVariable Long id) {
        return ModuleResponse.from(moduleService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List modules by course")
    public List<ModuleResponse> listByCourse(@RequestParam Long courseId) {
        return moduleService.getByCourse(courseId).stream().map(ModuleResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a module")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}