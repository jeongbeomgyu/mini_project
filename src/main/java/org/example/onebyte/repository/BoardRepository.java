package org.example.onebyte.repository;

import org.example.onebyte.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 카테고리 삭제할 때, 삭제한 카테고리의 Id 를 강제로 교체 시킴
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Board b SET b.category.id = :etcId WHERE b.category.id = :targetId")
    void updateCategoryBatch(@Param("targetId") Long targetId, @Param("etcId") Long etcId);

    // 삭제된 카테고리의 글들을 숨기는 쿼리문
    @Query("SELECT b FROM Board b JOIN b.category c WHERE c.isActive = true")
    Page<Board> findAllVisibleBoards(Pageable pageable);


import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
