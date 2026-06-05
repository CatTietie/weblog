package com.quanxiaoha.weblog.admin.staticsite.deploy;

import com.quanxiaoha.weblog.common.domain.dos.StaticSiteConfigDO;

public interface DeployStrategy {

    void deploy(String outputDir, StaticSiteConfigDO config) throws Exception;
}
