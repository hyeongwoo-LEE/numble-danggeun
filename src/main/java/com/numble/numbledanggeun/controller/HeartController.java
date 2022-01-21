package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.domain.heart.HeartRepository;
import com.numble.numbledanggeun.security.auth.PrincipalDetails;
import com.numble.numbledanggeun.service.heart.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class HeartController {

    private final HeartService heartService;

    /**
     * 관심 추가
     */
    @PostMapping("/boards/{boardId}/hearts")
    public String heart(@PathVariable("boardId") Long boardId,
                        @AuthenticationPrincipal PrincipalDetails principalDetails){

        heartService.heart(principalDetails.getMember().getMemberId(), boardId);

        return "redirect:/boards/"+boardId;
    }

    /**
     * 관심 삭제
     */
    @PostMapping("/boards/{boardId}/hearts/delete")
    public String cancelHeart(@PathVariable("boardId") Long boardId,
                              @AuthenticationPrincipal PrincipalDetails principalDetails){

        heartService.cancelHeart(principalDetails.getMember().getMemberId(), boardId);

        return "redirect:/boards/"+boardId;
    }


}
