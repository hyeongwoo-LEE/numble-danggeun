package com.numble.numbledanggeun.service.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.dto.board.BoardDTO;
import com.numble.numbledanggeun.dto.board.BoardPreviewDTO;
import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.board.BoardUpdateDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;

import java.io.IOException;
import java.util.List;

public interface BoardService {

    Board register(BoardDTO boardDTO, Long principalId) throws IOException;

    void modify(BoardUpdateDTO boardUpdateDTO) throws IOException;

    void modifyPostState(Long boardId, PostState postState);

    void remove(Long boardId);

    List<BoardResDTO> getAllBoardList(SearchDTO searchDTO, Long principalId);

    List<BoardResDTO> getBoardListByMemberId(SearchDTO searchDTO, Long principalId);

    List<BoardPreviewDTO> getPreviewBoardListByMemberId(SearchDTO searchDTO, Long boardId);

}
