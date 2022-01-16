package com.numble.numbledanggeun.service.comment;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.category.CategoryRepository;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.comment.CommentRepository;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.comment.CommentDTO;
import com.numble.numbledanggeun.dto.comment.CommentResDTO;
import com.numble.numbledanggeun.dto.comment.CommentUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CommentServiceImplTest {

    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired MemberRepository memberRepository;

    private Member member;
    private Board board;

    /**
     * member 1, category 1, board 1 생성
     */
    @BeforeEach
    void before(){
        Member member = createMember("beforeEmail@naver.com", "beforeName", "beforeNick", "010-1111-1111");
        Category category = createCategory("beforeCategory");
        Board board = createBoard(member, category, "beforeTitle", "beforeContent");

        this.member = member;
        this.board = board;
    }

    @Test
    void 댓글_작성() throws Exception{
        //given
        CommentDTO commentDTO = createCommentDTO(board.getBoardId(), "댓글작성테스트");

        //when
        Comment comment = commentService.register(commentDTO, member.getMemberId());

        //then
        assertThat(comment.getCommentId()).isNotNull();
        assertThat(comment.getBoard()).isEqualTo(board);
        assertThat(comment.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(comment.getContent()).isEqualTo(commentDTO.getContent());
        assertThat(comment.getIsExist()).isTrue();
    }

    @Test
    void 대댓글_작성() throws Exception{
        //given
        //부모댓글 생성
        Member parentMember = createMember("parent@naver.com", "parentName", "parentNick", "010-2222-2222");
        Comment parent = createComment(parentMember, board, "부모댓글입니다.");

        CommentDTO commentDTO = createCommentDTO(board.getBoardId(), "대댓글작성테스트",parent.getCommentId());

        //when
        Comment comment = commentService.register(commentDTO, member.getMemberId());

        //then
        assertThat(comment.getCommentId()).isNotNull();
        assertThat(comment.getBoard()).isEqualTo(board);
        assertThat(comment.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(comment.getParent()).isEqualTo(parent);
        assertThat(comment.getContent()).isEqualTo(commentDTO.getContent());
        assertThat(comment.getIsExist()).isTrue();
    }

    @Test
    void 댓글_수정() throws Exception{
        //given
        //댓글 생성
        Comment comment = createComment(member, board, "댓글수정테스트");

        //commentUpdateDTO 생성
        CommentUpdateDTO commentUpdateDTO = createCommentUpdateDTO(comment.getCommentId(), "댓글수정할래요.");

        //when
        commentService.modify(commentUpdateDTO);

        //then
        Comment findComment = commentRepository.findById(comment.getCommentId()).get();

        assertThat(findComment.getCommentId()).isEqualTo(comment.getCommentId());
        assertThat(findComment.getBoard()).isEqualTo(board);
        assertThat(findComment.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(findComment.getContent()).isEqualTo(commentUpdateDTO.getContent());
        assertThat(findComment.getIsExist()).isTrue();
    }

    @Test
    void 댓글_삭제() throws Exception{
        //given
        //댓글 생성
        Comment comment = createComment(member, board, "댓글삭제테스트");

        //when
        commentService.remove(comment.getCommentId());

        //then
        Comment findComment = commentRepository.findById(comment.getCommentId()).get();
        assertThat(findComment.getIsExist()).isFalse();
    }

    @Test
    void 댓글_삭제_자식댓글존재o() throws Exception{
        //given
        //댓글 생성
        Comment comment = createComment(member, board, "댓글삭제테스트");

        //자식댓글 생성
        Member childMember = createMember("child@naver.com", "childName", "childNick", "010-2222-2222");
        Comment child = createComment(childMember, board, "대댓글입니다.",comment);

        //when
        commentService.remove(comment.getCommentId());

        //then
        //부모댓글 검증
        Comment findComment = commentRepository.findById(comment.getCommentId()).get();
        assertThat(findComment.getIsExist()).isFalse();

        //자식댓글 검증
        Comment findChild = commentRepository.findById(child.getCommentId()).get();
        assertThat(findChild).isEqualTo(child);
    }

    @Test
    void 대댓글_삭제() throws Exception{
        //given
        Member parentMember = createMember("parent@naver.com", "parentName", "parentNick", "010-2222-2222");
        Comment parent = createComment(parentMember, board, "부모댓글입니다.");

        //대댓글 생성
        Comment comment = createComment(member, board, "대댓글입니다.",parent);

        //when
        commentService.remove(comment.getCommentId());

        //then
        //대댓글 검증
        Comment findComment = commentRepository.findById(comment.getCommentId()).get();
        assertThat(findComment.getIsExist()).isFalse();

        //부모댓글 검증
        Comment findParent = commentRepository.findById(parent.getCommentId()).get();
        assertThat(findParent).isEqualTo(parent);
    }

    /**
     * 댓글 5개 생성
     * comment4
     * comment2 - (childComment3,chileComment5)
     * comment1
     *
     * 으로 출력되어야 함. - 댓글(내림차순-최신순), 대댓글(오름차순-오래된순)
     */
    @Test
    void 댓글리스트_조회() throws Exception{
        //given
        Comment comment1 = createComment(member, board, "comment1");
        Comment comment2 = createComment(member, board, "comment2");
        Comment comment3 = createComment(member, board, "comment3",comment2);
        Comment comment4 = createComment(member, board, "comment4");
        Comment comment5 = createComment(member, board, "comment5",comment2);

        //when
        List<CommentResDTO> result = commentService.getCommentList(board.getBoardId());

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getContent()).isEqualTo("comment4");
        assertThat(result.get(1).getContent()).isEqualTo("comment2");
        assertThat(result.get(2).getContent()).isEqualTo("comment1");

        //두번째 댓글 - childList 검증
        CommentResDTO second = result.get(1);
        assertThat(second.getChildList().size()).isEqualTo(2);
        assertThat(second.getChildList().get(0).getContent()).isEqualTo("comment3");
        assertThat(second.getChildList().get(1).getContent()).isEqualTo("comment5");

        for (CommentResDTO dto : result){
            System.out.println(dto);
        }
    }

    private CommentUpdateDTO createCommentUpdateDTO(Long commentId, String content) {
        return CommentUpdateDTO.builder()
                .commentId(commentId)
                .content(content)
                .build();
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

    private CommentDTO createCommentDTO(Long boardId, String content, Long parentId) {
        return CommentDTO.builder()
                .boardId(boardId)
                .content(content)
                .parentId(parentId)
                .build();
    }

    private CommentDTO createCommentDTO(Long boardId, String content) {
        return CommentDTO.builder()
                .boardId(boardId)
                .content(content)
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
                .folderPath("2022/01/17")
                .filename("a13sa_file.jpeg")
                .build();
        memberRepository.save(member);
        return member;
    }
}