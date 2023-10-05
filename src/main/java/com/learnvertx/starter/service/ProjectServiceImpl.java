package com.learnvertx.starter.service;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.dto.ProjectsList;
import com.learnvertx.starter.repository.ProjectRepository;
import io.vertx.core.Future;

import java.util.Optional;

public record ProjectServiceImpl(ProjectRepository repository) implements ProjectService{
  @Override
  public Future<ProjectDto> createProject(ProjectDto projectDTO) {
    return repository().createProject(projectDTO);
  }

  @Override
  public Future<ProjectDto> updateProject(ProjectDto projectDTO) {
    return repository().updateProject(projectDTO);
  }

  @Override
  public Future<Optional<ProjectDto>> findProjectById(Integer id) {
    return repository().findProjectById(id);
  }

  @Override
  public Future<Void> removeProject(Integer id) {
    return repository().removeProject(id);
  }

  @Override
  public Future<ProjectsList> findProjectsByUser(Integer userId) {
    return repository().findProjectByUser(userId);
  }
}
