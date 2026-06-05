package com.quanxiaoha.weblog.admin.model.vo.staticsite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenTaskProgressRspVO {

    private Long taskId;

    private String status;

    private Integer totalPages;

    private Integer generatedPages;

    private String errorMessage;
}
