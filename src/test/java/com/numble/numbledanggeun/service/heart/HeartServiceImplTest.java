package com.numble.numbledanggeun.service.heart;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.category.CategoryRepository;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.heart.HeartRepository;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class HeartServiceImplTest {

    @Autowired HeartService heartService;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired HeartRepository heartRepository;
    @Autowired CategoryRepository categoryRepository;

    /**
     * member 1, category 1, board 1 생성
     */
    @BeforeEach
    void before(){
        Member member = createMember("beforeEmail@naver.com", "beforeName", "beforeNick", "010-1111-1111");
        Category category = createCategory("beforeCategory");
        createBoard(member, category, "beforeTitle", "beforeContent");
    }

    @Test
    void 관심_추가() throws Exception{
        //given
        Member member = memberRepository.findAll().get(0);
        Board board = boardRepository.findAll().get(0);

        //when
        Heart heart = heartService.heart(member.getMemberId(), board.getBoardId());

        //then
        Heart findHeart = heartRepository.findById(heart.getHeartId()).get();
        assertThat(findHeart.getHeartId()).isNotNull();
        assertThat(findHeart.getMember()).isEqualTo(member);
        assertThat(findHeart.getBoard()).isEqualTo(board);
    }

    @Test
    void 관심_삭제() throws Exception{
        //given
        Member member = memberRepository.findAll().get(0);
        Board board = boardRepository.findAll().get(0);

        //heart 생성
        Heart heart = createHeart(member, board);

        //when
        heartService.cancelHeart(member.getMemberId(), board.getBoardId());

        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> heartRepository.findById(heart.getHeartId()).get());

        assertThat(e.getMessage()).isEqualTo("No value present");

    }

    private Heart createHeart(Member member, Board board) {
        Heart heart = new Heart();
        heart.setMember(member);
        heart.setBoard(board);
        heartRepository.save(heart);
        return heart;
    }

    private Category createCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .build();
        categoryRepository.save(category);
        return category;
    }

    private Board createBoard(Member member, Category category, String title, String content) {
        Board board = Board.builder()
                .member(member)
                .category(category)
                .title(title)
                .content(content)
                .price(1000)
                .build();
        boardRepository.save(board);
        return board;
    }

    private Member createMember(String email, String username, String nickname, String phone) {
        Member member = Member.builder()
                .email(email)
                .username(username)
                .password("1111")
                .nickname(nickname)
                .phone(phone)
                .build();
        memberRepository.save(member);
        return member;
    }

}