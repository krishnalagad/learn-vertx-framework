package com.learnvertx.starter.model;

import com.learnvertx.starter.repository.TaskRepository;
import com.learnvertx.starter.repository.TaskRepositoryImpl;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Properties;

@ExtendWith(VertxExtension.class)
class HibernateConfigurationTest {

  private TaskRepository taskRepository;

  private Logger logger = LoggerFactory.getLogger(HibernateConfigurationTest.class);

  /**
   * @param vertx
   * @param context
   */
  @BeforeEach
  void initializeHibernateWithCodeTest(Vertx vertx, VertxTestContext context) {
    // 1. Create properties with config data
    Properties hibernateProps = new Properties();

    hibernateProps.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/revise?serverTimezone=UTC");
    hibernateProps.put("hibernate.connection.username", "root");
    hibernateProps.put("hibernate.connection.password", "krishna24");
    hibernateProps.put("jakarta.persistence.schema-generation.database.action", "update");
    hibernateProps.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

    // 2. Create Hibernate configurations
    Configuration hibernateConfig = new Configuration();
    hibernateConfig.setProperties(hibernateProps);
    hibernateConfig.addAnnotatedClass(Task.class);

    //3. Create ServiceRegistry
    ServiceRegistry serviceRegistry = new ReactiveServiceRegistryBuilder()
      .applySettings(hibernateConfig.getProperties())
      .build();

    //4. Create SessionFactory
    Stage.SessionFactory sessionFactory = hibernateConfig
      .buildSessionFactory(serviceRegistry)
      .unwrap(Stage.SessionFactory.class);

    this.taskRepository = new TaskRepositoryImpl(sessionFactory);
    context.completeNow();
  }

  @Test   // Test to check creation of task.
  void createTaskTest(Vertx vertx, VertxTestContext context) {
    TaskDto taskDto = new TaskDto(null, 10, "My 7th task content", true, LocalDateTime.now());
    context.verify(() -> {
      this.taskRepository.createTask(taskDto)
        .onFailure(err -> context.failNow(err))
        .onSuccess(result -> {
          Assertions.assertNotNull(result);
          Assertions.assertNotNull(result.id());
          Assertions.assertEquals(10, result.id());
          context.completeNow();
        });
    });
  }

  @Test   // Test to check get one task
  void findTaskByIdDoesNotExistsTest(Vertx vertx, VertxTestContext context) {
    context.verify(() -> {
      this.taskRepository.findtaskById(1)
//        .compose(r -> this.taskRepository.findtaskById(1))
        .onSuccess(result -> {
          Assertions.assertTrue(result.isEmpty());
          context.completeNow();
        })
        .onFailure(err -> context.failNow(err));
    });
  }

  @Test   // Test to check create and get task
  void findTaskByIdExistsTest(Vertx vertx, VertxTestContext context) {
    TaskDto taskDto = new TaskDto(null, 7, "My 7th new task content", true, LocalDateTime.now());
    context.verify(() -> {
      this.taskRepository.createTask(taskDto)
        .compose(r -> this.taskRepository.findtaskById(r.id()))
        .onFailure(err -> context.failNow(err))
        .onSuccess(result -> {
          Assertions.assertTrue(result.isPresent());
          context.completeNow();
        });
    });
  }

  @AfterEach
  public void finish(Vertx vertx, VertxTestContext testContext) {
    System.out.println("after");
    vertx.close(testContext.succeeding(response -> {
      testContext.completeNow();
    }));
  }

}
