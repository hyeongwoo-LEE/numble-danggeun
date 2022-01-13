package com.numble.numbledanggeun.service.heart;

import com.numble.numbledanggeun.domain.heart.Heart;

public interface HeartService {

    Heart heart(Long memberId, Long boardId);

    void cancelHeart(Long memberId, Long boardId);
}
