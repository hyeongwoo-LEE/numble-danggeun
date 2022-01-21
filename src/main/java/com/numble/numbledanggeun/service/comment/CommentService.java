package com.numble.numbledanggeun.service.comment;

import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.dto.comment.CommentDTO;
import com.numble.numbledanggeun.dto.comment.CommentResDTO;
import com.numble.numbledanggeun.dto.comment.CommentUpdateDTO;

import java.util.List;

public interface CommentService {

    Comment register(CommentDTO commentDTO, Long principalId);

    void modify(CommentUpdateDTO commentUpdateDTO);

    Long remove(Long commentId);

    List<CommentResDTO> getCommentList(Long boardId);

}
