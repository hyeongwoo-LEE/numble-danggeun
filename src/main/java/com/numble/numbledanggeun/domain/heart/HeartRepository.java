package com.numble.numbledanggeun.domain.heart;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HeartRepository extends JpaRepository<Heart,Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from Heart h where h.member.memberId = :memberId and h.board.boardId = :boardId")
    void deleteHeart(@Param("memberId") Long memberId, @Param("boardId") Long boardId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Heart h where h.board = :board")
    void deleteByBoard(@Param("board") Board board);

}
