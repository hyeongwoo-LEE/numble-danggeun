package com.numble.numbledanggeun.service.comment;

import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.dto.comment.CommentDTO;

public interface CommentService {

    Comment register(CommentDTO commentDTO, Long principalId);

    void modify(Comment)

}
