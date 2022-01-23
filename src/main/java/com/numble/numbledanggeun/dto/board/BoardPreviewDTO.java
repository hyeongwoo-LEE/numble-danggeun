package com.numble.numbledanggeun.dto.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.dto.boardImg.BoardImgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardPreviewDTO {

    private Long boardId;

    private Long writerId;

    private String title;

    private int price;

    private BoardImgDTO boardImgDTO = new BoardImgDTO();

    public BoardPreviewDTO(Board board){
        boardId = board.getBoardId();
        writerId = board.getMember().getMemberId();
        title = board.getTitle();
        price = board.getPrice();

        //첫번째 사진 출력
        if (board.getBoardImgList().size() > 0){
            boardImgDTO= new BoardImgDTO(board.getBoardImgList().get(0));
        }
    }
}
