package com.learnvertx.starter;

import com.learnvertx.starter.entity.Project;
import com.learnvertx.starter.entity.Task;
import com.learnvertx.starter.repository.ProjectRepository;
import com.learnvertx.starter.repository.ProjectRepositoryImpl;
import com.learnvertx.starter.repository.TaskRepositoryImpl;
import com.learnvertx.starter.service.ProjectService;
import com.learnvertx.starter.service.ProjectServiceImpl;
import com.learnvertx.starter.web.MainVerticle;
import com.learnvertx.starter.web.ProjectVerticle;
import io.vertx.core.Vertx;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class Application {

  public static void main(String[] args) {
//    Vertx vertx = Vertx.vertx();
//    vertx.deployVerticle(new MainVerticle());

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

    ProjectRepository projectRepository = null;
//    this.taskRepository = new TaskRepositoryImpl(sessionFactory);
    projectRepository = new ProjectRepositoryImpl(sessionFactory);

    ProjectService projectService = new ProjectServiceImpl(projectRepository);

//    Vertx.vertx().deployVerticle(new MainVerticle());
    Vertx.vertx().deployVerticle(new ProjectVerticle(projectService));
  }
}
