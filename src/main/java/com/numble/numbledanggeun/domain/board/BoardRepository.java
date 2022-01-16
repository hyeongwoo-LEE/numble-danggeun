package com.numble.numbledanggeun.domain.board;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardRepositoryQuerydsl {

    @Query("select b from Board b " +
            "join fetch b.member join fetch b.category " +
            "where b.boardId = :boardId")
    Board getBoardWithAll(@Param("boardId") Long boardId);


    //테스트 코드 사용
    @EntityGraph(attributePaths = {"member","category"}, type = EntityGraph.EntityGraphType.LOAD)
    Board findByTitle(String title);

}
