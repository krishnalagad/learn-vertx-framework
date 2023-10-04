package com.learnvertx.starter.repository;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.dto.ProjectsList;
import com.learnvertx.starter.entity.Project;
import com.learnvertx.starter.mappers.ProjectDtoMapper;
import com.learnvertx.starter.mappers.ProjectEntityMapper;
import io.vertx.core.Future;
import jakarta.persistence.criteria.*;
import org.hibernate.reactive.stage.Stage;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public record ProjectRepositoryImpl(Stage.SessionFactory sessionFactory) implements ProjectRepository {
  @Override
  public Future<ProjectDto> createProject(ProjectDto projectDto) {
    ProjectEntityMapper entityMapper = new ProjectEntityMapper();
    Project project = entityMapper.apply(projectDto);
    CompletionStage<Void> result = sessionFactory.withTransaction((s, t) -> s.persist(project));
    ProjectDtoMapper dtoMapper = new ProjectDtoMapper();
    Future<ProjectDto> future = Future.fromCompletionStage(result).map(v -> dtoMapper.apply(project));
    return future;
  }

  @Override
  public Future<ProjectDto> updateProject(ProjectDto projectDto) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaUpdate<Project> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Project.class);
    Root<Project> root = criteriaUpdate.from(Project.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), projectDto.id());

    criteriaUpdate.set("name", projectDto.name());
    criteriaUpdate.where(predicate);

    CompletionStage<Integer> result = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaUpdate)
      .executeUpdate());
    Future<ProjectDto> future = Future.fromCompletionStage(result).map(r -> projectDto);
    return future;
  }

  @Override
  public Future<Optional<ProjectDto>> findProjectById(Integer id) {
    ProjectDtoMapper mapper = new ProjectDtoMapper();
    CompletionStage<Project> result = sessionFactory().withTransaction((s,t) -> s.find(Project.class, id));
    Future<Optional<ProjectDto>> future = Future.fromCompletionStage(result)
      .map(r -> Optional.ofNullable(r))
      .map(r -> r.map(mapper));
    return future;
  }

  @Override
  public Future<Void> removeProject(Integer id) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Project> criteriaDelete = criteriaBuilder.createCriteriaDelete(Project.class);
    Root<Project> root = criteriaDelete.from(Project.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);
    criteriaDelete.where(predicate);

    CompletionStage<Integer> result = sessionFactory.withTransaction((s,t) -> s.createQuery(criteriaDelete).executeUpdate());
    Future<Void> future = Future.fromCompletionStage(result).compose(r -> Future.succeededFuture());
    return future;
  }

  @Override
  public Future<ProjectsList> findProjectByUser(Integer userId) {
    ProjectDtoMapper dtoMapper = new ProjectDtoMapper();
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Project> criteriaQuery = criteriaBuilder.createQuery(Project.class);
    Root<Project> root = criteriaQuery.from(Project.class);
    Predicate predicate = criteriaBuilder.equal(root.get("userId"), userId);
    criteriaQuery.where(predicate);
    CompletionStage<List<Project>> result = sessionFactory().withTransaction((s, t) -> s.createQuery(criteriaQuery).getResultList());
    Future<ProjectsList> future = Future.fromCompletionStage(result)
      .map(list -> list.stream().map(dtoMapper).collect(Collectors.toList()))
      .map(list -> new ProjectsList(list));
    return future;
  }
}
