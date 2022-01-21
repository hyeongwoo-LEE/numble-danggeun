package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.dto.board.BoardPreviewDTO;
import com.numble.numbledanggeun.dto.comment.CommentDTO;
import com.numble.numbledanggeun.dto.comment.CommentResDTO;
import com.numble.numbledanggeun.security.auth.PrincipalDetails;
import com.numble.numbledanggeun.service.board.BoardService;
import com.numble.numbledanggeun.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    /**
     * 댓글 리스트
     */
    @GetMapping("/boards/{boardId}/comments")
    public String boardList(@PathVariable("boardId") Long boardId, Model model,
                            @AuthenticationPrincipal PrincipalDetails principalDetails){

        BoardPreviewDTO previewDTO = boardService.getSimpleBoard(boardId);
        List<CommentResDTO> commentResDTOList = commentService.getCommentList(boardId);

        model.addAttribute("principalId", principalDetails.getMember().getMemberId());
        model.addAttribute("previewDTO", previewDTO);
        model.addAttribute("commentResDTOList", commentResDTOList);

        return "/comment/commentList";
    }

    /**
     * 댓글 생성 폼
     */
    @GetMapping("/boards/{boardId}/comments/new")
    public String createForm(@PathVariable("boardId") Long boardId, Model model){

        model.addAttribute("boardId", boardId);

        return "/comment/commentForm";
    }

    /**
     * 댓글 생성
     */
    @PostMapping("/comments")
    public String createComment(CommentDTO commentDTO,
                                @AuthenticationPrincipal PrincipalDetails principalDetails){

        commentService.register(commentDTO, principalDetails.getMember().getMemberId());

        return "redirect:/boards/"+commentDTO.getBoardId()+"/comments";

    }

}
