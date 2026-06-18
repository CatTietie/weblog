package com.quanxiaoha.weblog.common.utils;

import java.util.*;

public class SensitiveWordFilter {

    private static final char REPLACEMENT = '*';

    private final Map<Character, Object> rootMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void addWords(Collection<String> words) {
        for (String word : words) {
            if (word == null || word.isEmpty()) continue;
            Map<Character, Object> current = rootMap;
            for (int i = 0; i < word.length(); i++) {
                char c = Character.toLowerCase(word.charAt(i));
                Object node = current.get(c);
                if (node == null) {
                    Map<Character, Object> next = new HashMap<>();
                    current.put(c, next);
                    current = next;
                } else if (node instanceof Map) {
                    current = (Map<Character, Object>) node;
                } else {
                    Map<Character, Object> next = new HashMap<>();
                    next.put('\0', Boolean.TRUE);
                    current.put(c, next);
                    current = next;
                }
            }
            current.put('\0', Boolean.TRUE);
        }
    }

    public void reset() {
        rootMap.clear();
    }

    @SuppressWarnings("unchecked")
    public Set<String> detect(String text) {
        if (text == null || text.isEmpty()) return Collections.emptySet();
        Set<String> found = new LinkedHashSet<>();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            Map<Character, Object> current = rootMap;
            for (int j = i; j < len; j++) {
                char c = Character.toLowerCase(text.charAt(j));
                Object node = current.get(c);
                if (node == null) break;
                if (node instanceof Map) {
                    current = (Map<Character, Object>) node;
                    if (current.containsKey('\0')) {
                        found.add(text.substring(i, j + 1).toLowerCase());
                    }
                } else {
                    found.add(text.substring(i, j + 1).toLowerCase());
                    break;
                }
            }
        }
        return found;
    }

    public boolean contains(String text) {
        return !detect(text).isEmpty();
    }

    public String extractContext(String text, String word, int radius) {
        if (text == null || word == null) return "";
        int idx = text.toLowerCase().indexOf(word.toLowerCase());
        if (idx < 0) return "";
        int start = Math.max(0, idx - radius);
        int end = Math.min(text.length(), idx + word.length() + radius);
        StringBuilder sb = new StringBuilder();
        if (start > 0) sb.append("...");
        sb.append(text, start, end);
        if (end < text.length()) sb.append("...");
        return sb.toString();
    }
}
