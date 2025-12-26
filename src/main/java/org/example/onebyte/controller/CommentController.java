package org.example.onebyte.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.comment.CommentRequest;
import org.example.onebyte.dto.comment.CommentResponse;
import org.example.onebyte.security.JwtTokenizer; // 너 프로젝트 경로 맞춰
import org.example.onebyte.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenizer jwtTokenizer;

    //댓글 조회
    //모두 가능
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<Page<CommentResponse>> list(@PathVariable Long boardId, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(commentService.listByBoard(boardId, pageable));
    }

    //댓글 생성
    //로그인한 사람만 가능
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createComment(@RequestHeader("Authorization") String authorization, @PathVariable Long boardId, @Valid @RequestBody CommentRequest request) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        CommentResponse commentResponse = commentService.create(boardId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    //댓글 수정
    //작성자만 가능
    @PatchMapping("/comments/{commentId}")
    public  ResponseEntity<CommentResponse> updateComment(@RequestHeader("Authorization") String authorization, @PathVariable Long commentId, @Valid @RequestBody CommentRequest request){
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        CommentResponse commentResponse = commentService.update(commentId, userId, request);
        return ResponseEntity.ok(commentResponse);
    }



}
