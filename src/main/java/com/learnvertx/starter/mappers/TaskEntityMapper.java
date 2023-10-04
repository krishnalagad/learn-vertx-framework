package com.learnvertx.starter.mappers;

import com.learnvertx.starter.entity.Task;
import com.learnvertx.starter.dto.TaskDto;

import java.util.function.Function;

public class TaskEntityMapper implements Function<TaskDto, Task> {
  @Override
  public Task apply(TaskDto taskDto) {
    return new Task(taskDto.id(), taskDto.userId(), taskDto.content(), taskDto.completed(), taskDto.createdAt());
  }
}
