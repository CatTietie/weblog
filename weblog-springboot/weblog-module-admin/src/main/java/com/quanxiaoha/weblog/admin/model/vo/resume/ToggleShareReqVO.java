package com.quanxiaoha.weblog.admin.model.vo.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToggleShareReqVO {

    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;

    @NotNull(message = "分享状态不能为空")
    private Boolean enabled;
}
