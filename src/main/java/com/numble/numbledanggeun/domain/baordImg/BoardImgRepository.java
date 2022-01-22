package com.numble.numbledanggeun.domain.baordImg;

import com.numble.numbledanggeun.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardImgRepository extends JpaRepository<BoardImg,Long> {

    @Modifying(flushAutomatically = true)
    @Query("delete from BoardImg bi where bi.board = :board")
    void deleteByBoard(@Param("board") Board board);

    List<BoardImg> findBoardImgByBoard(Board board);
}
