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
import com.numble.numbledanggeun.dto.board.BoardDTO;
import com.numble.numbledanggeun.dto.board.BoardUpdateDTO;
import com.numble.numbledanggeun.handler.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BoardServiceImplTest {

    @Autowired BoardService boardService;
    @Autowired BoardRepository boardRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BoardImgRepository boardImgRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired HeartRepository heartRepository;

    private Long boardImgId;
    private Long commentId;
    private Long heartId;

    /**
     * member 1, category 1, board 1, boardImg 1, comment 1, heart 1 생성
     */
    @BeforeEach
    void before(){
        Member member = createMember("beforeEmail@naver.com", "beforeName", "beforeNick", "010-1111-1111");
        Category category = createCategory("beforeCategory");
        Board board = createBoard(member, category, "beforeTitle", "beforeContent");
        BoardImg boardImg = createBoardImg(board, UUID.randomUUID().toString() + "filename", "/before");
        boardImgId = boardImg.getBoardImgId();
        Comment comment = createComment(member, board, "testContent");
        commentId = comment.getCommentId();
        Heart heart = createHeart(member, board);
        heartId = heart.getHeartId();
    }

    @Test
    void 중고거래글_생성() throws Exception{
        //given
        Member member = createMember(
                "test@naver.com", "testName", "testNick", "010-0000-0000");

        Category category = createCategory("가전제품");

        //mock 이미지 파일 생성
        List<MultipartFile> imgFiles = new ArrayList<>();
        MultipartFile file1 = createImage("file1", "filename-1.jpeg");
        MultipartFile file2 = createImage("file2", "filename-2.jpeg");
        imgFiles.add(file1);
        imgFiles.add(file2);

        //DTO 생성
        BoardDTO boardDTO = createBoardDTO(category, "testTitle", "testContent", 1000, imgFiles);

        //when
        Board board = boardService.register(boardDTO, member.getMemberId());

        //then
        assertThat(boardRepository.findAll().size()).isEqualTo(2);
        //board 검증
        assertThat(board.getBoardId()).isNotNull();
        assertThat(board.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(board.getCategory().getCategoryId()).isEqualTo(category.getCategoryId());
        assertThat(board.getTitle()).isEqualTo(boardDTO.getTitle());
        assertThat(board.getContent()).isEqualTo(boardDTO.getContent());
        assertThat(board.getPrice()).isEqualTo(boardDTO.getPrice());
        assertThat(board.getPostState()).isEqualTo(PostState.SALE);

        //boardImg 검증
        List<BoardImg> boardImgList = boardImgRepository.findBoardImgByBoard(board);
        assertThat(boardImgList.size()).isEqualTo(2);
    }

    @Test
    void 중고거래글_수정() throws Exception{
        //given
        Board board = boardRepository.findAll().get(0);

        Category updateCategory = createCategory("updateCategory");

        List<MultipartFile> imgFiles = new ArrayList<>();
        MultipartFile file = createImage("updateFile", "updateFilename.jpeg");
        imgFiles.add(file);

        BoardUpdateDTO boardUpdateDTO = createBoardUpdateDTO(board.getBoardId(), updateCategory, "updateTile", "updateContent",
                1, imgFiles);

        //when
        boardService.modify(boardUpdateDTO);

        //then
        Board findBoard = boardRepository.findById(board.getBoardId()).get();

        //board 검증
        assertThat(findBoard.getBoardId()).isEqualTo(board.getBoardId());
        assertThat(findBoard.getMember().getMemberId()).isEqualTo(board.getMember().getMemberId());
        assertThat(findBoard.getCategory().getCategoryId()).isEqualTo(updateCategory.getCategoryId());
        assertThat(findBoard.getTitle()).isEqualTo(boardUpdateDTO.getTitle());
        assertThat(findBoard.getContent()).isEqualTo(boardUpdateDTO.getContent());
        assertThat(findBoard.getPrice()).isEqualTo(boardUpdateDTO.getPrice());
        assertThat(findBoard.getPostState()).isEqualTo(PostState.SALE);

        //boardImg 검증
        List<BoardImg> boardImgList = boardImgRepository.findBoardImgByBoard(board);
        assertThat(boardImgList.size()).isEqualTo(1);


    }

    @Test
    void 중고거래글_수정_price_0미만() throws Exception{
        //given
        Board board = boardRepository.findAll().get(0);

        Category updateCategory = createCategory("updateCategory");

        List<MultipartFile> imgFiles = new ArrayList<>();
        MultipartFile file = createImage("updateFile", "updateFilename.jpeg");
        imgFiles.add(file);

        BoardUpdateDTO boardUpdateDTO = createBoardUpdateDTO(board.getBoardId(), updateCategory, "updateTile", "updateContent",
                0, imgFiles);

        //when
        CustomException e = assertThrows(CustomException.class,
                () -> boardService.modify(boardUpdateDTO));

        //then
        assertThat(e.getMessage()).isEqualTo("가격이 0원 미만일 수 없습니다.");
    }

    @Test
    void 중고거래글_게시상태_수정() throws Exception{
        //given
        Board board = boardRepository.findAll().get(0);

        //when
        //SALE -> COMPLETION 으로 변경
        boardService.modifyPostState(board.getBoardId(), PostState.COMPLETION);

        //then
        assertThat(board.getPostState()).isEqualTo(PostState.COMPLETION);
    }

    @Test
    void 이미지_삭제() throws Exception{
        //given
        Board board = boardRepository.findAll().get(0);

        //when
        boardService.removeImage(board.getBoardId());

        //then
        List<BoardImg> boardImgList = boardImgRepository.findBoardImgByBoard(board);
        assertThat(boardImgList.size()).isEqualTo(0);

    }

    /*
    @Test
    void 글삭제() throws Exception{
        //given
        Board board = boardRepository.findAll().get(0);

        //when
        boardService.remove(board.getBoardId());

        //then
        //삭제한 board1의 boardImg1 레코드 검색 (boardImg 삭제)
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class,
                () -> (boardImgRepository.findById(boardImgId)).get());

        //삭제한 board1의 작성한 comment 레코드 검색 (comment 삭제)
        NoSuchElementException e2 = assertThrows(NoSuchElementException.class,
                () -> (commentRepository.findById(commentId)).get());

        //삭제한 board1에 추가한 heart 레코드 검색 (heart 삭제)
        NoSuchElementException e3 = assertThrows(NoSuchElementException.class,
                () -> (heartRepository.findById(heartId)).get());

        assertThat(e1.getMessage()).isEqualTo("No value present");
        assertThat(e2.getMessage()).isEqualTo("No value present");
        assertThat(e3.getMessage()).isEqualTo("No value present");

    }*/

    private BoardUpdateDTO createBoardUpdateDTO(Long boardId,Category category, String title, String content, int price, List<MultipartFile> imageFiles) {
        return BoardUpdateDTO.builder()
                .boardId(boardId)
                .categoryId(category.getCategoryId())
                .title(title)
                .content(content)
                .price(price)
                .imageFiles(imageFiles)
                .build();
    }

    private MultipartFile createImage(String name, String originalFilename) {
        return new MockMultipartFile(name, originalFilename, "image/jpeg", "some-image".getBytes());
    }

    private BoardDTO createBoardDTO(Category category, String title, String content, int price, List<MultipartFile> imageFiles) {

        return BoardDTO.builder()
                .categoryId(category.getCategoryId())
                .title(title)
                .content(content)
                .price(price)
                .imageFiles(imageFiles)
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

    private BoardImg createBoardImg(Board board, String filename, String folderPath) {

        BoardImg boardImg = BoardImg.builder()
                .folderPath(folderPath)
                .filename(filename)
                .build();
        boardImg.setBoard(board);
        boardImgRepository.save(boardImg);

        return boardImg;
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

    private Heart createHeart(Member member, Board board) {
        Heart heart = new Heart();
        heart.setMember(member);
        heart.setBoard(board);
        heartRepository.save(heart);
        return heart;
    }

}