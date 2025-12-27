package org.example.onebyte.repository;

import org.example.onebyte.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //리턴 타입 : 성능 최적화, UX때문
    Page<Comment> findByBoard_Id(Long boardId, Pageable pageable);
}