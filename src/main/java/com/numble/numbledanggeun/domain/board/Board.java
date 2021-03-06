package com.numble.numbledanggeun.domain.board;

import com.numble.numbledanggeun.domain.BaseEntity;
import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.handler.exception.CustomException;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"member","category"})
@Entity
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Builder.Default
    @Column(nullable = false)
    private String content = "";

    @Column(nullable = false)
    private int price;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PostState postState = PostState.SALE;

    @Builder.Default
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @BatchSize(size = 500)
    @Builder.Default
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Heart> heartList = new ArrayList<>();

    @BatchSize(size = 20)
    @Builder.Default
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY)
    private List<BoardImg> boardImgList = new ArrayList<>();

    public void changeCategory(Long categoryId){
        this.category = Category.builder().categoryId(categoryId).build();
    }

    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }

    public void changePrice(int price){
        if (price <= 0){
            throw new CustomException("????????? 0??? ????????? ??? ????????????.");
        }else{
            this.price = price;
        }
    }

    public void changePostState(PostState postState){
        this.postState = postState;
    }
}
