package com.numble.numbledanggeun.domain.comment;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment c join fetch c.member " +
            "where c.board.boardId = :boardId and c.parent is null " +
            "order by c.commentId desc")
    List<Comment> getCommentList(@Param("boardId") Long boardId);

    @Query("select c from Comment c where c.member = :member")
    List<Comment> getCommentByMember(@Param("member") Member member);

    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.board = :board")
    void deleteByBoard(@Param("board") Board board);
}
