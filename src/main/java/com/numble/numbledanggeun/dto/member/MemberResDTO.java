package com.numble.numbledanggeun.dto.member;

import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.dto.memberImg.MemberImgDTO;
import lombok.Data;

@Data
public class MemberResDTO {

    private Long memberId;

    private String nickname;

    private MemberImgDTO memberImgDTO;

    public MemberResDTO(Member member){
        memberId = member.getMemberId();
        nickname = member.getNickname();

        //회원 프로필 사진
        if (member.getFolderPath() != null && member.getFilename() != null){
            memberImgDTO = new MemberImgDTO(member.getFolderPath(),member.getFilename());
        }
    }
}
