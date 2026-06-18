package com.quanxiaoha.weblog.admin.model.vo.sensitiveword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddSensitiveWordReqVO {

    @NotBlank(message = "敏感词不能为空")
    private String word;

    private String category;
}
