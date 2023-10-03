package com.learnvertx.starter.repository;

import com.learnvertx.starter.model.Task;
import com.learnvertx.starter.model.TaskDto;
import com.learnvertx.starter.model.TasksList;
import io.vertx.core.Future;
import org.hibernate.reactive.stage.Stage;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public record TaskRepositoryImpl(Stage.SessionFactory sessionFactory) implements TaskRepository {

  @Override
  public Future<TaskDto> createTask(TaskDto task) {
    TaskEntityMapper entityMapper = new TaskEntityMapper();
    Task entity = entityMapper.apply(task);
    CompletionStage<Void> result = sessionFactory.withTransaction((s, t) -> s.persist(entity));
    TaskDtoMapper dtoMapper = new TaskDtoMapper();
    Future<TaskDto> future = Future.fromCompletionStage(result).map(v -> dtoMapper.apply(entity));
    return future;
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
    TaskDtoMapper dtoMapper = new TaskDtoMapper();
    CompletionStage<Task> result = sessionFactory.withTransaction((s, t) -> s.find(Task.class, id));
    Future<Optional<TaskDto>> future = Future.fromCompletionStage(result)
      .map(r -> Optional.ofNullable(r))
      .map(r -> r.map(dtoMapper));
    return future;
  }

  @Override
  public Future<TasksList> findTasksByUser(Integer userId) {
    return null;
  }
}
