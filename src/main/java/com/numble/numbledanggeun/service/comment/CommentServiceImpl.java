package com.numble.numbledanggeun.service.comment;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.comment.CommentRepository;
import com.numble.numbledanggeun.dto.comment.CommentDTO;
import com.numble.numbledanggeun.dto.comment.CommentResDTO;
import com.numble.numbledanggeun.dto.comment.CommentUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        System.out.println("=====================");
        System.out.println(commentDTO);
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

    /**
     * 댓글삭제 - isExist -> false로 변경 (실제로 데이터 삭제x)
     */
    @Transactional
    @Override
    public void remove(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalStateException("존재하지 않는 댓글입니다."));

        comment.removeComment();
    }

    /**
     * 댓글 리스트 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<CommentResDTO> getCommentList(Long boardId) {
        List<Comment> result = commentRepository.getCommentList(boardId);

        return result.stream().map(entity -> new CommentResDTO(entity)).collect(Collectors.toList());
    }


}
