package com.learnvertx.starter.mappers;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.entity.Project;

import java.util.function.Function;

public class ProjectEntityMapper implements Function<ProjectDto, Project> {
  @Override
  public Project apply(ProjectDto projectDto) {
    Project entity = new Project();
    entity.setId(projectDto.id());
    entity.setUserId(projectDto.userId());
    entity.setName(projectDto.name());
    return entity;
  }
}
