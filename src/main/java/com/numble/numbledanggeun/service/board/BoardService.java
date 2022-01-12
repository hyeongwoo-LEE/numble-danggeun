package com.numble.numbledanggeun.service.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.dto.board.BoardDTO;
import com.numble.numbledanggeun.dto.board.BoardUpdateDTO;

import java.io.IOException;

public interface BoardService {

    Board register(BoardDTO boardDTO, Long principalId) throws IOException;

    void modify(BoardUpdateDTO boardUpdateDTO) throws IOException;

    void modifyPostState(Long boardId, PostState postState);

    void remove(Long boardId);

}
