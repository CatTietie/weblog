package com.quanxiaoha.weblog.common.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023/11/13 11:22
 * @description: 全局静态变量
 **/
public interface Constants {
    /**
     * 月-日 格式
     */
    DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
}
