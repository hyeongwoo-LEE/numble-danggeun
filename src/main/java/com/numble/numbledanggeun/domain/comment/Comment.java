package com.numble.numbledanggeun.domain.comment;

import com.numble.numbledanggeun.domain.BaseEntity;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"member","board"})
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Builder.Default
    @Column(nullable = false)
    private String content = "";

    @Column(nullable = false)
    @Builder.Default
    private Boolean isExist = true;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> childList  = new ArrayList<>();

    //연관관계 메서드
    public void setBoard(Board board){
        this.board = board;
        board.getCommentList().add(this);
    }

    public void setParent(Comment parent){
        this.parent = parent;
        parent.getChildList().add(this);
    }
}
