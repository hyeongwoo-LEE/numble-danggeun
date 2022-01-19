package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.dto.board.BoardDTO;
import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.board.BoardUpdateDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;
    private final CategoryService categoryService;

    /**
     * 판매글 리스트
     */
    @GetMapping("/boards")
    public String boardList(SearchDTO searchDTO, Model model,
                            @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<BoardResDTO> boardResDTOList =
                boardService.getAllBoardList(searchDTO, principalDetails.getMember().getMemberId());

        model.addAttribute("boardResDTOList", boardResDTOList);

        return "/board/boardList";
    }

    /**
     * 판매글 등록 폼
     */
    @GetMapping("/boards/new")
    public String createForm(Model model){

        List<CategoryResDTO> categoryResDTOList = categoryService.getCategoryList();
        model.addAttribute("categoryResDTOList", categoryResDTOList);

        return "/board/boardForm";
    }

    /**
     * 판매글 등록
     */
    @PostMapping("/boards")
    public String createBoard(BoardDTO boardDTO,
                              @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        System.out.println("-----------------------");
        System.out.println(boardDTO);
        boardService.register(boardDTO, principalDetails.getMember().getMemberId());

        return "redirect:/boards";
    }

    /**
     * 판매글 수정 폼
     */
    @GetMapping("/boards/{id}/edit")
    public String updateForm(@PathVariable Long id){
        return null;
    }

    /**
     * 판매글 수정
     */
    @PostMapping("/boards/{id}/edit")
    public String updateBoard(@PathVariable Long id, BoardUpdateDTO boardUpdateDTO,
                              @AuthenticationPrincipal PrincipalDetails principalDetails){
        return null;
    }

    /**
     * 판매글 삭제
     */
    @PostMapping("/board/{id}/delete")
    public String deleteBoard(@PathVariable Long id){
        return null;
    }



}
