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
@TableName("t_static_gen_page")
public class StaticGenPageDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String pageType;

    private Long refId;

    private Integer pageNumber;

    private String contentHash;

    private String outputPath;

    private LocalDateTime lastGeneratedTime;

    private LocalDateTime sourceUpdateTime;
}
