package com.quanxiaoha.weblog.admin.model.vo.resume;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "上传简历入参 VO")
public class UploadResumeReqVO {

    private Long resumeId;

    private String name;
}
