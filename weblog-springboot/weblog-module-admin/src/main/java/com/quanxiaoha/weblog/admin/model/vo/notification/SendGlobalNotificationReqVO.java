package com.quanxiaoha.weblog.admin.model.vo.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendGlobalNotificationReqVO {

    @NotBlank(message = "通知标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    @Size(max = 500, message = "内容长度不能超过500个字符")
    private String content;
}
