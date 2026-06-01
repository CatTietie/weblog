package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishCommentReqVO {

    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过 500 字")
    private String content;

    private Long parentId;
}
