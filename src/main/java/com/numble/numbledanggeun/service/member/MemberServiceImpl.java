package com.numble.numbledanggeun.service.member;

import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 닉네임 중복체크
     */
    @Transactional(readOnly = true)
    @Override
    public Boolean isDuplicateNickname(String nickname) {
        Optional<Member> result = memberRepository.findByNickname(nickname);

        if (result.isPresent()){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * 회원가입
     */
    @Transactional
    @Override
    public Member join(SignupDTO signupDTO) {

        Optional<Member> result = memberRepository.findByNickname(signupDTO.getNickname());

        if (result.isPresent()){
            throw new IllegalStateException("이미 등록된 ID 입니다.");
        }

        //password 암호화
        String rawPassword = signupDTO.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        signupDTO.setPassword(encPassword);

        Member member = signupDTO.toEntity();

        memberRepository.save(member);

        return member;
    }


}
