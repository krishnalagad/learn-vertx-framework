package com.learnvertx.starter.web;

import com.learnvertx.starter.service.ProjectService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ProjectVerticle extends AbstractVerticle {

  private final ProjectService projectService;

  public ProjectVerticle(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    // API to get project by id.
    router.get("/api/v1/project/:id").handler(context -> {
      try {
        Integer id = Integer.parseInt(context.pathParam("id"));
        System.out.println("Id of project is: " + id);
        this.projectService.findProjectById(id)
          .onSuccess(result -> {
            if (result.isPresent()) {
              System.out.println(result);
              JsonObject body = JsonObject.mapFrom(result.get());
              context.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                .end(body.encode());
            } else {
              context.response().setStatusCode(404).end("No Record found with Id: " + id);
            }
          })
          .onFailure(err -> {
              System.err.println("Error processing request: " + err.getMessage());
              context.response().setStatusCode(500).end(err.getMessage());
            });
      } catch (NumberFormatException e) {
        context.response().setStatusCode(500).end(e.getMessage());
      }
    });

    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail("Unable to load configurations.");
      }
    });
  }
}
