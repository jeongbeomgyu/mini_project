package org.example.onebyte.controller;

import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.board.BoardRequest;
import org.example.onebyte.dto.board.BoardResponse;
import org.example.onebyte.security.CustomUserDetails;
import org.example.onebyte.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public Page<BoardResponse> list(Pageable pageable) {
        return boardService.list(pageable);
    }

    @GetMapping("/{boardId}")
    public BoardResponse getOne(@PathVariable Long boardId) {
        return boardService.getOne(boardId);
    }

    @PostMapping
    public BoardResponse create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody BoardRequest request
    ) {
        return boardService.create(user.getUserId(), request);
    }

    @PutMapping("/{boardId}")
    public BoardResponse update(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody BoardRequest request
    ) {
        return boardService.update(boardId, user.getUserId(), request);
    }

    @DeleteMapping("/{boardId}")
    public void delete(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        boardService.delete(boardId, user.getUserId());
    }
}
