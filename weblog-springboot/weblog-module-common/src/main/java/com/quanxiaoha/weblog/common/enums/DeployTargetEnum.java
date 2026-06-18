package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeployTargetEnum {

    GITHUB("GITHUB", "GitHub Pages"),
    OSS("OSS", "阿里云 OSS"),
    LOCAL("LOCAL", "仅本地");

    private final String code;
    private final String description;
}
