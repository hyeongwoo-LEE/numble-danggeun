package com.numble.numbledanggeun.service.comment;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.comment.CommentRepository;
import com.numble.numbledanggeun.dto.comment.CommentDTO;
import com.numble.numbledanggeun.dto.comment.CommentUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * 댓글작성
     */
    @Transactional
    @Override
    public Comment register(CommentDTO commentDTO, Long principalId) {

        Board board = boardRepository.findById(commentDTO.getBoardId()).orElseThrow(() ->
                new IllegalStateException("존재하지 않은 글입니다."));

        Comment comment;

        //대댓글인 경우
        if (commentDTO.getParentId() != null){
            Comment parent = commentRepository.findById(commentDTO.getParentId()).orElseThrow(() ->
                    new IllegalStateException("존재하는 부모댓글이 없습니다."));

            comment = commentDTO.toEntity(board, principalId, parent);
        }
        //댓글인 경우
        else{
            comment = commentDTO.toEntity(board,principalId);
        }

        return commentRepository.save(comment);
    }

    /**
     * 댓글수정
     */
    @Transactional
    @Override
    public void modify(CommentUpdateDTO commentUpdateDTO) {
        Comment comment = commentRepository.findById(commentUpdateDTO.getCommentId()).orElseThrow(() ->
                new IllegalStateException("존재하지 않는 댓글입니다."));

        comment.changeContent(commentUpdateDTO.getContent());
    }
}
