package com.numble.numbledanggeun.security.auth;

import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //스프링이 로그인 요청을 가로챌 때, username, password 변수 2개를 가로채는데
    //password 부분 처리는 알아서 함
    //username 이 DB에 있는지만 확인해주면 됨.

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                new IllegalStateException("존재하지 않은 회원입니다."));

        return new PrincipalDetails(member); //시큐리티 세션에 유저 정보가 저장됨.
    }
}
