package com.quanxiaoha.weblog.admin.staticsite;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentWidgetRenderer {

    public void populateCommentVariables(java.util.Map<String, Object> variables, String commentProvider, String commentConfig) {
        variables.put("commentProvider", commentProvider == null ? "none" : commentProvider);

        if ("giscus".equals(commentProvider) && commentConfig != null) {
            JSONObject config = JSON.parseObject(commentConfig);
            variables.put("giscusRepo", config.getString("repo"));
            variables.put("giscusRepoId", config.getString("repoId"));
            variables.put("giscusCategory", config.getString("category"));
            variables.put("giscusCategoryId", config.getString("categoryId"));

            String themeMode = config.getString("theme");
            if (themeMode == null) themeMode = "auto";
            variables.put("giscusThemeMode", themeMode);
            if ("auto".equals(themeMode)) {
                variables.put("giscusTheme", "preferred_color_scheme");
            } else if ("dark".equals(themeMode)) {
                variables.put("giscusTheme", "dark");
            } else {
                variables.put("giscusTheme", "light");
            }
        } else if ("waline".equals(commentProvider) && commentConfig != null) {
            JSONObject config = JSON.parseObject(commentConfig);
            variables.put("walineServerUrl", config.getString("serverURL"));
        }
    }
}
