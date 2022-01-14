package com.numble.numbledanggeun.service.comment;

import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.dto.comment.CommentDTO;
import com.numble.numbledanggeun.dto.comment.CommentUpdateDTO;

public interface CommentService {

    Comment register(CommentDTO commentDTO, Long principalId);

    void modify(CommentUpdateDTO commentUpdateDTO);

    void remove(Long commentId);

}
