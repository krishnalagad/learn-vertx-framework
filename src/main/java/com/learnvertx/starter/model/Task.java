package com.learnvertx.starter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vertx_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
  private Integer id;
  private Integer userId;
  private String content;
  private boolean completed;
  private LocalDateTime createdAt;

}
