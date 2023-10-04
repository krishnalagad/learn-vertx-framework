package com.learnvertx.starter.entity;

import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.dto.TaskDto;
import com.learnvertx.starter.repository.ProjectRepository;
import com.learnvertx.starter.repository.ProjectRepositoryImpl;
import com.learnvertx.starter.repository.TaskRepository;
import com.learnvertx.starter.repository.TaskRepositoryImpl;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

@ExtendWith(VertxExtension.class)
public class RelationshipTest {

  private TaskRepository taskRepository;
  private ProjectRepository projectRepository;

  private Logger logger = LoggerFactory.getLogger(RelationshipTest.class);

  @BeforeEach
  void initializeHibernateWithCodeTest(Vertx vertx, VertxTestContext context) {
    // 1. Create properties with config data
    Properties hibernateProps = new Properties();

    hibernateProps.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/revise?serverTimezone=UTC");
    hibernateProps.put("hibernate.connection.username", "root");
    hibernateProps.put("hibernate.connection.password", "krishna24");
    hibernateProps.put("jakarta.persistence.schema-generation.database.action", "update");
    hibernateProps.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    hibernateProps.put("hibernate.show_sql", true);
    hibernateProps.put("hibernate.format_sql", true);
//    hibernateProps.put("hibernate.generate_statistics", true);

    // 2. Create Hibernate configurations
    Configuration hibernateConfig = new Configuration();
    hibernateConfig.setProperties(hibernateProps);
    hibernateConfig.addAnnotatedClass(Task.class);
    hibernateConfig.addAnnotatedClass(Project.class);

    //3. Create ServiceRegistry
    ServiceRegistry serviceRegistry = new ReactiveServiceRegistryBuilder()
      .applySettings(hibernateConfig.getProperties())
      .build();

    //4. Create SessionFactory
    Stage.SessionFactory sessionFactory = hibernateConfig
      .buildSessionFactory(serviceRegistry)
      .unwrap(Stage.SessionFactory.class);

    this.taskRepository = new TaskRepositoryImpl(sessionFactory);
    this.projectRepository = new ProjectRepositoryImpl(sessionFactory);
    context.completeNow();
  }

  // Test to create project and mapped it to task
  @Test
  void createRelationshipTest(Vertx vertx, VertxTestContext context) {
    ProjectDto projectDto = new ProjectDto(null, 1, "Vertx Web Microservices");
    context.verify(() -> {
      this.projectRepository.createProject(projectDto).compose(project -> {
        Assertions.assertEquals(3, project.id());
        TaskDto taskDto = new TaskDto(null, 1, "Task-Add Records", false, LocalDateTime.now(),
          Optional.of(project));
        return taskRepository.createTask(taskDto);
      }).onSuccess(result -> {
        System.out.println(result);
        Assertions.assertTrue(result.project().isPresent());
        context.completeNow();
      }).onFailure(err -> context.failNow(err));
    });
  }
}
