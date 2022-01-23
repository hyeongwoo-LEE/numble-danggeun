package com.numble.numbledanggeun.service.member;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.baordImg.BoardImgRepository;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.category.CategoryRepository;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.comment.CommentRepository;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.heart.HeartRepository;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.member.MemberResDTO;
import com.numble.numbledanggeun.dto.member.MemberUpdateDTO;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import com.numble.numbledanggeun.service.board.BoardService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberServiceImplTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BoardImgRepository boardImgRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired HeartRepository heartRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private Member member;

    //회원탈퇴 테스트에 사용
    private Long board1Id;
    private Long boardImgId;
    private Long heartId;
    private Long comment1Id;
    private Long comment2Id;

    /**
     * member1 -> board1 작성(boardImg 1개 생성) -> heart추가, comment작성
     * member2 -> board2 작성
     * member1 -> board2에 comment 작성
     */
    @BeforeEach
    void before(){

        //회원 1,2 생성
        Member member1 = createMember("member1@naver.com", "member1Name", "member1Nickname");
        member= member1; //테스트 코드에서 사용
        Member member2 = createMember("member2@naver.com", "member2Name", "member2Nickname");

        Category category = createCategory("testCategory");
        //board 1,2 생성
        Board board1 = createBoard(member1, category, "board1Title", "content");
        board1Id = board1.getBoardId(); //테스트 코드에서 사용
        Board board2 = createBoard(member2, category, "board2Title", "content");

        //board1 - 이미지생성
        BoardImg boardImg = createBoardImg(board1, "aimd31lk_file.png", "2022/01/23");
        boardImgId = boardImg.getBoardImgId(); //테스트 코드에서 사용

        //회원1 -> board1 에 heart,comment 생성
        Heart heart = createHeart(member1, board1);
        heartId = heart.getHeartId(); //테스트 코드에서 사용
        Comment comment1 = createComment(member1, board1, "testComment");
        comment1Id = comment1.getCommentId(); //테스트 코드에서 사용

        //회원1 -> board2에 comment 생성
        Comment comment2 = createComment(member1, board2, "testComment");
        comment2Id = comment2.getCommentId(); //테스트 코드에서 사용


    }
    
    @Test
    void 닉네임_중복확인_사용가능x() throws Exception{
        //given
        
        //when
        Boolean result = memberService.isDuplicateNickname("member1Nickname");

        //then
        assertThat(result).isFalse();
    }
    
    @Test
    void 닉네임_중복확인_사용가능o() throws Exception{
        //given
        //when
        Boolean result = memberService.isDuplicateNickname("사용가능한 닉네임입니다.");

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 회원가입() throws Exception{
        //given
        SignupDTO signupDTO = SignupDTO.builder()
                .email("test@nanver.com")
                .password("1111")
                .phone("010-0000-0000")
                .nickname("testNickname")
                .username("testUsername")
                .build();

        //when
        Member saveMember = memberService.join(signupDTO);

        //then
        assertThat(saveMember.getMemberId()).isNotNull();
        assertThat(saveMember.getEmail()).isEqualTo(signupDTO.getEmail());
        assertThat(saveMember.getNickname()).isEqualTo(signupDTO.getNickname());
        assertThat(saveMember.getCreateDate()).isNotNull();
        assertThat(saveMember.getUsername()).isEqualTo(signupDTO.getUsername());
        assertThat(saveMember.getPhone()).isEqualTo(signupDTO.getPhone());
    }

    @Test
    void 회원조회() throws Exception{
        //given
        //when
        MemberResDTO memberResDTO = memberService.getProfile(member.getMemberId());

        //then
        assertThat(memberResDTO.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(memberResDTO.getNickname()).isEqualTo(member.getNickname());
        assertThat(memberResDTO.getMemberImgDTO().getFilename()).isEqualTo(member.getFilename());
        assertThat(memberResDTO.getMemberImgDTO().getFolderPath()).isEqualTo(member.getFolderPath());
    }
    
    @Test
    void 회원수정() throws Exception{
        //given
        MultipartFile file = createImage("updateFile", "updateFilename.jpeg");

        MemberUpdateDTO memberUpdateDTO = MemberUpdateDTO.builder()
                .nickname("수정닉네임")
                .imageFile(file)
                .build();

        //when
        memberService.modify(memberUpdateDTO, member.getMemberId());

        //then
        Member findMember = memberRepository.findById(this.member.getMemberId()).get();

        assertThat(findMember.getNickname()).isEqualTo(memberUpdateDTO.getNickname());
    }

    @Test
    void 회원탈퇴() throws Exception{
        //given
        Long principalId = member.getMemberId();

        //when
        memberService.remove(principalId);

        //then
        //탈퇴한 회원이 작성한 board1의 boardImg1 레코드 검색 (boardImg 삭제)
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class,
                () -> (boardImgRepository.findById(boardImgId)).get());

        //탈퇴한 회원이 board1에 추가한 heart 레코드 검색 (heart 삭제)
        NoSuchElementException e2 = assertThrows(NoSuchElementException.class,
                () -> (heartRepository.findById(heartId)).get());

        //탈퇴한 회원이 board1에 작성한 comment1 레코드 검색 (comment1 삭제)
        NoSuchElementException e3 = assertThrows(NoSuchElementException.class,
                () -> (commentRepository.findById(comment1Id)).get());

        //탈퇴한 회원이 작성한 board1 삭제 (board1 삭제)
        NoSuchElementException e4 = assertThrows(NoSuchElementException.class,
                () -> (boardRepository.findById(board1Id)).get());

        //탈퇴한 회원이 board2에 작성한 comment2 -> isExist - false 로 갱신
        Comment findComment2 = commentRepository.findById(comment2Id).get();
        assertThat(findComment2.getIsExist()).isFalse();

        //탈퇴한 회원 레코드 검색 (member1 삭제)
        NoSuchElementException e5 = assertThrows(NoSuchElementException.class,
                () -> (memberRepository.findById(principalId)).get());

        assertThat(e1.getMessage()).isEqualTo("No value present");
        assertThat(e2.getMessage()).isEqualTo("No value present");
        assertThat(e3.getMessage()).isEqualTo("No value present");
        assertThat(e4.getMessage()).isEqualTo("No value present");
        assertThat(e5.getMessage()).isEqualTo("No value present");
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

    private BoardImg createBoardImg(Board board, String filename, String folderPath) {

        BoardImg boardImg = BoardImg.builder()
                .folderPath(folderPath)
                .filename(filename)
                .build();
        boardImg.setBoard(board);
        boardImgRepository.save(boardImg);

        return boardImg;
    }

    private Member createMember(String email, String username, String nickname) {
        Member member = Member.builder()
                .email(email)
                .username(username)
                .password("1111")
                .nickname(nickname)
                .phone("010-0000-0000")
                .folderPath("2022/01/16")
                .filename("lkjsfo12a_file1.jpeg")
                .build();
        memberRepository.save(member);
        return member;
    }

    private Comment createComment(Member member, Board board, String content) {
        Comment comment = Comment.builder()
                .member(member)
                .content(content)
                .isExist(true)
                .build();
        comment.setBoard(board);
        return commentRepository.save(comment);
    }

    private Comment createComment(Member member, Board board, String content, Comment parent) {
        Comment comment = Comment.builder()
                .member(member)
                .content(content)
                .isExist(true)
                .build();
        comment.setBoard(board);
        comment.setParent(parent);
        return commentRepository.save(comment);
    }

    private Heart createHeart(Member member, Board board) {
        Heart heart = new Heart();
        heart.setMember(member);
        heart.setBoard(board);
        heartRepository.save(heart);
        return heart;
    }

    private MultipartFile createImage(String name, String originalFilename) {
        return new MockMultipartFile(name, originalFilename, "image/jpeg", "some-image".getBytes());
    }
}