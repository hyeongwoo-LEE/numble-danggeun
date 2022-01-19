package com.numble.numbledanggeun.service.member;

import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.dto.member.MemberResDTO;
import com.numble.numbledanggeun.dto.member.MemberUpdateDTO;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface MemberService {

    Boolean isDuplicateNickname(String nickname);

    Member join(SignupDTO signupDTO);

    MemberResDTO getProfile(Long principalId);

    void modify(MemberUpdateDTO memberUpdateDTO, Long principalId) throws IOException;
}
