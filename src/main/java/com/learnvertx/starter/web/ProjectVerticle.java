package com.learnvertx.starter.web;

import com.learnvertx.starter.dto.ProjectDto;
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
              ProjectDto projectDto = result.get();
              System.out.println("Result.get(): " + projectDto);
              JsonObject body = JsonObject.mapFrom(projectDto);
              System.out.println("JsonObject: " + body);
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

    // API to get all projects
    router.get("/api/v1/projects/:userId").handler(context -> {
      try {
        Integer userId = Integer.parseInt(context.pathParam("userId"));
        this.projectService.findProjectsByUser(userId)
          .onSuccess(result -> {
            if (!result.projects().isEmpty()) {
              System.out.println(result);
              JsonObject projects = JsonObject.mapFrom(result);
              context.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                .end(projects.encode());
            } else {
              context.response().setStatusCode(404).end("No Projects found with userId: " + userId);
            }
          })
          .onFailure(err -> {

          });
      } catch (Exception e) {
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
