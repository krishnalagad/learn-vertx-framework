package com.learnvertx.starter.mappers;

import com.learnvertx.starter.entity.Task;
import com.learnvertx.starter.dto.TaskDto;

import java.util.function.Function;

public class TaskDtoMapper implements Function<Task, TaskDto> {
  @Override
  public TaskDto apply(Task task) {
    return new TaskDto(task.getId(), task.getUserId(), task.getContent(), task.isCompleted(), task.getCreatedAt());
  }
}
