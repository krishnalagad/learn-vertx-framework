package com.learnvertx.starter.dto;

import java.time.LocalDateTime;

public record TaskDto(Integer id,
                      Integer userId,
                      String content,
                      boolean completed,
                      LocalDateTime createdAt) {
}
