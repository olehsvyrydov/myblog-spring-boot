package org.javaprojects.myblogsite.models;

import java.time.LocalDateTime;

public record Post(
        Long id,
        String title,
        String description,
        String content,
        String imageUrl,
        LocalDateTime createdAt
) {
}
