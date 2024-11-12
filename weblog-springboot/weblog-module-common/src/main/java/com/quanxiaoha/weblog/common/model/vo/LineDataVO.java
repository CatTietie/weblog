package com.quanxiaoha.weblog.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author lixing
 * @version V1.0
 * Copyright (c) 2024, lixing@hwadee.com All Rights Reserved.
 * @ProjectName:weblog-springboot
 * @Title: LineDataVO
 * @Package com.quanxiaoha.weblog.common.model.vo
 * @Description: 折线图统计VO
 * @date 2024/11/12 21:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineDataVO {
    private LocalDate xData;
    private Long seriesData;
}
