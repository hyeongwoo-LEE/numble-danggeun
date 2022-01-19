package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import com.numble.numbledanggeun.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String signupForm(){
        return "/auth/signup";
    }

    @PostMapping("/members")
    public String signupForm(SignupDTO signupDTO){

        memberService.join(signupDTO);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "/auth/login";
    }
}
