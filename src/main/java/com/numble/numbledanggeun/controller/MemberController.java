package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.dto.board.BoardDetailDTO;
import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.member.MemberResDTO;
import com.numble.numbledanggeun.dto.member.MemberUpdateDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;
import com.numble.numbledanggeun.security.auth.PrincipalDetails;
import com.numble.numbledanggeun.service.board.BoardService;
import com.numble.numbledanggeun.service.member.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;


    /**
     * 회원 프로필
     */
    @GetMapping("/profile")
    public String readProfile(Model model,
                              @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberResDTO memberResDTO = memberService.getProfile(principalDetails.getMember().getMemberId());

        model.addAttribute("memberResDTO", memberResDTO);

        return "/member/myProfile";
    }

    /**
     * 회원 수정 폼
     */
    @GetMapping("/profile/edit")
    public String updateForm(Model model,
                             @AuthenticationPrincipal PrincipalDetails principalDetails){

        MemberResDTO memberResDTO = memberService.getProfile(principalDetails.getMember().getMemberId());

        model.addAttribute("memberResDTO", memberResDTO);

        return "/member/profileUpdate";
    }

    /**
     * 회원 수정
     */
    @PostMapping("/profile/edit")
    public String updateMember(MemberUpdateDTO memberUpdateDTO,
                               @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

        memberService.modify(memberUpdateDTO,principalDetails.getMember().getMemberId());

        return "redirect:/profile";
    }

    /**
     * 회원 프로필 사진 삭제
     */
    @PostMapping("profile/image-delete")
    public String removeImageOfMember(@AuthenticationPrincipal PrincipalDetails principalDetails){

        memberService.removeImage(principalDetails.getMember().getMemberId());

        return "redirect:/profile";
    }

    /**
     * 판매내역
     */
    @GetMapping("/profile/sales")
    public String saleList(SearchDTO searchDTO, Model model,
                           @AuthenticationPrincipal PrincipalDetails principalDetails){

        Long principalId = principalDetails.getMember().getMemberId();
        List<BoardResDTO> boardResDTOList = boardService.getBoardListByMemberId(principalId, searchDTO, principalId);

        model.addAttribute("postState", searchDTO.getPostState());
        model.addAttribute("boardResDTOList", boardResDTOList);

        return "/member/myBoardList";
    }

    /**
     * 판매 상세 페이지
     */
    @GetMapping("/profile/sales/boards/{boardId}")
    public String getSaleDetailBoard(@PathVariable Long boardId, Model model,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails){

        BoardDetailDTO boardDetailDTO =
                boardService.getBoard(boardId, principalDetails.getMember().getMemberId());

        model.addAttribute("boardDetailDTO", boardDetailDTO);

        return "/member/myBoardDetail";
    }


    /**
     * 관심목록
     */
    @GetMapping("/profile/hearts")
    public String heartList(Model model,
                            @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<BoardResDTO> boardResDTOList =
                boardService.getBoardListOfHeart(principalDetails.getMember().getMemberId());
        model.addAttribute("boardResDTOList", boardResDTOList);

        return "/member/myHeartList";
    }
}
