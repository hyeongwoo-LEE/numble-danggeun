package com.numble.numbledanggeun.dto.comment;

import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.dto.memberImg.MemberImgDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentResDTO {

    private Long commentId;

    private Long writerId;

    private String writerNickname;

    private String content;

    private Boolean isExist;

    private List<CommentResDTO> childList = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime createDate;

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime updateDate;

    private MemberImgDTO memberImgDTO;

    public CommentResDTO(Comment comment){
        commentId = comment.getCommentId();
        writerId = comment.getMember().getMemberId();
        writerNickname = comment.getMember().getNickname();
        content = comment.getContent();
        isExist = comment.getIsExist();
        createDate = comment.getCreateDate();
        updateDate = comment.getUpdateDate();

        //대댓글 리스트
        childList = new ArrayList<>();
        if (comment.getChildList().size() > 0){
            childList = comment.getChildList().stream()
                    .map(entity -> new CommentResDTO(entity)).collect(Collectors.toList());
        }

        //회원 프로필 사진
        if (comment.getMember().getFolderPath() != null && comment.getMember().getFilename() != null){
            memberImgDTO = new MemberImgDTO(comment.getMember().getFolderPath(),comment.getMember().getFilename());
        }

    }

}
