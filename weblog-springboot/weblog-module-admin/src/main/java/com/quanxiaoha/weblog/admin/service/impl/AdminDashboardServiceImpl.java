package com.quanxiaoha.weblog.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.FindDashboardStatisticsInfoRspVO;
import com.quanxiaoha.weblog.admin.service.AdminDashboardService;
import com.quanxiaoha.weblog.common.constant.Constants;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticlePublishCountDO;
import com.quanxiaoha.weblog.common.domain.dos.StatisticsArticlePVDO;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import com.quanxiaoha.weblog.common.model.vo.LineDataVO;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: Group 5
 * @date: 2023-09-15 14:03
 * @description: 仪表盘
 **/
@Service
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;
    @Autowired
    private ArticleUpdateHistoryMapper articleUpdateHistoryMapper;

    @Autowired
    private StatisticsMapper statisticsMapper;

    /**
     * 获取仪表盘基础统计信息
     *
     * @return
     */
    @Override
    public Response findDashboardStatistics() {
        // 查询文章总数
        Long articleTotalCount = articleMapper.selectCount(Wrappers.emptyWrapper());

        // 查询分类总数
        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        // 查询标签总数
        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        // 总浏览量
        List<ArticleDO> articleDOS = articleMapper.selectAllReadNum();
        Long pvTotalCount = 0L;

        if (!CollectionUtils.isEmpty(articleDOS)) {
            // 所有 read_num 相加
            pvTotalCount = articleDOS.stream().mapToLong(ArticleDO::getReadNum).sum();
        }

        // 组装 VO 类
        FindDashboardStatisticsInfoRspVO vo = FindDashboardStatisticsInfoRspVO.builder()
                .articleTotalCount(articleTotalCount)
                .categoryTotalCount(categoryTotalCount)
                .tagTotalCount(tagTotalCount)
                .pvTotalCount(pvTotalCount)
                .build();

        return Response.success(vo);
    }

    /**
     * 获取文章发布热点统计信息
     *
     * @return
     */
    @Override
    public Response findDashboardPublishArticleStatistics() {
        // 当前日期
        LocalDate currDate = LocalDate.now();

        // 当前日期倒退一年的日期
        LocalDate startDate = currDate.minusYears(1);

        // 查找这一年内，每日发布的文章数量
        List<ArticlePublishCountDO> articlePublishCountDOS = articleMapper.selectDateArticlePublishCount(startDate, currDate);

        Map<LocalDate, Long> map = null;
        if (!CollectionUtils.isEmpty(articlePublishCountDOS)) {
            // DO 转 Map
            Map<LocalDate, Long> dateArticleCountMap = articlePublishCountDOS.stream()
                    .collect(Collectors.toMap(ArticlePublishCountDO::getDate, ArticlePublishCountDO::getCount));

            // 有序 Map, 返回的日期文章数需要以升序排列
            map = Maps.newLinkedHashMap();
            // 从上一年的今天循环到今天
            for (; startDate.isBefore(currDate) || startDate.isEqual(currDate); startDate = startDate.plusDays(1)) {
                // 以日期作为 key 从 dateArticleCountMap 中取文章发布总量
                Long count = dateArticleCountMap.get(startDate);
                // 设置到返参 Map
                map.put(startDate, Objects.isNull(count) ? 0 : count);
            }
        }

        return Response.success(map);
    }

    /**
     * 获取文章最近一周 PV 访问量统计信息
     *
     * @return
     */
    @Override
    public Response findDashboardPVStatistics() {
        // 查询最近一周的 PV 访问量记录
        List<StatisticsArticlePVDO> articlePVDOS = articlePVMapper.selectLatestWeekRecords();

        FindDashboardPVStatisticsInfoRspVO vo = null;
        if (!CollectionUtils.isEmpty(articlePVDOS)) {
            // 转 Map, 方便后续通过日期获取 PV 访问量
            Map<LocalDate, Long> pvDateCountMap = articlePVDOS.stream()
                    .collect(Collectors.toMap(StatisticsArticlePVDO::getPvDate, StatisticsArticlePVDO::getPvCount));

            // 日期集合
            List<String> pvDates = Lists.newArrayList();
            // PV 集合
            List<Long> pvCounts = Lists.newArrayList();

            // 当前日期
            LocalDate currDate = LocalDate.now();
            // 一周前
            LocalDate tmpDate = currDate.minusMonths(1);
            // 从一周前开始循环
            for (; tmpDate.isBefore(currDate) || tmpDate.isEqual(currDate); tmpDate = tmpDate.plusDays(1)) {
                // 设置对应日期的 PV 访问量
                pvDates.add(tmpDate.format(Constants.MONTH_DAY_FORMATTER));
                Long pvCount = pvDateCountMap.get(tmpDate);
                pvCounts.add(Objects.isNull(pvCount) ? 0 : pvCount);
            }

            vo = FindDashboardPVStatisticsInfoRspVO.builder()
                    .pvDates(pvDates)
                    .pvCounts(pvCounts)
                    .build();
        }

        return Response.success(vo);
    }

    @Override
    public Response findArticlePvCount() {
        List<JSONObject> res = articleMapper.getArticleReadNum();

        // 创建两个列表分别存储 title 和 readNum
        List<String> titles = new ArrayList<>();
        List<Integer> readNums = new ArrayList<>();

        // 遍历 res 列表，提取 title 和 readNum
        for (JSONObject item : res) {
            titles.add(item.getString("title"));
            readNums.add(item.getInteger("readNum"));
        }

        // 将两个列表封装到一个 JSONObject 中
        JSONObject result = new JSONObject();
        result.put("titles", titles);
        result.put("readNums", readNums);

        return Response.success(result);
    }

    /**
     * 统计文章更新频率
     *
     * @return
     */
    @Override
    public Response countArticleUpdateTimes() {
        List<JSONObject> queryJsonObject = articleUpdateHistoryMapper.countUpdateTimes();
        return Response.success(convertBarData(queryJsonObject, "title", "count"));
    }

    @Override
    public Response orderArticlePvCount() {
        List<JSONObject> queryJsonObject = articleMapper.orderArticle();
        return Response.success(convertBarData(queryJsonObject, "title", "readNum"));
    }

    @Override
    public Response countCategory() {
        return Response.success(statisticsMapper.categoryAndArticleCount());
    }

    @Override
    public Response countTags() {

        List<PieDataVO> dataVOS = statisticsMapper.tagsAndArticleCount();
        //过滤掉value值为0的数据
        List<PieDataVO> res = dataVOS.stream().filter(pieDataVO -> pieDataVO.getValue() != 0L).collect(Collectors.toList());
        return Response.success(res);
    }

    @Override
    public Response findDashboardUpdateStatistics() {
        List<LineDataVO> queryList = statisticsMapper.countUpdateOneMonth();
        List<String> xData;
        List<Long> seriesData;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        xData = queryList.stream().map(LineDataVO::getXData).collect(Collectors.toList()).stream().map(localDate ->
                localDate.format(formatter)
        ).collect(Collectors.toList());
        seriesData = queryList.stream().map(LineDataVO::getSeriesData).collect(Collectors.toList());
        JSONObject res = new JSONObject();
        res.put("xData", xData);
        res.put("seriesData", seriesData);
        return Response.success(res);
    }


    public JSONObject convertBarData(List<JSONObject> res, String XKey, String YKey) {
        // 创建两个列表分别存储 title 和 readNum
        List<String> XItems = new ArrayList<>();
        List<Integer> YItems = new ArrayList<>();

        // 遍历 res 列表，提取 title 和 readNum
        for (JSONObject item : res) {
            XItems.add(item.getString(XKey));
            YItems.add(item.getInteger(YKey));
        }

        // 将两个列表封装到一个 JSONObject 中
        JSONObject result = new JSONObject();
        result.put("xData", XItems);
        result.put("seriesData", YItems);

        return result;
    }


    public static JSONObject formatPvData(List<LineDataVO> pvDatesJsonList) {
        List<String> formattedDates = new ArrayList<>();
        List<Long> cumulativeCounts = new ArrayList<>();

        Long cumulativeCount = 0L;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");

        for (LineDataVO pvDateJson : pvDatesJsonList) {
            LocalDate xData = pvDateJson.getXData();
            Long count = pvDateJson.getSeriesData();

            String formattedDate = dateFormat.format(xData);
            formattedDates.add(formattedDate);

            cumulativeCount += count;
            cumulativeCounts.add(cumulativeCount);
        }

        JSONObject res = new JSONObject();
        res.put("updateDates", formattedDates);
        res.put("updateCounts", cumulativeCounts);

        return res;

    }

    @Override
    public void exportDashboardExcel(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Sheet 1: PV趋势数据（按日期范围过滤）
            Sheet pvSheet = workbook.createSheet("PV趋势数据");
            Row pvHeader = pvSheet.createRow(0);
            createCell(pvHeader, 0, "日期", headerStyle);
            createCell(pvHeader, 1, "PV访问量", headerStyle);

            List<StatisticsArticlePVDO> pvRecords = articlePVMapper.selectList(
                    Wrappers.<StatisticsArticlePVDO>lambdaQuery()
                            .ge(StatisticsArticlePVDO::getPvDate, startDate)
                            .le(StatisticsArticlePVDO::getPvDate, endDate)
                            .orderByAsc(StatisticsArticlePVDO::getPvDate));
            if (!CollectionUtils.isEmpty(pvRecords)) {
                int rowIdx = 1;
                for (StatisticsArticlePVDO pv : pvRecords) {
                    Row row = pvSheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(pv.getPvDate().toString());
                    row.createCell(1).setCellValue(pv.getPvCount());
                }
            }

            // Sheet 2: 文章阅读量Top6
            Sheet readSheet = workbook.createSheet("文章阅读量Top6");
            Row readHeader = readSheet.createRow(0);
            createCell(readHeader, 0, "文章标题", headerStyle);
            createCell(readHeader, 1, "阅读量", headerStyle);

            List<JSONObject> topArticles = articleMapper.orderArticle();
            if (!CollectionUtils.isEmpty(topArticles)) {
                int rowIdx = 1;
                for (JSONObject item : topArticles) {
                    Row row = readSheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(item.getString("title"));
                    row.createCell(1).setCellValue(item.getIntValue("readNum"));
                }
            }

            // Sheet 3: 分类分布
            Sheet categorySheet = workbook.createSheet("分类分布");
            Row categoryHeader = categorySheet.createRow(0);
            createCell(categoryHeader, 0, "分类名称", headerStyle);
            createCell(categoryHeader, 1, "文章数", headerStyle);

            List<PieDataVO> categories = statisticsMapper.categoryAndArticleCount();
            if (!CollectionUtils.isEmpty(categories)) {
                int rowIdx = 1;
                for (PieDataVO item : categories) {
                    Row row = categorySheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(item.getName());
                    row.createCell(1).setCellValue(item.getValue());
                }
            }

            // Sheet 4: 标签分布
            Sheet tagSheet = workbook.createSheet("标签分布");
            Row tagHeader = tagSheet.createRow(0);
            createCell(tagHeader, 0, "标签名称", headerStyle);
            createCell(tagHeader, 1, "文章数", headerStyle);

            List<PieDataVO> tags = statisticsMapper.tagsAndArticleCount();
            if (!CollectionUtils.isEmpty(tags)) {
                int rowIdx = 1;
                for (PieDataVO item : tags) {
                    if (item.getValue() == 0L) continue;
                    Row row = tagSheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(item.getName());
                    row.createCell(1).setCellValue(item.getValue());
                }
            }

            // Sheet 5: 发布热力图（按日期范围过滤）
            Sheet publishSheet = workbook.createSheet("发布热力图");
            Row publishHeader = publishSheet.createRow(0);
            createCell(publishHeader, 0, "日期", headerStyle);
            createCell(publishHeader, 1, "发布数", headerStyle);

            List<ArticlePublishCountDO> publishCounts = articleMapper.selectDateArticlePublishCount(startDate, endDate);
            if (!CollectionUtils.isEmpty(publishCounts)) {
                int rowIdx = 1;
                for (ArticlePublishCountDO item : publishCounts) {
                    Row row = publishSheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(item.getDate().toString());
                    row.createCell(1).setCellValue(item.getCount());
                }
            }

            // 设置响应头
            String fileName = "dashboard_report_" + startDate + "_" + endDate + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("导出仪表盘Excel失败", e);
            throw new RuntimeException("导出Excel失败");
        }
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
