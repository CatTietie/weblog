package com.quanxiaoha.weblog.common.utils;

import com.quanxiaoha.weblog.common.domain.mapper.SensitiveWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class SensitiveWordHelper {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    private final SensitiveWordFilter filter = new SensitiveWordFilter();

    @PostConstruct
    public void init() {
        refresh(0L);
    }

    public void refresh(Long tenantId) {
        List<String> words = sensitiveWordMapper.selectAllWords(tenantId);
        filter.reset();
        filter.addWords(words);
        log.info("敏感词库已刷新, 加载词条数: {}", words.size());
    }

    public Set<String> detect(String text) {
        return filter.detect(text);
    }

    public boolean contains(String text) {
        return filter.contains(text);
    }

    public String extractContext(String text, String word, int radius) {
        return filter.extractContext(text, word, radius);
    }

    public SensitiveWordFilter getFilter() {
        return filter;
    }
}
