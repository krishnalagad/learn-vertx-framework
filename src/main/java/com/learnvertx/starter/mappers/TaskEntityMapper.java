package com.learnvertx.starter.mappers;

import com.learnvertx.starter.entity.Project;
import com.learnvertx.starter.entity.Task;
import com.learnvertx.starter.dto.TaskDto;

import java.util.function.Function;

public class TaskEntityMapper implements Function<TaskDto, Task> {
  @Override
  public Task apply(TaskDto taskDto) {
    Task task = new Task();
    task.setId(taskDto.id());
    task.setUserId(taskDto.userId());
    task.setContent(taskDto.content());
    task.setCompleted(taskDto.completed());
    task.setCreatedAt(taskDto.createdAt());
    ProjectEntityMapper entityMapper = new ProjectEntityMapper();
    if (taskDto.project().isPresent()) {
      Project project = entityMapper.apply(taskDto.project().get());
      task.setProject(project);
    } else {
      task.setProject(null);
    }
    return task;
  }
}
