package com.numble.numbledanggeun.domain.board;

import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;

import java.util.List;

public interface BoardRepositoryQuerydsl {

    List<Object[]> getAllBoardList(SearchDTO searchDTO);

    List<Object[]> getBoardListByMemberId(SearchDTO searchDTO);

    List<Object[]> getBoardListOfHeart(Long principalId);

    List<Board> getPreviewBoardListInDetailView(Long boardId);



}
