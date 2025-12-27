package org.example.onebyte.dto.board;

import jakarta.validation.constraints.NotBlank;

public record BoardRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}
