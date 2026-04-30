package com.quanxiaoha.weblog.admin.model.vo.user;

import com.quanxiaoha.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户分页查询 VO")
public class UserPageListReqVO extends BasePageQuery {

    private String username;

    private Integer status;

    private Long roleId;
}
