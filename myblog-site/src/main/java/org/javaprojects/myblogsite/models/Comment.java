package org.javaprojects.myblogsite.models;

import java.time.LocalDateTime;

public record Comment(
        long id,
        String author,
        String content,
        long postId,
        LocalDateTime createdAt
) {
}
