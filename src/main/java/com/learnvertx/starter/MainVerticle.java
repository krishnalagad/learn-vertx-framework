package com.learnvertx.starter;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    DeploymentOptions options = new DeploymentOptions()
//      .setWorker(true)
//      .setInstances(8);

//    vertx.deployVerticle("com.learnvertx.starter.HelloVerticle", options);
    vertx.deployVerticle(new HelloVerticle());

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
