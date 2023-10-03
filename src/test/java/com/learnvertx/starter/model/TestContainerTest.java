package com.learnvertx.starter.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("ALL")
@Testcontainers
class TestContainerTest {

  private final static DockerImageName mysql= DockerImageName.parse("mysql:latest");

  @Container
  MySQLContainer<?> container = new MySQLContainer<>(mysql)
    .withDatabaseName("testcontainersdb")
    .withUsername("tcuser")
    .withPassword("tcsecret");

  @Test
  void testContainerIsRunningTest() {
    Assertions.assertTrue(container.isCreated());
    Assertions.assertTrue(container.isRunning());
  }
}
