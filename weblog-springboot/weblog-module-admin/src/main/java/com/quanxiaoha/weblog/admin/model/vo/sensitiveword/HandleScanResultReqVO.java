package com.quanxiaoha.weblog.admin.model.vo.sensitiveword;

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
public class HandleScanResultReqVO {

    @NotEmpty(message = "结果ID列表不能为空")
    private List<Long> ids;

    private Integer action;
}
