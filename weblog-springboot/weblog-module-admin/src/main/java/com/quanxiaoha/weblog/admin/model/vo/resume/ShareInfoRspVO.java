package com.quanxiaoha.weblog.admin.model.vo.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareInfoRspVO {

    private Boolean shareEnabled;

    private String shareCode;
}
