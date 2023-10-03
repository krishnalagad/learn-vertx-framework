package com.learnvertx.starter.repository;

import com.learnvertx.starter.model.Task;
import com.learnvertx.starter.model.TaskDto;

import java.util.function.Function;

class TaskDtoMapper implements Function<Task, TaskDto> {
  @Override
  public TaskDto apply(Task task) {
    return new TaskDto(task.getId(), task.getUserId(), task.getContent(), task.isCompleted(), task.getCreatedAt());
  }
}
