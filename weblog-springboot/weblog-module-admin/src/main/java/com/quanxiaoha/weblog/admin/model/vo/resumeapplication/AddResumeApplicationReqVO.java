package com.quanxiaoha.weblog.admin.model.vo.resumeapplication;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "新增投递记录入参 VO")
public class AddResumeApplicationReqVO {

    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;

    @NotBlank(message = "公司名称不能为空")
    private String company;

    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime applyTime;

    private String channel;

    private Integer status;

    private String remark;
}
