package com.quanxiaoha.weblog.common.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveWordFilterTest {

    private SensitiveWordFilter filter;

    @BeforeEach
    void setUp() {
        filter = new SensitiveWordFilter();
        filter.addWords(Arrays.asList("敏感词", "违禁", "广告推广", "ab"));
    }

    @Test
    void detect_shouldFindExactMatch() {
        Set<String> hits = filter.detect("这段文字包含敏感词在里面");
        assertTrue(hits.contains("敏感词"));
    }

    @Test
    void detect_shouldFindMultipleHits() {
        Set<String> hits = filter.detect("敏感词和违禁都在这里");
        assertTrue(hits.contains("敏感词"));
        assertTrue(hits.contains("违禁"));
    }

    @Test
    void detect_shouldReturnEmptyWhenNoHit() {
        Set<String> hits = filter.detect("这是一段正常的文字内容");
        assertTrue(hits.isEmpty());
    }

    @Test
    void detect_shouldHandleNullInput() {
        Set<String> hits = filter.detect(null);
        assertTrue(hits.isEmpty());
    }

    @Test
    void detect_shouldHandleEmptyString() {
        Set<String> hits = filter.detect("");
        assertTrue(hits.isEmpty());
    }

    @Test
    void detect_shouldBeCaseInsensitive() {
        Set<String> hits = filter.detect("contains AB inside");
        assertTrue(hits.contains("ab"));
    }

    @Test
    void detect_shouldMatchAtStartOfText() {
        Set<String> hits = filter.detect("敏感词在开头");
        assertTrue(hits.contains("敏感词"));
    }

    @Test
    void detect_shouldMatchAtEndOfText() {
        Set<String> hits = filter.detect("结尾是敏感词");
        assertTrue(hits.contains("敏感词"));
    }

    @Test
    void detect_shouldMatchEntireText() {
        Set<String> hits = filter.detect("敏感词");
        assertTrue(hits.contains("敏感词"));
    }

    @Test
    void detect_shouldMatchLongerWord() {
        Set<String> hits = filter.detect("这是广告推广信息");
        assertTrue(hits.contains("广告推广"));
    }

    @Test
    void detect_shouldHandleSingleCharacterInput() {
        Set<String> hits = filter.detect("a");
        assertTrue(hits.isEmpty());
    }

    @Test
    void detect_shouldHandleSpecialCharacters() {
        SensitiveWordFilter f = new SensitiveWordFilter();
        f.addWords(Collections.singletonList("test@word"));
        Set<String> hits = f.detect("contains test@word here");
        assertTrue(hits.contains("test@word"));
    }

    @Test
    void detect_shouldHandleRepeatedCharacters() {
        Set<String> hits = filter.detect("ababab");
        assertTrue(hits.contains("ab"));
    }

    @Test
    void contains_shouldReturnTrueOnHit() {
        assertTrue(filter.contains("包含敏感词"));
    }

    @Test
    void contains_shouldReturnFalseOnClean() {
        assertFalse(filter.contains("干净的内容"));
    }

    @Test
    void contains_shouldReturnFalseOnNull() {
        assertFalse(filter.contains(null));
    }

    @Test
    void contains_shouldReturnFalseOnEmpty() {
        assertFalse(filter.contains(""));
    }

    @Test
    void reset_shouldClearAllWords() {
        filter.reset();
        assertFalse(filter.contains("敏感词"));
    }

    @Test
    void addWords_shouldSkipNullAndEmptyEntries() {
        SensitiveWordFilter f = new SensitiveWordFilter();
        f.addWords(Arrays.asList(null, "", "valid"));
        assertTrue(f.contains("valid"));
        assertFalse(f.contains(""));
    }

    @Test
    void addWords_shouldHandleEmptyCollection() {
        SensitiveWordFilter f = new SensitiveWordFilter();
        f.addWords(Collections.emptyList());
        assertFalse(f.contains("anything"));
    }

    @Test
    void detect_shouldHandlePrefixOverlap() {
        // "广告推广" is a word; "广告" alone is not
        Set<String> hits = filter.detect("广告");
        assertFalse(hits.contains("广告"));
    }

    @Test
    void extractContext_shouldReturnSnippetAroundHit() {
        String text = "前面很多内容然后这里出现了敏感词接着后面还有很多内容";
        String ctx = filter.extractContext(text, "敏感词", 5);
        assertTrue(ctx.contains("敏感词"));
        assertTrue(ctx.length() < text.length());
    }

    @Test
    void extractContext_shouldReturnEmptyForNull() {
        assertEquals("", filter.extractContext(null, "敏感词", 5));
        assertEquals("", filter.extractContext("text", null, 5));
    }

    @Test
    void extractContext_shouldReturnEmptyWhenWordNotFound() {
        assertEquals("", filter.extractContext("clean text", "nothere", 5));
    }

    @Test
    void extractContext_shouldHandleWordAtStart() {
        String ctx = filter.extractContext("敏感词在最前面后面很多内容", "敏感词", 3);
        assertTrue(ctx.startsWith("敏感词"));
    }

    @Test
    void extractContext_shouldHandleWordAtEnd() {
        String ctx = filter.extractContext("前面很多内容最后是敏感词", "敏感词", 3);
        assertTrue(ctx.endsWith("敏感词"));
    }
}
