package com.learnvertx.starter.mappers;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.entity.Project;

import java.util.function.Function;

public class ProjectDtoMapper implements Function<Project, ProjectDto> {
  @Override
  public ProjectDto apply(Project project) {
    return new ProjectDto(project.getId(), project.getUserId(), project.getName());  }
}
