package com.learnvertx.starter.repository;

import com.learnvertx.starter.mappers.TaskDtoMapper;
import com.learnvertx.starter.mappers.TaskEntityMapper;
import com.learnvertx.starter.model.Task;
import com.learnvertx.starter.dto.TaskDto;
import com.learnvertx.starter.dto.TasksList;
import io.vertx.core.Future;
import jakarta.persistence.criteria.*;
import org.hibernate.reactive.stage.Stage;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaUpdate<Task> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Task.class);
    Root<Task> root = criteriaUpdate.from(Task.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), task.id());

    criteriaUpdate.set("content", task.content());
    criteriaUpdate.set("completed", task.completed());
    criteriaUpdate.where(predicate);

    CompletionStage<Integer> result = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaUpdate)
      .executeUpdate());
    Future<TaskDto> future = Future.fromCompletionStage(result).map(r -> task);

    return future;
  }

  @Override
  public Future<Void> removeTask(Integer id) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Task> criteriaDelete = criteriaBuilder.createCriteriaDelete(Task.class);
    Root<Task> root = criteriaDelete.from(Task.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);
    criteriaDelete.where(predicate);

    CompletionStage<Integer> result = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaDelete)
      .executeUpdate());
    Future<Void> future = Future.fromCompletionStage(result).compose(r -> Future.succeededFuture());
    return future;
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
    TaskDtoMapper taskDtoMapper = new TaskDtoMapper();
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
    Root<Task> root = criteriaQuery.from(Task.class);
    Predicate predicate = criteriaBuilder.equal(root.get("userId"), userId);
    criteriaQuery.where(predicate);

    CompletionStage<List<Task>> result = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaQuery)
      .getResultList());
    Future<TasksList> future = Future.fromCompletionStage(result)
      .map(list -> list.stream().map(taskDtoMapper).collect(Collectors.toList()))
      .map(list -> new TasksList(list));
    return future;
  }
}
