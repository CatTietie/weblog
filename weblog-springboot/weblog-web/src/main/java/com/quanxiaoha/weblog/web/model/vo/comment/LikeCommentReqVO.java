package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeCommentReqVO {

    @NotNull(message = "评论 ID 不能为空")
    private Long commentId;

    @NotNull(message = "点赞状态不能为空")
    private Boolean liked;
}
