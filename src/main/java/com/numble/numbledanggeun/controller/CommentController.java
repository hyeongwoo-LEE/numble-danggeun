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
import org.springframework.web.bind.annotation.*;

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
    public String createForm(@PathVariable("boardId") Long boardId, Model model,
                             @RequestParam(value = "parentId",required = false) Long parentId){

        model.addAttribute("boardId", boardId);
        model.addAttribute("parentId", parentId);

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

    /**
     * 댓글 수정 폼
     */
    @GetMapping("/comments/{commentId}/edit")
    public String modifyComment(){
        return null;
    }

    /**
     * 댓글 수정
     */

    /**
     * 댓글 삭제
     */
    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId){
        Long boardId = commentService.remove(commentId);

        return "redirect:/boards/"+boardId+"/comments";
    }

}
