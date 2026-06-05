package com.quanxiaoha.weblog.admin.schedule;

import com.quanxiaoha.weblog.common.domain.dos.ApplicationReminderLogDO;
import com.quanxiaoha.weblog.common.domain.dos.ResumeApplicationDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ApplicationReminderLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeApplicationMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.mail.MailService;
import com.quanxiaoha.weblog.common.mail.MailService.ExpiredApplicationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ApplicationExpiryReminderTask {

    private static final int EXPIRY_DAYS = 30;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ResumeApplicationMapper resumeApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationReminderLogMapper reminderLogMapper;

    @Autowired
    private MailService mailService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() {
        log.info("==> 开始执行投递过期提醒任务...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusDays(EXPIRY_DAYS);

        // 1. 查询所有状态为"已投递"且投递时间和最后更新时间均超过30天的记录
        List<ResumeApplicationDO> expiredList = resumeApplicationMapper.selectExpiredApplications(cutoff);

        if (expiredList.isEmpty()) {
            log.info("==> 未发现过期投递记录，跳过发送");
            saveLog(now, 0, 0, 0, 0);
            return;
        }

        log.info("==> 扫描到过期投递记录 {} 条，开始按用户聚合...", expiredList.size());

        // 2. 按用户ID分组，确保每个用户只收到一封聚合邮件
        Map<Long, List<ResumeApplicationDO>> groupedByUser = expiredList.stream()
                .collect(Collectors.groupingBy(ResumeApplicationDO::getUserId));

        log.info("==> 涉及 {} 个用户，逐用户发送聚合提醒邮件...", groupedByUser.size());

        int sentCount = 0;
        int failedCount = 0;

        // 3. 遍历分组后的Map，每个用户发送一封包含所有过期投递的聚合邮件
        for (Map.Entry<Long, List<ResumeApplicationDO>> entry : groupedByUser.entrySet()) {
            Long userId = entry.getKey();
            List<ResumeApplicationDO> userApplications = entry.getValue();

            UserDO user = userMapper.selectById(userId);
            if (user == null || !StringUtils.hasText(user.getEmail())) {
                log.warn("==> 用户 {} 无邮箱，跳过提醒（该用户有 {} 条过期投递）", userId, userApplications.size());
                failedCount++;
                continue;
            }

            // 将该用户所有过期投递汇总到一封邮件中
            List<ExpiredApplicationInfo> infos = userApplications.stream()
                    .map(app -> new ExpiredApplicationInfo(
                            app.getCompany(),
                            app.getPosition(),
                            app.getApplyTime() != null ? app.getApplyTime().format(DATE_FMT) : "未知"
                    ))
                    .collect(Collectors.toList());

            try {
                mailService.sendApplicationExpiryReminder(user.getEmail(), infos);
                sentCount++;
                log.info("==> 用户 {}（{}）提醒邮件发送成功，包含 {} 条过期投递",
                        userId, user.getEmail(), infos.size());
            } catch (Exception e) {
                log.error("==> 发送过期提醒邮件失败，用户: {}, 邮箱: {}", userId, user.getEmail(), e);
                failedCount++;
            }
        }

        saveLog(now, expiredList.size(), groupedByUser.size(), sentCount, failedCount);
        log.info("==> 投递过期提醒任务完成，扫描过期投递: {}, 涉及用户: {}, 邮件发送成功: {}, 失败: {}",
                expiredList.size(), groupedByUser.size(), sentCount, failedCount);
    }

    private void saveLog(LocalDateTime executeTime, int scannedCount, int userCount, int sentCount, int failedCount) {
        ApplicationReminderLogDO logDO = ApplicationReminderLogDO.builder()
                .executeTime(executeTime)
                .scannedCount(scannedCount)
                .userCount(userCount)
                .sentCount(sentCount)
                .failedCount(failedCount)
                .createTime(LocalDateTime.now())
                .build();
        reminderLogMapper.insert(logDO);
    }
}
