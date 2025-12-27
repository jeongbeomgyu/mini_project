package org.example.onebyte.dto.board;

import org.example.onebyte.entity.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long id,
        String title,
        String content,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUserId(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
