package com.learnvertx.starter.repository;

import com.learnvertx.starter.model.Task;
import com.learnvertx.starter.model.TaskDto;

import java.util.function.Function;

class TaskEntityMapper implements Function<TaskDto, Task> {
  @Override
  public Task apply(TaskDto taskDto) {
    return new Task(taskDto.id(), taskDto.userId(), taskDto.content(), taskDto.completed(), taskDto.createdAt());
  }
}
