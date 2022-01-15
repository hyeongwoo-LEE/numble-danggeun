package com.numble.numbledanggeun.dto.boardImg;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardImgDTO {

    private String folderPath;

    private String filename;

    public BoardImgDTO(BoardImg boardImg){
        folderPath = boardImg.getFolderPath();
        filename = boardImg.getFilename();
    }
}
