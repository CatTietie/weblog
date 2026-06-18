# Weblog 项目架构文档

## 项目概览

Weblog 是一个前后端分离的全栈博客系统，采用 **Spring Boot** 后端 + **Vue 3** 前端架构。除博客核心功能外，还集成了简历管理、评论互动、实时通知、用户访问分析等功能模块。

---

## 技术栈总览

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 (Composition API) + Vite 4 |
| UI 组件库 | Element Plus |
| 状态管理 | Pinia + pinia-plugin-persistedstate |
| 样式方案 | TailwindCSS |
| 图表 | ECharts |
| Markdown 编辑 | md-editor-v3 |
| 动画 | GSAP + animate.css |
| 拖拽 | vuedraggable |
| PDF 导出 | html2canvas + jsPDF |
| 实时通信 | STOMP.js + SockJS |
| 桌面通知 | Browser Notification API (@vueuse) |
| 图片预览 | v-viewer (viewerjs) |
| 后端框架 | Spring Boot 2.6.3 |
| ORM | MyBatis-Plus 3.5.2 |
| 数据库 | MySQL (weblog-cat) |
| 认证 | Spring Security + JWT (JJWT 0.11.2) |
| WebSocket | Spring WebSocket (STOMP) |
| 权限缓存 | Caffeine |
| 对象存储 | 阿里云 OSS |
| UA 解析 | uadetector-resources |
| API 文档 | Knife4j 4.3.0 |
| 对象映射 | MapStruct 1.5.5.Final |
| Markdown 渲染 | commonmark 0.20.0 (+ 扩展: tables, heading-anchor, image-attributes, task-list-items) |
| 构建工具 | Maven (后端) / npm + Vite (前端) |
| Java 版本 | 1.8 |

---

## 功能模块总览

| 模块 | 说明 | 前台/后台 |
|------|------|-----------|
| 博客文章 | 文章发布/编辑/删除/搜索、分类/标签管理、文章版本历史 | 前台 + 后台 |
| 归档 | 按时间归档文章列表 | 前台 |
| 评论系统 | 嵌套评论、点赞、评论审核 | 前台 + 后台 |
| 实时通知 | WebSocket 推送、评论回复通知、全局广播通知 | 前台 + 后台 |
| 简历管理 | 简历 CRUD、多模板、多语言、分享链接、PDF 导出、简历诊断 | 前台 + 后台 |
| 求职追踪 | 投递记录管理、投递统计分析 | 后台 |
| 简历模板 | 模板管理(启用/禁用/排序) | 后台 |
| 用户管理 | 注册/登录/密码修改、用户 CRUD | 前台 + 后台 |
| 角色权限 | RBAC 角色管理、细粒度权限分配 | 后台 |
| 博客设置 | Logo/名称/简介/社交链接配置 | 后台 |
| 数据仪表盘 | 文章统计(PV/阅读量/发布热力图/更新频率)、分类/标签分布 | 后台 |
| 用户访问分析 | 设备/浏览器/操作系统/时段/页面统计 | 后台 |
| 文件管理 | 阿里云 OSS 文件上传 | 后台 |

---

## 目录结构

```
weblog/
├── db/                            # 数据库 SQL 脚本 (23 个文件)
│   ├── init.sql                   # 完整建表脚本
│   ├── rbac_init.sql              # RBAC 权限模型初始化
│   ├── resume_init.sql            # 简历表
│   ├── resume_template_init.sql   # 简历模板表 + 种子数据
│   ├── resume_application.sql     # 求职投递记录表
│   ├── comment_init.sql           # 评论表
│   ├── notification_init.sql      # 通知表
│   ├── article_status_and_version.sql  # 文章状态 + 版本表
│   └── ...                        # 其他建表/迁移脚本
├── start-all.bat                  # 一键启动脚本
├── stop-all.bat                   # 一键停止脚本
├── weblog-springboot/             # 后端 (Spring Boot 多模块)
│   ├── pom.xml                    # 父 POM
│   ├── weblog-web/                # 主启动模块 (公开 API + WebSocket)
│   ├── weblog-module-admin/       # 后台管理模块
│   ├── weblog-module-common/      # 公共模块 (实体/工具/配置)
│   └── weblog-module-jwt/         # JWT 认证模块
└── weblog-vue3/
    └── weblog-vue3/               # Vue 3 前端项目
        ├── src/
        │   ├── api/               # API 请求层 (admin/ + frontend/)
        │   ├── components/        # 公共组件 (图表/简历/表单)
        │   ├── composables/       # 组合式函数
        │   ├── layouts/           # 布局组件 (admin/ + frontend/)
        │   ├── pages/             # 页面 (admin/ + frontend/)
        │   ├── router/            # 路由配置
        │   ├── stores/            # Pinia 状态管理
        │   └── utils/             # 工具函数 (简历解析/诊断/PDF导出)
        ├── package.json
        └── vite.config.js
```

---

## 后端架构

### 模块依赖关系

```
weblog-web (启动入口, 公开 API + WebSocket)
├── weblog-module-admin (后台管理)
│   ├── weblog-module-common (公共模块)
│   └── weblog-module-jwt (认证模块)
│       └── weblog-module-common
└── weblog-module-common
```

### 模块说明

#### weblog-web（主启动模块）

公开博客 API 入口 + WebSocket 通知服务。

| 包 | 职责 |
|---|------|
| `controller` | 公开 API (文章/分类/标签/归档/设置/评论/通知/简历分享/简历模板) |
| `service/impl` | 前台业务逻辑 |
| `model/vo` | 前台视图对象 |
| `convert` | MapStruct 对象转换器 |
| `markdown` | Markdown 渲染 (commonmark + 自定义图片渲染 + nofollow 链接) |
| `config` | WebSocket (STOMP) 配置、Knife4j 配置 |
| `event/subscriber` | 通知推送事件监听 (WebSocket) |

#### weblog-module-admin（后台管理模块）

后台管理面板全部功能。

| 包 | 职责 |
|---|------|
| `controller` | 管理端控制器 (14 个: 文章/分类/标签/设置/仪表盘/文件/评论/通知/简历/投递/模板/用户/角色/注册) |
| `service/impl` | 管理端业务逻辑 (14 个 Service) |
| `model/vo` | 管理端请求/响应对象 |
| `config` | Spring Security、阿里云 OSS、线程池、Knife4j |
| `event/subscriber` | Spring 事件监听 (文章阅读、评论发布、全局通知) |
| `schedule` | 定时任务 (每日 PV 记录初始化) |

#### weblog-module-common（公共模块）

所有模块共享的基础代码。

| 包 | 职责 |
|---|------|
| `domain/dos` | 数据库实体 DO (21 个) |
| `domain/mapper` | MyBatis-Plus Mapper 接口 (22 个) |
| `config` | MyBatis-Plus 配置 (分页 + 自定义批量插入)、Jackson 配置 |
| `exception` | 全局异常处理 + BizException |
| `enums` | 响应码枚举 |
| `utils` | 统一响应 Response<T>、分页响应 PageResponse、JSON 工具 |
| `aspect` | `@ApiOperationLog` AOP 日志切面 |
| `constant` | 常量定义 (简历相关) |

#### weblog-module-jwt（JWT 认证模块）

无状态 JWT 认证 + 权限缓存。

| 类 | 职责 |
|---|------|
| `JwtAuthenticationFilter` | 拦截登录请求，验证用户名密码 |
| `TokenAuthenticationFilter` | 每次请求校验 JWT Token |
| `RestAuthenticationSuccessHandler` | 登录成功后生成并返回 Token |
| `RestAuthenticationFailureHandler` | 认证失败响应 |
| `RestAuthenticationEntryPoint` | 未认证访问拦截 |
| `RestAccessDeniedHandler` | 权限不足拦截 |
| `JwtTokenHelper` | Token 生成、验证、解析工具 |
| `UserDetailServiceImpl` | 从数据库加载用户信息和角色 |
| `PermissionCacheService` | Caffeine 缓存权限数据 |

---

## 前端架构

### 路由结构

```
/ (Hash 路由)
├── /                             # 博客首页 (文章列表)
├── /archive/list                 # 归档列表
├── /category/list                # 分类列表
├── /category/article/list        # 分类下文章
├── /tag/list                     # 标签列表
├── /tag/article/list             # 标签下文章
├── /article/:articleId           # 文章详情 (含评论区)
├── /login                        # 登录页
├── /resume/list                  # 我的简历列表 (需认证)
├── /resume/edit                  # 简历编辑器 (需认证)
├── /resume/s/:shareCode          # 简历公开分享页
├── /:pathMatch(.*)*              # 404 页面
└── /admin/index                  # 后台 (需认证)
    ├── /article-stats            # 文章统计仪表盘
    ├── /user-stats               # 用户访问统计
    ├── /article/list             # 文章管理
    ├── /category/list            # 分类管理
    ├── /tag/list                 # 标签管理
    ├── /blog/settings            # 博客设置
    ├── /user/list                # 用户管理
    ├── /role/list                # 角色权限管理
    ├── /resume-template/list     # 简历模板管理
    ├── /comment/list             # 评论审核管理
    └── /notification/send        # 全局通知发送
```

### API 层组织

```
src/api/
├── admin/                        # 后台管理 API (13 个文件)
│   ├── article.js                # 文章 CRUD + 版本历史
│   ├── blogsettings.js           # 博客设置
│   ├── category.js               # 分类管理
│   ├── tag.js                    # 标签管理
│   ├── dashboard.js              # 仪表盘统计 (PV/阅读/发布/更新/分类/标签)
│   ├── file.js                   # 文件上传
│   ├── user.js                   # 登录/注册/用户信息
│   ├── userManage.js             # 用户/角色/权限管理
│   ├── userStats.js              # 用户访问统计
│   ├── resume.js                 # 简历 CRUD + 分享
│   ├── resumeTemplate.js         # 简历模板管理
│   ├── resumeApplication.js      # 求职投递管理
│   ├── comment.js                # 评论审核
│   └── notification.js           # 全局通知
└── frontend/                     # 前台公开 API (7 个文件)
    ├── article.js                # 文章列表/详情/搜索
    ├── archive.js                # 归档
    ├── blogsettings.js           # 博客设置
    ├── category.js               # 分类
    ├── tag.js                    # 标签
    ├── comment.js                # 评论 (列表/发布/点赞)
    ├── notification.js           # 通知 (未读数/列表/已读)
    ├── resumeTemplate.js         # 简历模板 (已启用列表)
    └── resume-share.js           # 简历公开分享
```

### 关键组件

#### 图表组件 (src/components/)

| 组件 | 用途 |
|------|------|
| `ArticlePVLineChat` | 文章 PV 趋势折线图 |
| `ArticleReadNumChat` | 文章阅读量统计 |
| `ArticleUpdateChat` | 文章更新频率统计 |
| `ArtilcePublishCalendar` | 文章发布热力日历 |
| `CategoryCountPieChat` | 分类分布饼图 |
| `TagsCountChat` | 标签分布图 |
| `OsCountChat` | 操作系统分布图 |
| `DeviceCountChat` | 设备类型统计 |
| `BrowserCountChat` | 浏览器分布图 |
| `PeriodCountChat` | 时段访问统计 |

#### 简历组件 (src/components/resume/)

| 组件 | 用途 |
|------|------|
| `ResumePreview` | 简历完整预览渲染 |
| `TemplateSelector` | 模板选择器 |
| `LanguageSwitcher` | 多语言切换 (zh/en/ja/ko/fr/de/es) |
| `CoverSettings` | 封面与求职信设置 |
| `BlockEditor` | 模块化内容编辑器 |
| `ApplicationDrawer` | 求职投递抽屉面板 |
| `ResumeDiagnosis` | 简历诊断评分 |
| `MarkdownSnippets` | Markdown 片段工具 |
| `DraggableEntries` | 拖拽排序条目 |
| `ResumeApplicationLineChart` | 投递统计折线图 |
| `ResumeApplicationPieChart` | 投递统计饼图 |

#### 简历模板 (src/components/resume/templates/)

| 模板 | 风格 |
|------|------|
| `ClassicTemplate` | 经典左右分栏 |
| `ModernTemplate` | 现代全宽 |
| `SimpleTemplate` | 简洁单栏 |

#### 前台布局组件 (src/layouts/frontend/)

| 组件 | 用途 |
|------|------|
| `Header` | 顶部导航栏 |
| `Footer` | 页脚 |
| `UserInfoCard` | 博主信息侧边卡片 |
| `CategoryListCard` | 分类侧边卡片 |
| `TagListCard` | 标签云侧边卡片 |
| `Toc` | 文章目录导航 |
| `NotificationBell` | 实时通知铃铛 |
| `ScrollToTopButton` | 回到顶部按钮 |
| `ToolboxButton` | 浮动工具按钮 |

### 状态管理 (Pinia)

| Store | 职责 | 持久化 |
|-------|------|--------|
| `user.js` | 用户信息、权限校验、周期性权限刷新 (3 分钟)、登出 | 是 |
| `menu.js` | 管理端侧边栏宽度 (250px/64px 切换) | 是 |
| `blogsettings.js` | 博客设置缓存 | 否 |

### 组合式函数 (Composables)

| 文件 | 导出 | 用途 |
|------|------|------|
| `cookie.js` | `getToken`, `setToken`, `removeToken`, `getTabList`, `setTabList` | Cookie Token 管理 |
| `useTagList.js` | `useTabList` | 后台标签页导航管理 |
| `util.js` | `showMessage`, `showModel`, `showPageLoading`, `hidePageLoading` | UI 通用工具 |
| `useDesktopNotification.js` | `useDesktopNotification` | 浏览器桌面通知 |
| `useWebSocket.js` | `useWebSocket` | STOMP WebSocket 连接管理 (含自动重连) |

### 前后端通信

- **协议:** REST over HTTP (JSON) + WebSocket (STOMP over SockJS)
- **代理:** Vite 开发服务器将 `/api` 代理到 `http://localhost:8080`（去除 `/api` 前缀），`/api/ws` 代理到 WebSocket
- **请求方法:** 几乎所有接口使用 POST（包括查询）；用户/角色管理使用 RESTful (GET/PUT/DELETE)
- **认证:** Axios 拦截器自动添加 `Authorization: Bearer <token>` 请求头
- **Token 存储:** Cookie（key: `Authorization`）
- **错误处理:** Axios 响应拦截器统一处理 401 (跳转登录)、403 (权限不足提示)

---

## 数据库设计

### 核心表结构 (21 张表)

| 表名 | 说明 | 关键特性 |
|------|------|----------|
| `t_article` | 文章主表 | 软删除, 状态(草稿/已发布) |
| `t_article_content` | 文章内容 | Markdown 正文, TEXT 类型 |
| `t_article_category_rel` | 文章-分类关联 | 唯一约束 (一文一类) |
| `t_article_tag_rel` | 文章-标签关联 | 多对多 |
| `t_article_update_history` | 文章更新历史 | 复合主键 (articleId + updateTime) |
| `t_article_version` | 文章版本快照 | 标题 + 内容全量快照 |
| `t_category` | 分类 | 唯一名称, 软删除 |
| `t_tag` | 标签 | 唯一名称, 软删除 |
| `t_user` | 用户 | 软删除, BCrypt 密码, 角色关联 |
| `t_user_role` | 用户角色 (旧表) | 字符串角色映射 |
| `t_role` | 角色 (RBAC) | 唯一 code |
| `t_permission` | 权限 | 层级结构 (parentId), 类型: menu/button/api |
| `t_role_permission` | 角色-权限关联 | 多对多 |
| `t_blog_settings` | 博客设置 | Logo/名称/简介/社交链接 |
| `t_statistics_article_pv` | 文章 PV 日统计 | 唯一日期 |
| `t_comment` | 评论 | 嵌套 (parentId), 点赞数, 状态审核, 软删除 |
| `t_notification` | 通知 | 接收者/发送者, 已读状态, 关联文章/评论 |
| `t_resume` | 简历 | 多语言 (JSON), 分享码, 封面数据 |
| `t_resume_template` | 简历模板 | 组件名绑定, 启用/禁用 |
| `t_resume_application` | 求职投递 | 状态流转 (投递/面试/offer/拒绝/撤回) |
| `user_visit_stats` | 用户访问统计 | IP/设备/浏览器/操作系统/页面 |

### 实体关系

```
User (1) ──── (1) Role ──── (N) RolePermission ──── (N) Permission

Article (1) ──── (1) ArticleContent
Article (1) ──── (1) ArticleCategoryRel ──── (1) Category
Article (1) ──── (N) ArticleTagRel ──── (N) Tag
Article (1) ──── (N) ArticleUpdateHistory
Article (1) ──── (N) ArticleVersion
Article (1) ──── (N) Comment

Comment ──── (self) Comment [parent_id 嵌套]
Comment (N) ──── (1) User
Comment ──── (N) Notification

Notification (N) ──── (1) User [receiver + sender]

Resume (N) ──── (1) User
Resume (1) ──── (N) ResumeApplication
Resume (N) ──── (1) ResumeTemplate
```

---

## 认证与授权

### 认证流程

```
[前端] POST /login (username, password)
    ↓
[JwtAuthenticationFilter] 拦截登录请求
    ↓
[DaoAuthenticationProvider] → [UserDetailServiceImpl] 验证凭据
    ↓ (成功)
[RestAuthenticationSuccessHandler] 生成 JWT Token
    ↓
[前端] 存储 Token 到 Cookie
    ↓
[后续请求] Axios 自动附加 Authorization: Bearer <token>
    ↓
[TokenAuthenticationFilter] 验证 Token → 设置 SecurityContext
```

### RBAC 权限模型

| 角色 | 权限范围 |
|------|----------|
| `ROLE_ADMIN` | 全部权限 (10 项) |
| `ROLE_USER` | 有限权限 (5 项，无用户/角色管理) |
| `ROLE_VISITOR` | 只读 (仅仪表盘查看) |

**权限粒度示例:** `article:publish`, `article:delete`, `comment:delete`, `role:manage`, `template:create` 等

**前端权限控制:** Pinia user store 提供 `hasPermission(code)` 方法，动态控制 UI 元素显隐；每 3 分钟刷新权限缓存。

### 安全配置要点

- CSRF 关闭（无状态 API）
- Session 策略：STATELESS
- `/admin/**` 路径需认证
- 其余路径公开访问
- 写操作通过 `@PreAuthorize` + 权限 code 保护
- WebSocket 连接通过 JWT Handshake 拦截器认证

---

## WebSocket 实时通知

### 架构

```
[Spring WebSocket Config]
  ├── STOMP Endpoint: /ws (SockJS fallback)
  ├── Message Broker: /topic (广播), /queue (个人)
  ├── Application Prefix: /app
  └── User Prefix: /user

[认证]
  ├── JwtHandshakeInterceptor (握手阶段验证 Token)
  └── WebSocketAuthChannelInterceptor (通道级认证)

[推送通道]
  ├── /user/{userId}/queue/notifications (个人通知)
  └── /topic/global (全局广播)
```

### 事件驱动流程

```
[评论发布] → PublishCommentEvent
    → PublishCommentSubscriber (异步)
        → 创建 Notification 记录
        → 触发 NotificationCreatedEvent
            → NotificationPushSubscriber
                → WebSocketNotificationService.push()

[全局通知] → SendGlobalNotificationEvent
    → SendGlobalNotificationSubscriber (异步, 批量 500)
        → 批量创建 Notification 记录
        → 触发 NotificationCreatedEvent
            → WebSocket 广播到 /topic/global
```

---

## API 接口汇总

### 公开接口 (无需认证)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/login` | POST | 用户登录 |
| `/register` | POST | 用户注册 |
| `/article/list` | POST | 文章分页列表 |
| `/article/detail` | POST | 文章详情 |
| `/article/search` | POST | 文章搜索 |
| `/archive/list` | POST | 归档分页 |
| `/blog/settings/detail` | POST | 博客设置 |
| `/category/list` | POST | 分类列表 |
| `/category/article/list` | POST | 分类下文章 |
| `/tag/list` | POST | 标签列表 |
| `/tag/article/list` | POST | 标签下文章 |
| `/comment/list` | POST | 评论列表 |
| `/comment/publish` | POST | 发布评论 |
| `/comment/like` | POST | 评论点赞 |
| `/notification/unreadCount` | POST | 未读通知数 |
| `/notification/list` | POST | 通知列表 |
| `/notification/read` | POST | 标记已读 |
| `/notification/readAll` | POST | 全部已读 |
| `/resume/share/{shareCode}` | GET | 公开简历查看 |
| `/resume/template/enabled/list` | POST | 已启用模板列表 |

### 管理接口 (需 JWT 认证)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/admin/article/publish` | POST | 发布文章 |
| `/admin/article/delete` | POST | 删除文章 |
| `/admin/article/list` | POST | 文章列表 |
| `/admin/article/detail` | POST | 文章详情 |
| `/admin/article/update` | POST | 更新文章 |
| `/admin/article/status` | POST | 切换文章状态 (草稿/发布) |
| `/admin/article/version/list` | POST | 文章版本列表 |
| `/admin/article/version/detail` | POST | 版本详情 |
| `/admin/category/add` | POST | 添加分类 |
| `/admin/category/delete` | POST | 删除分类 |
| `/admin/category/list` | POST | 分类分页 |
| `/admin/category/select/list` | POST | 分类下拉列表 |
| `/admin/tag/add` | POST | 添加标签 |
| `/admin/tag/delete` | POST | 删除标签 |
| `/admin/tag/list` | POST | 标签分页 |
| `/admin/tag/search` | POST | 标签搜索 |
| `/admin/tag/select/list` | POST | 标签下拉列表 |
| `/admin/blog/settings/update` | POST | 更新博客设置 |
| `/admin/blog/settings/detail` | POST | 获取博客设置 |
| `/admin/file/upload` | POST | 文件上传 (OSS) |
| `/admin/password/update` | POST | 修改密码 |
| `/admin/user/info` | POST | 当前用户信息 |
| `/admin/users` | POST | 创建用户 |
| `/admin/users/list` | POST | 用户列表 |
| `/admin/users/reset-password` | POST | 重置密码 |
| `/admin/users/{id}` | GET/PUT/DELETE | 用户详情/更新/删除 |
| `/admin/roles` | GET/POST | 角色列表/创建 |
| `/admin/roles/{id}` | GET/PUT/DELETE | 角色详情/更新/删除 |
| `/admin/roles/assign-permissions` | POST | 分配权限 |
| `/admin/permissions` | GET | 权限树 |
| `/admin/role/select/list` | POST | 角色下拉列表 |
| `/admin/comment/list` | POST | 评论列表 |
| `/admin/comment/delete` | POST | 删除评论 |
| `/admin/comment/status/batch-update` | POST | 批量审核评论 |
| `/admin/notification/sendGlobal` | POST | 发送全局通知 |
| `/admin/resume/list` | POST | 简历列表 |
| `/admin/resume/detail` | POST | 简历详情 |
| `/admin/resume/create` | POST | 创建简历 |
| `/admin/resume/update` | POST | 更新简历 |
| `/admin/resume/delete` | POST | 删除简历 |
| `/admin/resume/upload` | POST | 上传简历 (Markdown) |
| `/admin/resume/share/toggle` | POST | 切换分享状态 |
| `/admin/resume/share/info` | POST | 分享信息 |
| `/admin/resume/application/add` | POST | 添加投递记录 |
| `/admin/resume/application/list` | POST | 投递列表 |
| `/admin/resume/application/update` | POST | 更新投递 |
| `/admin/resume/application/delete` | POST | 删除投递 |
| `/admin/resume/application/statistics` | POST | 投递统计 |
| `/admin/resume/template/add` | POST | 添加模板 |
| `/admin/resume/template/list` | POST | 模板列表 |
| `/admin/resume/template/update` | POST | 更新模板 |
| `/admin/resume/template/delete` | POST | 删除模板 |
| `/admin/resume/template/status/update` | POST | 模板启用/禁用 |
| `/admin/dashboard/statistics` | POST | 基础统计 |
| `/admin/dashboard/ArticlesReadNumber` | POST | 文章阅读量 |
| `/admin/dashboard/ArticlesReadNumber/top6` | POST | 阅读量 Top6 |
| `/admin/dashboard/publishArticle/statistics` | POST | 发布统计 |
| `/admin/dashboard/pv/statistics` | POST | PV 统计 |
| `/admin/dashboard/updateTimes/statistics` | POST | 更新频率 |
| `/admin/dashboard/updateArticle/statistics` | POST | 更新统计 |
| `/admin/dashboard/category` | POST | 分类统计 |
| `/admin/dashboard/tag` | POST | 标签统计 |
| `/admin/userStats/os` | GET | 操作系统统计 |
| `/admin/userStats/device` | GET | 设备统计 |
| `/admin/userStats/browser` | GET | 浏览器统计 |
| `/admin/userStats/period` | GET | 时段统计 |
| `/admin/userStats/pageUrl` | GET | 页面访问统计 |

---

## 统一响应格式

```json
{
  "success": true,
  "message": "操作成功",
  "errorCode": null,
  "data": { ... }
}
```

---

## 开发环境配置

### 后端

- **端口:** 8080
- **数据库:** localhost:3306/weblog-cat
- **JWT 过期时间:** 1440 分钟 (24 小时)
- **文件上传限制:** 5MB (单文件)
- **API 文档:** http://localhost:8080/doc.html
- **WebSocket:** ws://localhost:8080/ws

### 前端

- **端口:** Vite 默认 (5173)
- **代理规则:** `/api/ws` → WebSocket 代理; `/api` → `http://localhost:8080` (去除 `/api` 前缀)
- **路由模式:** Hash (`/#/path`)
- **自动导入:** Element Plus 组件 + API 自动导入 (unplugin)

### 启动方式

```bash
# 后端
cd weblog-springboot
mvn spring-boot:run -pl weblog-web

# 前端
cd weblog-vue3/weblog-vue3
npm install
npm run dev
```

或使用项目根目录 `start-all.bat` 一键启动。

---

## 关键设计模式

| 模式 | 应用场景 |
|------|----------|
| 多模块分层 | Maven 模块化拆分职责 |
| 统一响应封装 | `Response<T>` / `PageResponse` |
| 全局异常处理 | `@RestControllerAdvice` + BizException |
| AOP 日志 | `@ApiOperationLog` 注解式请求日志 (MDC traceId) |
| MapStruct 转换 | DO ↔ VO 对象转换 |
| 事件驱动 | Spring ApplicationEvent (阅读计数、评论通知、全局通知) |
| 定时任务 | `@Scheduled` (每日 PV 记录初始化) |
| 自定义批量插入 | MyBatis-Plus SQL 注入器 |
| RBAC 权限 | 角色-权限细粒度控制 + Caffeine 缓存 |
| WebSocket 推送 | STOMP over SockJS (个人 + 广播通道) |
| 路由守卫 | 前端 `permission.js` Token + 权限检查 |
| Pinia 持久化 | 用户状态/菜单状态跨刷新保持 |
| 周期权限刷新 | 前端 3 分钟轮询权限变更并通知用户 |
| 拖拽排序 | vuedraggable 实现简历模块排序 |
| PDF 导出 | html2canvas 截图 + jsPDF 生成 |
| 简历诊断 | 前端评分引擎 (完整度/量化/关键词/格式) |
