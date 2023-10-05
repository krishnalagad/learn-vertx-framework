package com.learnvertx.starter.service;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.dto.ProjectsList;
import io.vertx.core.Future;

import java.util.Optional;

public interface ProjectService {

  Future<ProjectDto> createProject (ProjectDto projectDTO);

  Future<ProjectDto> updateProject(ProjectDto projectDTO);

  Future<Optional<ProjectDto>> findProjectById (Integer id);

  Future<Void> removeProject (Integer id);

  Future<ProjectsList> findProjectsByUser (Integer userId);
}
