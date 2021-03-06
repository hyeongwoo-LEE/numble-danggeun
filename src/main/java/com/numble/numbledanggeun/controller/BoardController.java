package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.dto.board.*;
import com.numble.numbledanggeun.dto.category.CategoryResDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;
import com.numble.numbledanggeun.security.auth.PrincipalDetails;
import com.numble.numbledanggeun.service.board.BoardService;
import com.numble.numbledanggeun.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;
    private final CategoryService categoryService;

    /**
     * 전체 판매글 리스트
     */
    @GetMapping("/boards")
    public String boardList(SearchDTO searchDTO, Model model,
                            @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<BoardResDTO> boardResDTOList =
                boardService.getAllBoardList(searchDTO, principalDetails.getMember().getMemberId());

        model.addAttribute("boardResDTOList", boardResDTOList);

        return "board/boardList";
    }

    /**
     * 판매글 등록 폼
     */
    @GetMapping("/boards/new")
    public String createForm(Model model){

        List<CategoryResDTO> categoryResDTOList = categoryService.getCategoryList();
        model.addAttribute("categoryResDTOList", categoryResDTOList);

        return "board/boardForm";
    }

    /**
     * 판매글 등록
     */
    @PostMapping("/boards")
    public String createBoard(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                              @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

        boardService.register(boardDTO, principalDetails.getMember().getMemberId());

        return "redirect:/boards";
    }

    /**
     * 판매글 수정 폼
     */
    @GetMapping("/boards/{boardId}/edit")
    public String updateForm(@PathVariable Long boardId, Model model){

        List<CategoryResDTO> categoryResDTOList = categoryService.getCategoryList();
        BoardUpdateResDTO boardUpdateResDTO = boardService.findOne(boardId);

        model.addAttribute("categoryResDTOList", categoryResDTOList);
        model.addAttribute("boardUpdateResDTO", boardUpdateResDTO);

        return "board/boardUpdate";
    }

    /**
     * 판매글 수정
     */
    @PostMapping("/boards/{boardId}/edit")
    public String updateBoard(@PathVariable Long boardId,
                              @Valid BoardUpdateDTO boardUpdateDTO, BindingResult bindingResult) throws IOException {

        boardService.modify(boardUpdateDTO);

        return "redirect:/profile/sales/boards/"+boardId;
    }

    /**
     * 판매글 게시상태 수정
     */
    @PostMapping("/boards/{boardId}/postState-edit")
    public String updatePostStateOfBoard(@PathVariable("boardId") Long boardId,
                                         @RequestParam(value = "postState") String postState){

        boardService.modifyPostState(boardId,PostState.valueOf(postState.toUpperCase()));


        if (postState.equals(PostState.COMPLETION.toString())){
            return "redirect:/profile/sales?postState="+PostState.SALE.toString();
        }
        else{
            return "redirect:/profile/sales?postState="+PostState.COMPLETION.toString();
        }
    }

    /**
     * 판매글 이미지 삭제
     */
    @PostMapping("/boards/{boardId}/image-delete")
    public String deleteImageOfBoard(@PathVariable Long boardId){

        boardService.removeImage(boardId);

        return "redirect:/boards/"+boardId+"/edit";
    }

    /**
     * 판매글 삭제
     */
    @PostMapping("/boards/{boardId}/delete")
    public String deleteBoard(@PathVariable Long boardId){

        boardService.remove(boardId);

        return "redirect:/profile/sales";
    }

    /**
     * 판매글 상세 페이지
     */
    @GetMapping("/boards/{boardId}")
    public String getBoard(@PathVariable("boardId") Long boardId, Model model,
                           @AuthenticationPrincipal PrincipalDetails principalDetails){

        //판매글 상세정보
        BoardDetailDTO boardDetailDTO = boardService.getBoard(
                boardId, principalDetails.getMember().getMemberId());

        //글 작성자 다른 판매 상품 미리보기 리스트
        List<BoardPreviewDTO> boardPreviewDTOList =
                boardService.getPreviewBoardListInDetailView(boardId);

        model.addAttribute("boardDetailDTO", boardDetailDTO);
        model.addAttribute("boardPreviewDTOList", boardPreviewDTOList);

        return "board/boardDetail";
    }

    /**
     * 회원별 판매글 리스트 조회
     */
    @GetMapping("/boards/members/{memberId}")
    public String getBoardListByMemberId(@PathVariable("memberId") Long writerId,
                                         SearchDTO searchDTO, Model model,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<BoardResDTO> boardResDTOList = boardService
                .getBoardListByMemberId(writerId, searchDTO, principalDetails.getMember().getMemberId());

        model.addAttribute("writerId", writerId);
        model.addAttribute("boardResDTOList", boardResDTOList);

        return "board/memberBoardList";

    }


}
