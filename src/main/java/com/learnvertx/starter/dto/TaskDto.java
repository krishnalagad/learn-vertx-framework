package com.learnvertx.starter.dto;

import java.time.LocalDateTime;
import java.util.Optional;

public record TaskDto(Integer id,
                      Integer userId,
                      String content,
                      boolean completed,
                      LocalDateTime createdAt,

                      Optional<ProjectDto> project) {
}
