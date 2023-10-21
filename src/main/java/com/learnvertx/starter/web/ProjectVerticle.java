package com.learnvertx.starter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnvertx.starter.dto.ProjectDto;
import com.learnvertx.starter.dto.ProjectsList;
import com.learnvertx.starter.service.ProjectService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ProjectVerticle extends AbstractVerticle {

  private final ProjectService projectService;

  public ProjectVerticle(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

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
//              JsonObject body = JsonObject.mapFrom(projectDto);
//              JsonObject body = new JsonObject().put("response", projectDto);
              JsonObject body = new JsonObject();
              body.put("id", projectDto.id());
              body.put("userId", projectDto.userId());
              body.put("name", projectDto.name());
              System.out.println("JsonObject: " + body);
              context.request().response().setStatusCode(200).putHeader("Content-Type", "application/json")
                .end(body.encodePrettily());
            } else {
              context.request().response().setStatusCode(404).end("No Record found with Id: " + id);
            }
          })
          .onFailure(err -> {
            System.err.println("Error processing request: " + err.getMessage());
            context.request().response().setStatusCode(500).end(err.getMessage());
          });
      } catch (NumberFormatException e) {
        context.response().setStatusCode(500).end(e.getMessage());
      }
    });

    // API to get all projects
//    router.get("/api/v1/projects/:userId").handler(context -> {
//      try {
//        Integer userId = Integer.parseInt(context.pathParam("userId"));
//        this.projectService.findProjectsByUser(userId)
//          .onSuccess(result -> {
//            if (!result.projects().isEmpty()) {
//              System.out.println(result);
//              JsonObject projects = JsonObject.mapFrom(result.projects());
////              JsonObject projects = new JsonObject();
////              projects.put("response", result);
//              System.out.println(projects);
//              context.request().response().setStatusCode(200).putHeader("Content-Type", "application/json")
//                .end(projects.encode());
//            } else {
//              context.request().response().setStatusCode(404).end("No Projects found with userId: " + userId);
//            }
//          })
//          .onFailure(err -> {
//            System.out.println("Error in processing request: " + err.getMessage());
//            context.request().response().setStatusCode(500).end();
//          });
//      } catch (Exception e) {
//        context.response().setStatusCode(500).end(e.getMessage());
//      }
//    });

    router.get("/api/v1/projects/:userId").handler(context -> {
      try {
        Integer userId = Integer.parseInt(context.pathParam("userId"));
        this.projectService.findProjectsByUser(userId)
          .onComplete(result -> {
            if (result.succeeded()) {
              ProjectsList projectsList = result.result();
              if (!projectsList.projects().isEmpty()) {
                System.out.println("ProjectList:" + projectsList.projects());
                String jsonString = projectsList.projects().toString();
//                ObjectMapper objectMapper = new ObjectMapper();
//                try {
//                  ObjectMapper obj = objectMapper.readValue(jsonString, ObjectMapper.class);
//                  System.out.println("ObjectMapper:" + obj);
//                  context.request().response()
//                    .setStatusCode(200)
//                    .putHeader("Content-Type", "application/json")
//                    .end((Handler<AsyncResult<Void>>) obj);
//                } catch (Exception e) {
//                  e.printStackTrace();
//                  throw new RuntimeException(e);
//                }
                JsonObject jsonObject = new JsonObject(jsonString);
//                JsonObject projects = JsonObject.mapFrom(projectsList.projects());
                System.out.println("JsonObject: " + jsonObject);

                context.request().response()
                  .setStatusCode(200)
//                  .putHeader("Content-Type", "application/json")
                  .end(jsonObject.encodePrettily());
              } else {
                context.response()
                  .setStatusCode(404)
                  .end("No Projects found with userId: " + userId);
              }
            } else {
              Throwable err = result.cause();
              System.out.println("Error in processing request: " + err.getMessage());
              context.response().setStatusCode(500).end();
            }
          });
      } catch (Exception e) {
        context.response().setStatusCode(500).end(e.getMessage());
      }
    });

    // API to delete project by id
    router.delete("/api/v1/project/:id").handler(context -> {
      Integer id = Integer.valueOf(context.pathParam("id"));
      this.projectService.removeProject(id)
        .onSuccess(result -> context.response().setStatusCode(204).end("Project deleted successfully."))
        .onFailure(err -> context.response().setStatusCode(500).end("Error in deleting project."));
    });

    // API to create project
    router.post("/api/v1/project").handler(context -> {
      JsonObject body = context.getBodyAsJson();
      Integer userId = body.getInteger("userId");
      String name = body.getString("name");
      ProjectDto payload = new ProjectDto(null, userId, name);

      this.projectService.createProject(payload)
        .onSuccess(result -> {
          System.out.println("Project Created: " + result);
//          JsonObject responseBody = JsonObject.mapFrom(result);
          context.response().setStatusCode(201).end(result.toString());
        })
        .onFailure(err -> context.response().setStatusCode(500).end());
    });

    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        System.out.println("HTTP server started on port 8080");
        startPromise.complete();
      } else {
        startPromise.fail("Unable to load configurations.");
      }
    });
  }
}
