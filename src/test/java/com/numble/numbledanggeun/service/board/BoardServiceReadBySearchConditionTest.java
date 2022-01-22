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
import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class BoardServiceReadBySearchConditionTest {

    @Autowired BoardService boardService;
    @Autowired BoardRepository boardRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BoardImgRepository boardImgRepository;
    @Autowired HeartRepository heartRepository;
    @Autowired CommentRepository commentRepository;

    //현재 사용자 memberId
    private Long loginMemberId;

    /**
     * board1,2 (디지털기기), board3,5(게임/취미)
     * board1,3,5 title = ~닌텐도~
     * board 3만 게시글상태 - 완료, 나머진 모두 판매중
     * board1 이미지 2개, 댓글 2개, 하트 1개 (게시글작성자)
     */
    @BeforeEach
    void before(){
        IntStream.rangeClosed(1,5).forEach(i -> {
            Member member = createMember("email" + i + "naver.com", "username" + i, "nickname" + i);
            Category category = createCategory("test" + i);
            createBoard(member, category, "Title" + i, "content");
        });

        List<Board> boardList = boardRepository.findAll();

        Board board1 = boardList.get(0);
        loginMemberId = board1.getMember().getMemberId(); //board1 작성자를 현재 사용자라고 가정.
        Board board2 = boardList.get(1);
        Board board3 = boardList.get(2);
        Board board5 = boardList.get(4);

        Category categoryA = categoryRepository.save(createCategory("디지털기기"));
        Category categoryB = categoryRepository.save(createCategory("게임/취미"));

        board1.changeCategory(categoryA.getCategoryId());
        board2.changeCategory(categoryA.getCategoryId());
        board3.changeCategory(categoryB.getCategoryId());
        board5.changeCategory(categoryB.getCategoryId());

        board1.changeTitle("닌텐도 팔아요!");
        board3.changeTitle("닌텐도 같이할 사람~");
        board5.changeTitle("싸게 사고 싶어요, 닌텐도...");

        board3.changePostState(PostState.COMPLETION);

        createBoardImg(board1,"sfio_이미지.jpeg","2022/01/16");
        createBoardImg(board1,"asdf12D_이미지.jpeg","2022/01/16");

        Comment parentComment = createComment(board2.getMember(), board1, "깍아주세요");
        createComment(board1.getMember(),board1,"좋아요",parentComment);

        createHeart(board1.getMember(),board1);
    }

    /**
     * board1,2 (디지털기기), board3,5(게임/취미)
     * board1,3,5 title = ~닌텐도~
     * board 3만 게시글상태 - 완료, 나머진 모두 판매중
     * board1 이미지 2개, 댓글 2개, 하트 1개 -게시글작성자
     *
     * --> board5 -> board1 -> board3 순으로 출력 되어야 함.
     */
    @Test
    void 판매글리스트_조회() throws Exception{
        //given
        Category categoryA = categoryRepository.findByName("디지털기기");
        Category categoryB = categoryRepository.findByName("게임/취미");

        List<Long> categories = List.of(categoryA.getCategoryId(), categoryB.getCategoryId());
        SearchDTO searchDTO = createSearchDTO("닌텐도", categories);

        //when
        List<BoardResDTO> result = boardService.getAllBoardList(searchDTO, loginMemberId);

        //then
        assertThat(result).extracting("boardId").containsExactly(5L,1L,3L);
        for (BoardResDTO boardResDTO : result){
            System.out.println(boardResDTO);
        }
    }

    private SearchDTO createSearchDTO(String keyword, List<Long> categories) {
        return SearchDTO.builder()
                .keyword(keyword)
                .categoryIdList(categories)
                .build();
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
