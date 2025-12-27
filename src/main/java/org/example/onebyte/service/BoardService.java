package org.example.onebyte.service;

import org.example.onebyte.dto.board.BoardRequest;
import org.example.onebyte.dto.board.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    Page<BoardResponse> list(Pageable pageable);

    BoardResponse getOne(Long boardId);

    BoardResponse create(Long userId, BoardRequest request);

    BoardResponse update(Long boardId, Long userId, BoardRequest request);

    void delete(Long boardId, Long userId);
}
