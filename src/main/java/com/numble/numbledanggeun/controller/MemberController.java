package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.dto.member.MemberResDTO;
import com.numble.numbledanggeun.dto.member.MemberUpdateDTO;
import com.numble.numbledanggeun.security.auth.PrincipalDetails;
import com.numble.numbledanggeun.service.member.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;


    /**
     * 회원 프로필
     */
    @GetMapping("/members/{id}")
    public String readProfile(Model model,
                              @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberResDTO memberResDTO = memberService.getProfile(principalDetails.getMember().getMemberId());

        model.addAttribute("memberResDTO", memberResDTO);

        return "/member/myProfile";
    }

    /**
     * 회원 수정 폼
     */
    @GetMapping("/members/{id}/edit")
    public String updateForm(Model model,
                             @AuthenticationPrincipal PrincipalDetails principalDetails){

        MemberResDTO memberResDTO = memberService.getProfile(principalDetails.getMember().getMemberId());

        model.addAttribute("memberResDTO", memberResDTO);

        return "/member/profileUpdate";
    }

    /**
     * 회원 수정
     */
    @PostMapping("/members/{id}/edit")
    public String updateMember(MemberUpdateDTO memberUpdateDTO,
                               @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

        memberService.modify(memberUpdateDTO,principalDetails.getMember().getMemberId());

        return "redirect:/members/"+principalDetails.getMember().getMemberId();
    }


}
