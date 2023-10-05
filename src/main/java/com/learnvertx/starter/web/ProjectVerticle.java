package com.learnvertx.starter.web;

import com.learnvertx.starter.service.ProjectService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ProjectVerticle extends AbstractVerticle {

  private final ProjectService projectService;

  public ProjectVerticle(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);

    // API to get project by id.
    router.get("/api/v1/project/:id").handler(context -> {
      Integer id = Integer.getInteger(context.pathParam("id"));
      this.projectService.findProjectById(id)
        .onSuccess(result -> {
          if (result.isPresent()) {
            JsonObject body = JsonObject.mapFrom(result.get());
            context.response().setStatusCode(200).end(body.encode());
          } else {
            context.response().setStatusCode(404).end();
          }
        })
        .onFailure(err -> context.response().setStatusCode(500).end());
    });
  }
}
