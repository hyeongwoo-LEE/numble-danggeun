package com.numble.numbledanggeun.dto.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.dto.boardImg.BoardImgDTO;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResDTO {

    private Long boardId;

    private Long writerId;

    private String title;

    private int price;

    private int commentCount;

    private int heartCount;

    private Boolean isHeart;

    private PostState postState;

    private BoardImgDTO boardImgDTO;

    public BoardResDTO(Board board, int commentCount, int heartCount, Long principalId){
        System.out.println("시작---------------");
        boardId = board.getBoardId();
        writerId = board.getMember().getMemberId();
        title = board.getTitle();
        price = board.getPrice();
        this.commentCount = commentCount;
        this.heartCount = heartCount;
        postState = board.getPostState();

        //관심버튼 상태 체크
        isHeart = false;
        for (Heart heart : board.getHeartList()){
            if (heart.getMember().getMemberId().equals(principalId)){
                isHeart = true;
                break;
            }
        }

        //첫번째 사진 출력
        if (board.getBoardImgList().size() > 0){
            boardImgDTO= new BoardImgDTO(board.getBoardImgList().get(0));
        }
        System.out.println("종료---------------");
    }
}
