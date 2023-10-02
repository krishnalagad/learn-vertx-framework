package com.learnvertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    // Router 1
    router.get("/api/v1/hello").handler(ctx -> {
      ctx.request().response().end("Hello Vert.x World");
    });

    // Router 2
    router.get("/api/v1/hello/:name").handler(ctx -> {
      String name = ctx.pathParam("name");
      ctx.request().response().end(String.format("Hello %s", name));
    });

    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail(http.cause());
      }
    });

//    vertx.createHttpServer().requestHandler(req -> {
//      req.response().end("Hello World API");
//    }).listen(8080);
  }
}
