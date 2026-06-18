package com.quanxiaoha.weblog.admin.model.vo.resumeapplication;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新投递记录入参 VO")
public class UpdateResumeApplicationReqVO {

    @NotNull(message = "记录 ID 不能为空")
    private Long id;

    private String company;

    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime applyTime;

    private String channel;

    private Integer status;

    private String remark;
}
