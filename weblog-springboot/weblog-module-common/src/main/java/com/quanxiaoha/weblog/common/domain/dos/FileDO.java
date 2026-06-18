package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_file")
public class FileDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String originalName;

    private String url;

    private Long fileSize;

    private String contentType;

    private Long tenantId;

    private LocalDateTime createTime;
}
