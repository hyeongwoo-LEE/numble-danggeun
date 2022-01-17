package com.numble.numbledanggeun.service.board;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.baordImg.BoardImgRepository;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.category.CategoryRepository;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.comment.CommentRepository;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.heart.HeartRepository;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.board.BoardDetailDTO;
import com.numble.numbledanggeun.dto.board.BoardPreviewDTO;
import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class BoardServiceReadByMemberConditionTest {

    @Autowired BoardService boardService;
    @Autowired BoardRepository boardRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BoardImgRepository boardImgRepository;
    @Autowired HeartRepository heartRepository;
    @Autowired CommentRepository commentRepository;

    //현재 사용자 memberId
    private Long loginMemberId;
    private Long viewBoardId;

    /**
     * board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
     * borad 6 - NoMember 가 작성
     * board 1,2,5 - 판매중, board 3 - 거래완료, board 4 - 예약중
     * board1 이미지 2개, 댓글 2개, 하트 2개 (게시글작성자,NoMember)
     * board3 하트 1개 (게시글작성자)
     */
    @BeforeEach
    void before(){
        Member member = createMember("email@naver.com", "username", "nickname");
        loginMemberId = member.getMemberId();
        Category category = createCategory("testCategory");

        IntStream.rangeClosed(1,5).forEach(i -> {
            createBoard(member, category, "Title" + i, "content");
        });

        List<Board> boardList = boardRepository.findAll();

        Board board1 = boardList.get(0);
        Board board2 = boardList.get(1);
        viewBoardId = board2.getBoardId();
        Board board3 = boardList.get(2);
        Board board4 = boardList.get(3);
        Board board5 = boardList.get(4);

        board3.changePostState(PostState.COMPLETION);
        board4.changePostState(PostState.RESERVATION);

        createBoardImg(board1,"sfio_이미지.jpeg","2022/01/16");
        createBoardImg(board1,"asdf12D_이미지.jpeg","2022/01/16");

        Comment parentComment = createComment(board2.getMember(), board1, "깍아주세요");
        createComment(board1.getMember(),board1,"좋아요",parentComment);

        createHeart(board1.getMember(),board1);
        createHeart(board1.getMember(),board3);

        //회원별 판매글 리스트 조회시 출력되면 안되는 게시글 생성
        Member NoMember = createMember("NoEmail@naver.com", "NoUsername", "NoNickname");
        Category testCategory = createCategory("NoCategory");
        Board board6 = createBoard(NoMember, testCategory, "출력되면 안돼요", "출력되면 안돼요");
        createHeart(NoMember, board1); //NoMember 가 board1 게시글 관심 버튼 추가.
    }

    /**
     * board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
     * borad 6 - NoMember 가 작성
     * board 1,2,5 - 판매중, board 3 - 거래완료, board 4 - 예약중
     * board1 이미지 2개, 댓글 2개, 하트 2개 (게시글작성자,NoMember)
     * board3 하트 1개 (게시글작성자)
     *
     * => board5 -> board2 -> board1 -> board4 -> board3 순으로 출력되어야함.
     */
    @Test
    void 회원별_판매글_리스트_조회() throws Exception{
        //given
        //board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
        SearchDTO searchDTO = createSearchDTO(loginMemberId);

        //when
        List<BoardResDTO> result = boardService.getBoardListByMemberId(searchDTO, loginMemberId);

        //then
        assertThat(result).extracting("title")
                .containsExactly("Title5","Title2","Title1","Title4","Title3");
        assertThat(result).extracting("writerId")
                .containsExactly(searchDTO.getMemberId(),searchDTO.getMemberId(),searchDTO.getMemberId()
                ,searchDTO.getMemberId(),searchDTO.getMemberId());
        for (BoardResDTO boardResDTO :result){
            System.out.println(boardResDTO);
        }
    }

    /**
     * 거래완료인 판매글이 1개이므로 board3 만 출력됨.
     */
    @Test
    void 회원별_판매글_리스트_조회_게시상태_조건O() throws Exception{
        //given
        //board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
        SearchDTO searchDTO = createSearchDTO(loginMemberId,PostState.COMPLETION);

        //when
        List<BoardResDTO> result = boardService.getBoardListByMemberId(searchDTO, loginMemberId);

        //then
        assertThat(result).extracting("title").containsExactly("Title3");
        assertThat(result).extracting("writerId").containsExactly(searchDTO.getMemberId());
        assertThat(result).extracting("postState").containsExactly(searchDTO.getPostState());
    }

    /**
     * board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
     * board 1- 판매중, board 3 - 거래완료
     * board1 이미지 2개, 댓글 2개, 하트 2개 (게시글작성자,NoMember)
     * board3 하트 1개 (게시글작성자)
     *
     * ==> 현재로그인사용자 == 게시글 작성자 -> board1,board3 관심중
     * ==> baord1 -> board3 순으로 출력
     */
    @Test
    void 회원별_관심목록_리스트() throws Exception{
        //when
        List<BoardResDTO> result = boardService.getBoardListOfHeart(loginMemberId);

        //then
        for (BoardResDTO dto : result){
            System.out.println(dto);
        }
    }


    /**
     * board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
     * borad 6 - NoMember 가 작성
     * board 1,2,5 - 판매중, board 3 - 거래완료, board 4 - 예약중
     * 현재 board2의 판매글을 보고 있다고 가정.
     * limit를 3개로 했으므로  미리보기 4개 출력
     * => board5 -> board1 -> board4 -> board3 순으로 출력되어야함.
     */
    @Test
    void 미리보기_판매글리스트_조회() throws Exception{
        //given
        //board 1,2,3,4,5 모두 한명이 작성 - 현재 로그인 사용자라고 가정.
        //현재 board2 판매글을 보고있다고 가정.
        // => board2의 작성자의 다른 판매글들이 출력되어야 함.
        SearchDTO searchDTO = createSearchDTO(loginMemberId);

        //when
        List<BoardPreviewDTO> result = boardService.getPreviewBoardListByMemberId(searchDTO, viewBoardId);

        //then
        assertThat(result.size()).isEqualTo(4);
        assertThat(result).extracting("title")
                .containsExactly("Title5","Title1","Title4","Title3");
        assertThat(result).extracting("writerId")
                .containsExactly(searchDTO.getMemberId(),searchDTO.getMemberId()
                        ,searchDTO.getMemberId(),searchDTO.getMemberId());
        for (BoardPreviewDTO dto: result){
            System.out.println(dto);
        }
    }

    /**
     * board1 의 상세 페이지
     */
    @Test
    void 판매글_상세페이지_조회() throws Exception{
        //given
        Board board1 = boardRepository.findByTitle("Title1");
        viewBoardId = board1.getBoardId();

        //when
        BoardDetailDTO result = boardService.getBoard(viewBoardId, loginMemberId);

        //then
        assertThat(result.getBoardId()).isEqualTo(board1.getBoardId());
        assertThat(result.getWriterId()).isEqualTo(board1.getMember().getMemberId());
        assertThat(result.getWriterNickname()).isEqualTo(board1.getMember().getNickname());
        assertThat(result.getTitle()).isEqualTo(board1.getTitle());
        assertThat(result.getContent()).isEqualTo(board1.getContent());
        assertThat(result.getPrice()).isEqualTo(board1.getPrice());
        assertThat(result.getIsHeart()).isTrue();
        assertThat(result.getCategoryName()).isEqualTo(board1.getCategory().getName());
        assertThat(result.getPostState()).isEqualTo(board1.getPostState().name());
        assertThat(result.getCreateDate()).isEqualTo(board1.getCreateDate());
        assertThat(result.getUpdateDate()).isEqualTo(board1.getUpdateDate());
        assertThat(result.getBoardImgDTOList().size()).isEqualTo(2);
        assertThat(result.getBoardImgDTOList().get(0).getFilename()).isEqualTo(board1.getBoardImgList().get(0).getFilename());
        assertThat(result.getBoardImgDTOList().get(1).getFilename()).isEqualTo(board1.getBoardImgList().get(1).getFilename());
    }

    private SearchDTO createSearchDTO(Long memberId,PostState postState) {
        return SearchDTO.builder().memberId(memberId).postState(postState).build();
    }
    private SearchDTO createSearchDTO(Long memberId) {
        return SearchDTO.builder().memberId(memberId).build();
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

}
