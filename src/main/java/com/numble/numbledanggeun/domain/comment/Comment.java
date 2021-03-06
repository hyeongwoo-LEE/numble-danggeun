package com.numble.numbledanggeun.domain.comment;

import com.numble.numbledanggeun.domain.BaseEntity;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"member","board","parent"})
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") //회원탈퇴시 null 값 가질 수 있음.
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
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @BatchSize(size = 100)
    @Builder.Default
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> childList  = new ArrayList<>();


    public void changeContent(String content){
        this.content = content;
    }

    public void removeComment(){
        this.isExist = false;
    }

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
