package com.learnvertx.starter.model;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.CompletionStage;

@ExtendWith(VertxExtension.class)
class HibernateConfigurationTest {

  /**
   * @param vertx
   * @param context
   */
  @Test
  void initializeHibernateWithCodeTest(Vertx vertx, VertxTestContext context) {
    // 1. Create properties with config data
    Properties hibernateProps = new Properties();

    hibernateProps.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/revise?serverTimezone=UTC");
    hibernateProps.put("hibernate.connection.username", "root");
    hibernateProps.put("hibernate.connection.password", "krishna24");
    hibernateProps.put("jakarta.persistence.schema-generation.database.action", "update");
    hibernateProps.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

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

    // Perform actions on DB.
    Task task = new Task();
    task.setUserId(1);
    task.setContent("This is new task");
    task.setCompleted(false);
    task.setCreatedAt(LocalDateTime.now());

    System.out.println("Task ID before insertion is : " + task.getId());
    CompletionStage<Void> insertionResult = sessionFactory.withTransaction((s, t) -> s.persist(task));

    Future<Void> future = Future.fromCompletionStage(insertionResult);
    context.verify(() -> future.onFailure(err -> context.failNow(err)).onSuccess(r -> {
      System.out.println("Task ID before insertion is : " + task.getId());
      context.completeNow();
    }));
  }

}
