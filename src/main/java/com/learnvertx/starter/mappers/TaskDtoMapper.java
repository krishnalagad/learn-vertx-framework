package com.learnvertx.starter.mappers;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.entity.Task;
import com.learnvertx.starter.dto.TaskDto;

import java.util.Optional;
import java.util.function.Function;

public class TaskDtoMapper implements Function<Task, TaskDto> {
  @Override
  public TaskDto apply(Task task) {
    ProjectDtoMapper dtoMapper = new ProjectDtoMapper();
    Optional<ProjectDto> project = null;
    if (task.getProject() != null) {
        project = Optional.ofNullable(dtoMapper.apply(task.getProject()));
    } else {
      project = Optional.empty();
    }
    return new TaskDto(task.getId(), task.getUserId(), task.getContent(), task.isCompleted(),
      task.getCreatedAt(), project);
  }
}
