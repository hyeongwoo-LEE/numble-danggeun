package com.numble.numbledanggeun.domain.baordImg;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.dto.boardImg.BoardImgDTO;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"board"})
@Entity
public class BoardImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardImgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String folderPath;

    @Column(nullable = false)
    private String filename;

    //연관관계 메서드
    public void setBoard(Board board){
        this.board = board;
        board.getBoardImgList().add(this);
    }
}
