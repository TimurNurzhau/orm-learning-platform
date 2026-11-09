package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    Optional<Module> findByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);
}