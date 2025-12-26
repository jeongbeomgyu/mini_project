package org.example.onebyte.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.comment.CommentCreateRequest;
import org.example.onebyte.dto.comment.CommentResponse;
import org.example.onebyte.security.JwtTokenizer; // 너 프로젝트 경로 맞춰
import org.example.onebyte.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenizer jwtTokenizer;

    //댓글 생성
    //로그인한 사람만 가능
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentResponse> createComment(@RequestHeader("Authorization") String Authorization, @PathVariable long boardId, @Valid @RequestBody CommentCreateRequest request) {
        Long userId = jwtTokenizer.getUserIdFromToken(Authorization);
        CommentResponse commentResponse = commentService.create(boardId, userId, request);
        return  ResponseEntity.ok(commentResponse);
    }

    //댓글 수정
    //작성자만 가능




}
