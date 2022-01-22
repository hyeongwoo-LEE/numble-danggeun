package com.numble.numbledanggeun.service.member;

import com.numble.numbledanggeun.domain.baordImg.BoardImgRepository;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.category.CategoryRepository;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.service.board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberServiceImplTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BoardImgRepository boardImgRepository;


}