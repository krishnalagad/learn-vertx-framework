package com.learnvertx.starter;

import io.vertx.core.AbstractVerticle;

public class HelloVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("hello.vertx.addr", msg -> {
      msg.reply("Hello Vert.x World");
    });

    vertx.eventBus().consumer("hello.named.addr", msg -> {
      String name = (String) msg.body();
      msg.reply(String.format("Hello %s", name));
    });
  }
}
