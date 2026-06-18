package com.quanxiaoha.weblog.common.constant;

public interface PermissionConstants {

    // Article
    String ARTICLE_PUBLISH = "article:publish";
    String ARTICLE_DELETE = "article:delete";
    String ARTICLE_UPDATE = "article:update";

    // Category
    String CATEGORY_CREATE = "category:create";
    String CATEGORY_DELETE = "category:delete";

    // Tag
    String TAG_CREATE = "tag:create";
    String TAG_DELETE = "tag:delete";

    // User
    String USER_CREATE = "user:create";
    String USER_LIST = "user:list";
    String USER_VIEW = "user:view";
    String USER_UPDATE = "user:update";
    String USER_DELETE = "user:delete";
    String USER_PASSWORD = "user:password";

    // Blog Settings
    String SETTINGS_UPDATE = "settings:update";

    // File
    String FILE_UPLOAD = "file:upload";

    // Comment
    String COMMENT_LIST = "comment:list";
    String COMMENT_DELETE = "comment:delete";
    String COMMENT_UPDATE = "comment:update";

    // Role
    String ROLE_MANAGE = "role:manage";

    // Resume Template
    String TEMPLATE_CREATE = "template:create";
    String TEMPLATE_UPDATE = "template:update";
    String TEMPLATE_DELETE = "template:delete";

    // Notification
    String NOTIFICATION_SEND = "notification:send";
}
