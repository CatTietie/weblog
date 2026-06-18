package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentReplyRspVO {

    private Long id;

    private String content;

    private LocalDateTime createTime;

    private String username;

    private String replyToUsername;

    private Integer likeCount;
}
