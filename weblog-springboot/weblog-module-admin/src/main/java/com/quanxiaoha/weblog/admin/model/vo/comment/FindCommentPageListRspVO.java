package com.quanxiaoha.weblog.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询评论分页数据出参 VO")
public class FindCommentPageListRspVO {

    private Long id;

    private String content;

    private String username;

    private String articleTitle;

    private LocalDateTime createTime;

    private Integer status;

    private String statusName;
}
