package com.numble.numbledanggeun.dto.board;

import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    @Enumerated(EnumType.STRING) //TODO 필요?
    private Category category;

    @NotNull
    private int price;

    @NotBlank
    private List<MultipartFile> imageFiles;

    public Board toEntity(Long memberId){
        return Board.builder()
                .member(Member.builder().memberId(memberId).build())
                .title(title)
                .content(content)
                .category(category)
                .price(price)
                .postState(PostState.SALE)
                .build();
    }

}
