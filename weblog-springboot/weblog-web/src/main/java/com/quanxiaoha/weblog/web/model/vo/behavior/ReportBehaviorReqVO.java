package com.quanxiaoha.weblog.web.model.vo.behavior;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "批量上报行为事件")
public class ReportBehaviorReqVO {

    @NotEmpty(message = "事件列表不能为空")
    private List<BehaviorEventItem> events;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BehaviorEventItem {
        private Integer eventType;
        private Long articleId;
        private Long tagId;
        private String keyword;
        private Integer durationSeconds;
    }
}
