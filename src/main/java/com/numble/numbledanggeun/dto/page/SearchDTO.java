package com.numble.numbledanggeun.dto.page;

import com.numble.numbledanggeun.domain.board.PostState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {

    private String keyword;

    private List<Long> categoryIdList;

    private PostState postState;
}
