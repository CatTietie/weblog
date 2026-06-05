package com.quanxiaoha.weblog.admin.model.vo.sensitiveword;

import com.quanxiaoha.weblog.common.model.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindScanResultPageListReqVO extends BasePageQuery {

    private Long taskId;

    private Integer handled;
}
