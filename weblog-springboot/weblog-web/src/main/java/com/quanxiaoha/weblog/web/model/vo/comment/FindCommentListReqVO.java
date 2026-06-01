package com.quanxiaoha.weblog.web.model.vo.comment;

import com.quanxiaoha.weblog.common.model.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentListReqVO extends BasePageQuery {

    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;
}
