package com.numble.numbledanggeun.domain.board;

import com.numble.numbledanggeun.domain.category.QCategory;
import com.numble.numbledanggeun.domain.heart.QHeart;
import com.numble.numbledanggeun.domain.member.QMember;
import com.numble.numbledanggeun.dto.board.BoardDTO;
import com.numble.numbledanggeun.dto.board.BoardResDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.aspectj.util.LangUtil.isEmpty;

public class BoardRepositoryImpl implements BoardRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 판매글 리스트 조회
     */
    @Override
    public List<Object[]> getAllBoardList(SearchDTO searchDTO) {

        QBoard board = QBoard.board;
        QCategory category = QCategory.category;

        List<Tuple> result = queryFactory
                .select(
                        board, board.commentList.size(), board.heartList.size())
                .from(board)
                .leftJoin(category)
                .on(category.categoryId.eq(board.category.categoryId))
                .where(titleContain(searchDTO.getKeyword()), (categoriesEq(searchDTO.getCategoryIdList())))
                .orderBy(board.postState.desc(), board.updateDate.desc())
                .fetch();

        return result.stream().map(t -> t.toArray()).collect(Collectors.toList());
    }

    /**
     * 글 작성자의 판매상품 리스트 조회
     */
    @Override
    public List<Object[]> getBoardListByMemberId(SearchDTO searchDTO) {

        QBoard board = QBoard.board;

        List<Tuple> result = queryFactory
                .select(board, board.commentList.size(), board.heartList.size())
                .from(board)
                .where(board.member.memberId.eq(searchDTO.getMemberId()),
                        postStateEq(searchDTO.getPostState()))
                .orderBy(board.postState.desc(), board.updateDate.desc())
                .fetch();

        return result.stream().map(t -> t.toArray()).collect(Collectors.toList());
    }

    /**
     * 회원의 관심 목록
     */
    @Override
    public List<Object[]> getBoardListOfHeart(Long principalId) {
        QBoard board = QBoard.board;
        QHeart heart = QHeart.heart;

//        List<Tuple> result = queryFactory
//                .select(board, board.commentList.size(), board.heartList.size())
//                .from(board)
//                .join(heart).on(heart.board.boardId.eq(board.boardId))
//                .where(heart.member.memberId.eq(principalId))
//                .fetch();

        List<Tuple> result = queryFactory
                .select(board, board.commentList.size(), board.heartList.size())
                .from(board)
                .where(board.boardId.in(
                        JPAExpressions
                                .select(heart.board.boardId)
                                .from(heart)
                                .where(heart.member.memberId.eq(principalId))
                ))
                .orderBy(board.postState.desc(), board.updateDate.desc())
                .fetch();

        return result.stream().map(t -> t.toArray()).collect(Collectors.toList());
    }

    /**
     * 판매글 작성자의 다른 판매글 미리보기 리스트 - 현재 상세보기 글 제외
     */
    @Override
    public List<Board> getPreviewBoardListByMemberId(SearchDTO searchDTO, Long boardId) {
        QBoard board = QBoard.board;

        return queryFactory
                .selectFrom(board)
                .where(board.member.memberId.eq(searchDTO.getMemberId()),
                        board.boardId.ne(boardId))
                .orderBy(board.postState.desc(), board.updateDate.desc())
                .limit(4)
                .fetch();
    }

    private BooleanExpression postStateEq(PostState postState) {
        QBoard board = QBoard.board;
        return postState!=null ? board.postState.eq(postState):null;
    }

    private BooleanBuilder categoriesEq(List<Long> categories) {
        QCategory category = QCategory.category;

        if (categories != null && categories.size() > 0){
            BooleanBuilder builder = new BooleanBuilder();
            for (Long id : categories){
                builder.or(category.categoryId.eq(id));
            }
            return builder;
        }else{
            return null;
        }
    }

    private BooleanExpression titleContain(String keyword) {
        QBoard board = QBoard.board;
        return !isEmpty(keyword) ? board.title.contains(keyword):null;
    }
}
