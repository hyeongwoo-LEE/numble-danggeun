package com.numble.numbledanggeun.dto.comment;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long boardId;

    private String content;

    private Long parentId;

    public Comment toEntity(Board board, Long principalId, Comment parent){
        Comment comment = Comment.builder()
                .member(Member.builder().memberId(principalId).build())
                .content(content)
                .isExist(true)
                .build();
        comment.setBoard(board);
        comment.setParent(parent);

        return comment;
    }

    public Comment toEntity(Board board, Long principalId){
        Comment comment = Comment.builder()
                .member(Member.builder().memberId(principalId).build())
                .content(content)
                .isExist(true)
                .build();
        comment.setBoard(board);

        return comment;
    }
}
