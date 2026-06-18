package com.quanxiaoha.weblog.admin.model.vo.resumeapplication;

import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindResumeApplicationStatisticsRspVO {
    private List<String> dates;
    private List<Long> dailyCounts;
    private List<PieDataVO> statusDistribution;
    private Long totalCount;
}
