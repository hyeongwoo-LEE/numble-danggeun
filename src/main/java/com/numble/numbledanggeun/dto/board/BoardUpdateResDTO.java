package com.numble.numbledanggeun.dto.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.dto.boardImg.BoardImgDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BoardUpdateResDTO {

    private Long boardId;

    private String title;

    private String content;

    private Long categoryId;

    private int price;

    private List<BoardImgDTO> boardImgDTOList = new ArrayList<>();

    public BoardUpdateResDTO(Board board){
        boardId = board.getBoardId();
        title = board.getTitle();
        content = board.getContent();
        categoryId = board.getCategory().getCategoryId();
        price = board.getPrice();

        //판매글 사진
        boardImgDTOList = new ArrayList<>();
        if (board.getBoardImgList().size() > 0){
            boardImgDTOList = board.getBoardImgList().stream()
                    .map(boardImg -> new BoardImgDTO(boardImg)).collect(Collectors.toList());
        }
    }


}
