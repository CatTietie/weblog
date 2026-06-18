package com.quanxiaoha.weblog.common.enums;

import com.quanxiaoha.weblog.common.exception.BaseExceptionInterface;
import com.quanxiaoha.weblog.common.utils.I18nUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    SYSTEM_ERROR("10000", "error.10000"),
    PARAM_NOT_VALID("10001", "error.10001"),

    LOGIN_FAIL("20000", "error.20000"),
    USERNAME_OR_PWD_ERROR("20001", "error.20001"),
    UNAUTHORIZED("20002", "error.20002"),
    USERNAME_NOT_FOUND("20003", "error.20003"),
    FORBIDDEN("20004", "error.20004"),
    CATEGORY_NAME_IS_EXISTED("20005", "error.20005"),
    TAG_CANT_DUPLICATE("20006", "error.20006"),
    TAG_NOT_EXISTED("20007", "error.20007"),
    FILE_UPLOAD_FAILED("20008", "error.20008"),
    CATEGORY_NOT_EXISTED("20009", "error.20009"),
    ARTICLE_NOT_FOUND("20010", "error.20010"),
    CATEGORY_CAN_NOT_DELETE("20011", "error.20011"),
    TAG_CAN_NOT_DELETE("20012", "error.20012"),
    USERNAME_IS_EXISTED("20013", "error.20013"),
    USER_IS_DISABLED("20014", "error.20014"),
    RESUME_NOT_FOUND("20015", "error.20015"),
    RESUME_NOT_BELONG_TO_USER("20016", "error.20016"),
    RESUME_UPLOAD_FILE_INVALID("20017", "error.20017"),
    RESUME_SHARE_DISABLED("20018", "error.20018"),
    RESUME_TEMPLATE_NAME_IS_EXISTED("20019", "error.20019"),
    RESUME_TEMPLATE_NOT_FOUND("20020", "error.20020"),
    APPLICATION_RECORD_NOT_FOUND("20021", "error.20021"),
    APPLICATION_RECORD_NOT_BELONG_TO_USER("20022", "error.20022"),
    COMMENT_NOT_FOUND("20023", "error.20023"),
    COMMENT_LOGIN_REQUIRED("20024", "error.20024"),
    ARTICLE_VERSION_NOT_FOUND("20025", "error.20025"),
    TENANT_ACCESS_DENIED("20026", "error.20026"),
    TENANT_DISABLED("20027", "error.20027"),
    SENSITIVE_WORD_EXISTED("20028", "error.20028"),
    CONTENT_HIT_SENSITIVE_WORD("20029", "error.20029"),
    SENSITIVE_SCAN_TASK_NOT_FOUND("20030", "error.20030"),
    SENSITIVE_SCAN_IN_PROGRESS("20031", "error.20031"),
    ;

    @Getter
    private String errorCode;
    private String messageKey;

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return I18nUtil.getMessage(messageKey);
    }
}
