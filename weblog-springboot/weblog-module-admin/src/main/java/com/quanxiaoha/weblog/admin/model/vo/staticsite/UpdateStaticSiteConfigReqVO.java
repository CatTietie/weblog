package com.quanxiaoha.weblog.admin.model.vo.staticsite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStaticSiteConfigReqVO {

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
}
