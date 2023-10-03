package com.learnvertx.starter.repository;

import com.learnvertx.starter.model.TaskDto;
import com.learnvertx.starter.model.TasksList;
import io.vertx.core.Future;
import org.hibernate.reactive.stage.Stage;

import java.util.Optional;

public record TaskRepositoryImpl(Stage.SessionFactory sessionFactory) implements TaskRepository {

  @Override
  public Future<TaskDto> createTask(TaskDto task) {
    return null;
  }

  @Override
  public Future<TaskDto> updateTask(TaskDto task) {
    return null;
  }

  @Override
  public Future<Void> removeTask(Integer id) {
    return null;
  }

  @Override
  public Future<Optional<TaskDto>> findtaskById(Integer id) {
    return null;
  }

  @Override
  public Future<TasksList> findTasksByUser(Integer userId) {
    return null;
  }
}
