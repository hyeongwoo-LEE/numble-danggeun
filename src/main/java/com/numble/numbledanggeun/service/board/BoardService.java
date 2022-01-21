package com.numble.numbledanggeun.service.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.dto.board.*;
import com.numble.numbledanggeun.dto.page.SearchDTO;

import java.io.IOException;
import java.util.List;

public interface BoardService {

    Board register(BoardDTO boardDTO, Long principalId) throws IOException;

    void modify(BoardUpdateDTO boardUpdateDTO) throws IOException;

    void modifyPostState(Long boardId, PostState postState);

    void remove(Long boardId);

    List<BoardResDTO> getAllBoardList(SearchDTO searchDTO, Long principalId);

    List<BoardResDTO> getBoardListByMemberId(Long memberId, SearchDTO searchDTO, Long principalId);

    List<BoardResDTO> getBoardListOfHeart(Long principalId);

    List<BoardPreviewDTO> getPreviewBoardListInDetailView(Long boardId);

    BoardDetailDTO getBoard(Long boardId, Long principalId);

    BoardPreviewDTO getSimpleBoard(Long boardId);


}
