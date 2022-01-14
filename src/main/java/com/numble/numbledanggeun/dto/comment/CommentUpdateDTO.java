package com.numble.numbledanggeun.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateDTO {

    @NotNull
    private Long commentId;

    @NotNull
    private String content;
}
