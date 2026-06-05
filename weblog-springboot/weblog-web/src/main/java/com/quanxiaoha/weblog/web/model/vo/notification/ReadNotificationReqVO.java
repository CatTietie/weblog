package com.quanxiaoha.weblog.web.model.vo.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadNotificationReqVO {

    @NotNull(message = "通知 ID 不能为空")
    private Long id;
}
