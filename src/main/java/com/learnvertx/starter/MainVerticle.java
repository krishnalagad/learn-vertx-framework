package com.learnvertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    DeploymentOptions options = new DeploymentOptions()
      .setWorker(true)
      .setInstances(8);

    vertx.deployVerticle("com.learnvertx.starter.HelloVerticle", options);

    Router router = Router.router(vertx);

    // Router 1
    router.get("/api/v1/hello").handler(this::helloVertx);

    // Router 2
    router.get("/api/v1/hello/:name").handler(this::helloName);

    // created server here and set port number.
    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail(http.cause());
      }
    });
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
