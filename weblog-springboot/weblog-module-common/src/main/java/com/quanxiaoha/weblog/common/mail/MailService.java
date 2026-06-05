package com.quanxiaoha.weblog.common.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${weblog.mail.admin-email}")
    private String adminEmail;

    public void sendApplicationStatusNotification(String toEmail, String company, String position, String statusLabel) {
        String subject = String.format("【求职进展】%s - %s 状态更新", company, position != null ? position : "未指定职位");
        String content = buildStatusNotificationHtml(company, position, statusLabel);

        try {
            sendHtmlMail(toEmail, subject, content);
            log.info("==> 投递状态变更邮件发送成功，收件人: {}, 公司: {}, 状态: {}", toEmail, company, statusLabel);
        } catch (Exception e) {
            log.error("==> 投递状态变更邮件发送失败，收件人: {}, 公司: {}", toEmail, company, e);
            notifyAdmin(toEmail, company, position, statusLabel, e.getMessage());
        }
    }

    public void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    private void notifyAdmin(String failedRecipient, String company, String position, String status, String errorMsg) {
        try {
            String subject = "【系统告警】投递状态通知邮件发送失败";
            String content = String.format(
                    "<p>以下邮件发送失败，请关注：</p>"
                    + "<ul>"
                    + "<li>收件人：%s</li>"
                    + "<li>公司：%s</li>"
                    + "<li>职位：%s</li>"
                    + "<li>状态：%s</li>"
                    + "<li>错误信息：%s</li>"
                    + "</ul>",
                    failedRecipient, company, position != null ? position : "未指定", status, errorMsg
            );
            sendHtmlMail(adminEmail, subject, content);
            log.info("==> 管理员告警邮件发送成功");
        } catch (Exception ex) {
            log.error("==> 管理员告警邮件也发送失败", ex);
        }
    }

    private String buildStatusNotificationHtml(String company, String position, String statusLabel) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset=\"UTF-8\"></head><body style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">"
                + "<div style=\"border: 1px solid #e0e0e0; border-radius: 8px; padding: 30px;\">"
                + "<h2 style=\"color: #333; margin-top: 0;\">求职进展通知</h2>"
                + "<p style=\"color: #555; font-size: 15px;\">您好，您的求职投递状态已更新：</p>"
                + "<table style=\"width: 100%; border-collapse: collapse; margin: 20px 0;\">"
                + "<tr><td style=\"padding: 10px; border-bottom: 1px solid #eee; color: #888; width: 80px;\">公司</td>"
                + "<td style=\"padding: 10px; border-bottom: 1px solid #eee; font-weight: 600;\">" + company + "</td></tr>"
                + "<tr><td style=\"padding: 10px; border-bottom: 1px solid #eee; color: #888;\">职位</td>"
                + "<td style=\"padding: 10px; border-bottom: 1px solid #eee;\">" + (position != null ? position : "未指定") + "</td></tr>"
                + "<tr><td style=\"padding: 10px; border-bottom: 1px solid #eee; color: #888;\">最新状态</td>"
                + "<td style=\"padding: 10px; border-bottom: 1px solid #eee;\"><span style=\"display: inline-block; padding: 4px 12px; border-radius: 4px; background-color: #e8f5e9; color: #2e7d32; font-weight: 600;\">" + statusLabel + "</span></td></tr>"
                + "</table>"
                + "<p style=\"color: #999; font-size: 13px; margin-bottom: 0;\">此邮件由系统自动发送，请勿回复。</p>"
                + "</div></body></html>";
    }

    public static class ExpiredApplicationInfo {
        private final String company;
        private final String position;
        private final String applyDate;

        public ExpiredApplicationInfo(String company, String position, String applyDate) {
            this.company = company;
            this.position = position;
            this.applyDate = applyDate;
        }

        public String getCompany() { return company; }
        public String getPosition() { return position; }
        public String getApplyDate() { return applyDate; }
    }

    public void sendApplicationExpiryReminder(String toEmail, List<ExpiredApplicationInfo> applications) throws MessagingException {
        String subject = String.format("【投递进展提醒】您有 %d 条投递超过30天未更新", applications.size());
        String content = buildExpiryReminderHtml(applications);
        sendHtmlMail(toEmail, subject, content);
        log.info("==> 投递过期提醒邮件发送成功，收件人: {}, 投递数: {}", toEmail, applications.size());
    }

    private String buildExpiryReminderHtml(List<ExpiredApplicationInfo> applications) {
        StringBuilder rows = new StringBuilder();
        for (ExpiredApplicationInfo app : applications) {
            rows.append("<tr>")
                    .append("<td style=\"padding: 10px; border-bottom: 1px solid #eee;\">").append(app.getCompany()).append("</td>")
                    .append("<td style=\"padding: 10px; border-bottom: 1px solid #eee;\">").append(app.getPosition() != null ? app.getPosition() : "未指定").append("</td>")
                    .append("<td style=\"padding: 10px; border-bottom: 1px solid #eee;\">").append(app.getApplyDate()).append("</td>")
                    .append("</tr>");
        }

        return "<!DOCTYPE html>"
                + "<html><head><meta charset=\"UTF-8\"></head><body style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">"
                + "<div style=\"border: 1px solid #e0e0e0; border-radius: 8px; padding: 30px;\">"
                + "<h2 style=\"color: #333; margin-top: 0;\">投递进展提醒</h2>"
                + "<p style=\"color: #555; font-size: 15px;\">您好，以下投递记录已超过 <strong>30天</strong> 未更新状态，建议您登录系统查看或主动跟进：</p>"
                + "<table style=\"width: 100%; border-collapse: collapse; margin: 20px 0;\">"
                + "<tr style=\"background-color: #f5f5f5;\">"
                + "<th style=\"padding: 10px; text-align: left; border-bottom: 2px solid #ddd;\">公司</th>"
                + "<th style=\"padding: 10px; text-align: left; border-bottom: 2px solid #ddd;\">职位</th>"
                + "<th style=\"padding: 10px; text-align: left; border-bottom: 2px solid #ddd;\">投递日期</th>"
                + "</tr>"
                + rows
                + "</table>"
                + "<p style=\"color: #666; font-size: 14px;\">如果这些投递已有结果，请登录系统更新状态，以便更好地管理您的求职进度。</p>"
                + "<p style=\"color: #999; font-size: 13px; margin-bottom: 0;\">此邮件由系统自动发送，请勿回复。</p>"
                + "</div></body></html>";
    }
}
