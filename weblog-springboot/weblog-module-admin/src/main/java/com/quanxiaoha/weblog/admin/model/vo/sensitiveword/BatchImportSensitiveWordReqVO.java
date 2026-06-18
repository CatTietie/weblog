package com.quanxiaoha.weblog.admin.model.vo.sensitiveword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchImportSensitiveWordReqVO {

    @NotEmpty(message = "导入词列表不能为空")
    private List<String> words;

    private String category;
}
