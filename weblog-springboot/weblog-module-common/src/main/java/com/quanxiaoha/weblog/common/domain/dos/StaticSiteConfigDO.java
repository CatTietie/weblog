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
@TableName("t_static_site_config")
public class StaticSiteConfigDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String commentProvider;

    private String commentConfig;

    private Boolean githubEnabled;

    private String githubToken;

    private String githubRepo;

    private String githubBranch;

    private String githubCname;

    private Boolean ossEnabled;

    private String ossEndpoint;

    private String ossAccessKeyId;

    private String ossAccessKeySecret;

    private String ossBucketName;

    private String ossBasePath;

    private Boolean autoDeploy;

    private String outputBaseUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
