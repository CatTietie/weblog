package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentListRspVO {

    private Long id;

    private String content;

    private LocalDateTime createTime;

    private String username;

    private Integer likeCount;

    private List<FindCommentReplyRspVO> replies;
}
