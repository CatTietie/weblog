package com.quanxiaoha.weblog.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixing
 * @version V1.0
 * Copyright (c) 2024, lixing@hwadee.com All Rights Reserved.
 * @ProjectName:weblog-springboot
 * @Title: PieDataVO
 * @Package com.quanxiaoha.weblog.common.model.vo
 * @Description: 扇形图数据VO
 * @date 2024/11/11 20:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PieDataVO {
    private Long value;
    private String name;
}
