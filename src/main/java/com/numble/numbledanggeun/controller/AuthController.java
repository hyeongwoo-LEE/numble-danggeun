package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import com.numble.numbledanggeun.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(){
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupForm(@Valid SignupDTO signupDTO, BindingResult bindingResult){

        memberService.join(signupDTO);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "auth/login";
    }
}
