package org.example.onebyte.service;

import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.board.BoardRequest;
import org.example.onebyte.dto.board.BoardResponse;
import org.example.onebyte.entity.Board;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.PostNotFoundException;
import org.example.onebyte.repository.BoardRepository;
import org.example.onebyte.repository.UserRepository;
import org.example.onebyte.type.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponse> list(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(BoardResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getOne(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new PostNotFoundException(boardId));
        return BoardResponse.from(board);
    }

    @Override
    public BoardResponse create(Long userId, BoardRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다. id=" + userId));

        Board board = Board.builder()
                .title(request.title())
                .content(request.content())
                .userId(user.getId())
                .role(user.getRole())
                .build();

        return BoardResponse.from(boardRepository.save(board));
    }

    @Override
    public BoardResponse update(Long boardId, Long userId, BoardRequest request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new PostNotFoundException(boardId));

        if (!board.getUserId().equals(userId)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        board.update(request.title(), request.content());
        return BoardResponse.from(board);
    }

    @Override
    public void delete(Long boardId, Long userId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new PostNotFoundException(boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다. id=" + userId));

        boolean isAuthor = board.getUserId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }
}
