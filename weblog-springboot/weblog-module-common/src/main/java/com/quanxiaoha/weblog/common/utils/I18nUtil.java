package com.quanxiaoha.weblog.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class I18nUtil {

    private final MessageSource messageSource;
    private static I18nUtil instance;

    public I18nUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static String getMessage(String code) {
        if (instance == null || instance.messageSource == null) {
            return code;
        }
        return instance.messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code, Object[] args) {
        if (instance == null || instance.messageSource == null) {
            return code;
        }
        return instance.messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
    }
}
