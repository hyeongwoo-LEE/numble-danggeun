package com.numble.numbledanggeun.service.heart;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.heart.HeartRepository;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.handler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class HeartServiceImpl implements HeartService{

    private final HeartRepository heartRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 관심추가
     */
    @Transactional
    @Override
    public Heart heart(Long memberId, Long boardId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new CustomException("존재하지 않는 회원입니다."));

        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new CustomException("존재하지 않는 글입니다."));

        Heart heart = new Heart();
        heart.setMember(member);
        heart.setBoard(board);

        return heartRepository.save(heart);
    }

    /**
     * 관심삭제
     */
    @Transactional
    @Override
    public void cancelHeart(Long memberId, Long boardId) {

        heartRepository.deleteHeart(memberId,boardId);

    }
}
