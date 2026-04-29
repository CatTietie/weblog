package com.quanxiaoha.weblog.admin.model.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户信息响应 VO")
public class UserRspVO {

    private Long id;

    private String username;

    private String role;

    private String roleName;

    private Integer status;

    private String statusName;

    private LocalDateTime createTime;

}
