package com.quanxiaoha.weblog.admin.model.vo.staticsite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindGenTaskPageListReqVO {

    private Long current = 1L;

    private Long size = 10L;
}
