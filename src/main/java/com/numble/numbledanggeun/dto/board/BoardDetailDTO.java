package com.numble.numbledanggeun.dto.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.dto.boardImg.BoardImgDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailDTO {

    private Long boardId;

    private Long writerId;

    private String writerNickname;

    private String title;

    private String content;

    private int price;

    private Boolean isHeart;

    private String categoryName;

    private String postState;

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime createDate;

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime updateDate;

    private List<BoardImgDTO> boardImgDTOList = new ArrayList<>();

    public BoardDetailDTO(Board board, Long principalId){

        System.out.println("시작 --------------------------------------");
        boardId = board.getBoardId();
        writerId = board.getMember().getMemberId();
        writerNickname = board.getMember().getNickname();
        title = board.getTitle();
        content = board.getContent();
        price = board.getPrice();
        categoryName = board.getCategory().getName();
        postState = board.getPostState().name();
        createDate = board.getCreateDate();
        updateDate = board.getUpdateDate();

        //관심버튼 상태 체크
        isHeart = false;
        for (Heart heart : board.getHeartList()){
            if (heart.getMember().getMemberId().equals(principalId)){
                isHeart = true;
                break;
            }
        }

        //판매글 사진
        boardImgDTOList = new ArrayList<>();
        if (board.getBoardImgList().size() > 0){
            boardImgDTOList = board.getBoardImgList().stream()
                    .map(boardImg -> new BoardImgDTO(boardImg)).collect(Collectors.toList());
        }

        System.out.println("종료 --------------------------------------");
    }

}
