package com.learnvertx.starter.entity;

import jakarta.persistence.*;
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
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer userId;
  private String content;
  private boolean completed;
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "projectId", nullable = true)
  private Project project;

}
