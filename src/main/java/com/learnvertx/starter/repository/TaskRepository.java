package com.learnvertx.starter.repository;

import com.learnvertx.starter.dto.TaskDto;
import com.learnvertx.starter.dto.TasksList;
import io.vertx.core.Future;

import java.util.Optional;

public interface TaskRepository {

  Future<TaskDto> createTask(TaskDto task);

  Future<TaskDto> updateTask(TaskDto task);

  Future<Void> removeTask(Integer id);

  Future<Optional<TaskDto>> findtaskById(Integer id);

  Future<TasksList> findTasksByUser(Integer userId);
}
