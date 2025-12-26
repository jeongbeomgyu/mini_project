package org.example.onebyte.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.MessageResponse;
import org.example.onebyte.dto.comment.CommentCreateRequest;
import org.example.onebyte.dto.comment.CommentResponse;
import org.example.onebyte.dto.comment.CommentUpdateRequest;
import org.example.onebyte.entity.Board;
import org.example.onebyte.entity.Comment;
import org.example.onebyte.entity.User;
import org.example.onebyte.repository.BoardRepository;
import org.example.onebyte.repository.CommentRepository;
import org.example.onebyte.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    //댓글 생성
    @Override
    public CommentResponse create(Long boardId, Long userId, CommentCreateRequest request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다. id=" + userId));

        Comment comment = Comment.create(board, user, request.content());
        Comment saved = commentRepository.save(comment);

        return CommentResponse.from(saved);
    }

    //댓글 조회 - 페이징
    @Override
    public Page<CommentResponse> listByBoard(Long boardId, Pageable pageable){
        return null;
    }

    //댓글 수정
    @Override
    public CommentResponse update(Long commentId, Long userId, CommentUpdateRequest request){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new IllegalArgumentException("댓글이 존재하지않습니다."));

        // 작성자 검증
        if(!comment.getUser().getId().equals(userId)){
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        return null;
    }

    //댓글 삭제
    @Override
    public MessageResponse delete(Long commentId, Long userId){
        return null;
    }

    //댓글 ID로 한건 조회 : 보류
    //@Override
    //public CommentResponse getOne(Long commentId);
}
