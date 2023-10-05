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


    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
