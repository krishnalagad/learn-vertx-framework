package com.learnvertx.starter.repository;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.dto.ProjectsList;
import io.vertx.core.Future;

import java.util.Optional;

public interface ProjectRepository {

  Future<ProjectDto> createProject(ProjectDto projectDto);

  Future<ProjectDto> updateProject(ProjectDto projectDto);

  Future<Optional<ProjectDto>> findProjectById(Integer id);

  Future<Void> removeProject(Integer id);

  Future<ProjectsList> findProjectByUser(Integer userId);
}
