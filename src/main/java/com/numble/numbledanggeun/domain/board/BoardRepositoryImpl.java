package com.numble.numbledanggeun.domain.board;

import com.numble.numbledanggeun.domain.category.QCategory;
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
     * 글 리스트 조회
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
