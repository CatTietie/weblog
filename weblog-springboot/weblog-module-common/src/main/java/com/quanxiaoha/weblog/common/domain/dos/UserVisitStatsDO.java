package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("user_visit_stats")
public class UserVisitStatsDO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private LocalDateTime visit_time;

    private String user_ip;

    private String device_type;

    private String browser_name;

    private String os_name;

    private Boolean is_new_visitor;

    private String page_url;

}