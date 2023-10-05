package com.learnvertx.starter.web;

import com.learnvertx.starter.entity.Project;
import com.learnvertx.starter.entity.Task;
import com.learnvertx.starter.repository.ProjectRepository;
import com.learnvertx.starter.repository.ProjectRepositoryImpl;
import com.learnvertx.starter.service.ProjectService;
import com.learnvertx.starter.service.ProjectServiceImpl;
import com.learnvertx.starter.web.HelloVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class MainVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    DeploymentOptions options = new DeploymentOptions()
//      .setWorker(true)
//      .setInstances(8);

//    -----------------------------------------------------------------------------------------------------------------
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

//    -----------------------------------------------------------------------------------------------------------------

//    vertx.deployVerticle("com.learnvertx.starter.web.HelloVerticle", options);
    vertx.deployVerticle(new HelloVerticle());
    vertx.deployVerticle(new ProjectVerticle(projectService));

    Router router = Router.router(vertx);

    router.route().handler(ctx -> {
      String authToken = ctx.request().getHeader("AUTH_TOKEN");
      if (authToken != null && "myAuthToken".contentEquals(authToken)) {
        ctx.next();
      } else {
        ctx.response().setStatusCode(401).setStatusMessage("UNAUTHORIZED").end();
      }
    });

    // Router 1
    router.get("/api/v1/hello").handler(this::helloVertx);

    // Router 2
    router.get("/api/v1/hello/:name").handler(this::helloName);

    // static route
    router.route().handler(StaticHandler.create("web"));

    // setting type, format and path of configuration file.
    ConfigStoreOptions defaultConfig = new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path", "config.json"));

    ConfigRetrieverOptions opts = new ConfigRetrieverOptions()
      .addStore(defaultConfig);

    ConfigRetriever configRetriever = ConfigRetriever.create(vertx, opts);

    Handler<AsyncResult<JsonObject>> handler = asyncResult -> this.handleConfigResults(startPromise, router, asyncResult);
    configRetriever.getConfig(handler);
  }

  void handleConfigResults(Promise<Void> startPromise, Router router, AsyncResult<JsonObject> asyncResult) {
    if (asyncResult.succeeded()) {
      JsonObject config = asyncResult.result();
      JsonObject httpKey = config.getJsonObject("http");
      int httpPort = httpKey.getInteger("port");

      // created server here and set port number.
      vertx.createHttpServer().requestHandler(router).listen(httpPort, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port " + httpPort);
        }
      });
    } else {
      // Other stuff here
      startPromise.fail("Unable to load configurations.");
    }
  }

  void helloVertx(RoutingContext ctx) {
    vertx.eventBus().request("hello.vertx.addr", "", reply -> {
      ctx.request().response().end((String) reply.result().body());
    });
  }

  void helloName(RoutingContext ctx) {
    String name = ctx.pathParam("name");
    vertx.eventBus().request("hello.named.addr", name, reply -> {
      ctx.request().response().end((String) reply.result().body());
    });
  }
}
