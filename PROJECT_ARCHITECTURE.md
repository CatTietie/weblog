# Weblog 项目架构文档

## 项目概览

Weblog 是一个前后端分离的博客系统，采用 **Spring Boot** 后端 + **Vue 3** 前端架构，包含公开博客展示和后台管理面板两大功能模块。

---

## 技术栈总览

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 (Composition API) + Vite 4 |
| UI 组件库 | Element Plus |
| 状态管理 | Pinia + pinia-plugin-persistedstate |
| 样式方案 | TailwindCSS |
| 图表 | ECharts |
| Markdown | md-editor-v3 + commonmark |
| 后端框架 | Spring Boot 2.6.3 |
| ORM | MyBatis-Plus 3.5.2 |
| 数据库 | MySQL (weblog-cat) |
| 认证 | Spring Security + JWT (JJWT 0.11.2) |
| 对象存储 | 阿里云 OSS |
| API 文档 | Knife4j 4.3.0 |
| 构建工具 | Maven (后端) / npm + Vite (前端) |
| Java 版本 | 1.8 |

---

## 目录结构

```
weblog/
├── db/                            # 数据库 SQL 脚本
│   ├── init.sql                   # 完整建表脚本
│   └── rbac_init.sql              # RBAC 权限模型初始化
├── start-all.bat                  # 一键启动脚本
├── stop-all.bat                   # 一键停止脚本
├── weblog-springboot/             # 后端 (Spring Boot 多模块)
│   ├── pom.xml                    # 父 POM
│   ├── weblog-web/                # 主启动模块 (公开 API)
│   ├── weblog-module-admin/       # 后台管理模块
│   ├── weblog-module-common/      # 公共模块 (实体/工具/配置)
│   └── weblog-module-jwt/         # JWT 认证模块
└── weblog-vue3/
    └── weblog-vue3/               # Vue 3 前端项目
        ├── src/
        │   ├── api/               # API 请求层
        │   ├── components/        # 公共组件
        │   ├── composables/       # 组合式函数
        │   ├── layouts/           # 布局组件
        │   ├── pages/             # 页面
        │   ├── router/            # 路由配置
        │   └── stores/            # Pinia 状态管理
        ├── package.json
        └── vite.config.js
```

---

## 后端架构

### 模块依赖关系

```
weblog-web (启动入口)
├── weblog-module-admin (后台管理)
│   ├── weblog-module-common (公共模块)
│   └── weblog-module-jwt (认证模块)
│       └── weblog-module-common
└── weblog-module-common
```

### 模块说明

#### weblog-web（主启动模块）

公开博客 API 入口，负责博客前台展示相关接口。

| 包 | 职责 |
|---|------|
| `controller` | 公开 API 控制器 (文章/分类/标签/归档/设置) |
| `service/impl` | 前台业务逻辑 |
| `model/vo` | 前台视图对象 |
| `convert` | MapStruct 对象转换器 |
| `markdown` | Markdown 渲染 (commonmark) |
| `config` | Knife4j/Swagger 配置 |

#### weblog-module-admin（后台管理模块）

后台管理面板的所有功能：文章 CRUD、分类标签管理、博客设置、文件上传、仪表盘统计。

| 包 | 职责 |
|---|------|
| `controller` | 管理端控制器 (文章/分类/标签/设置/仪表盘/文件/用户统计) |
| `service/impl` | 管理端业务逻辑 |
| `model/vo` | 管理端请求/响应对象 |
| `config` | Spring Security 配置、线程池、Knife4j |
| `event/subscriber` | Spring 事件 (文章阅读事件) |
| `schedule` | 定时任务 (PV 记录初始化) |

#### weblog-module-common（公共模块）

所有模块共享的基础代码。

| 包 | 职责 |
|---|------|
| `domain/dos` | 数据库实体 (DO) |
| `domain/mapper` | MyBatis-Plus Mapper 接口 |
| `config` | MyBatis-Plus 配置、Jackson 配置 |
| `exception` | 全局异常处理 (`GlobalExceptionHandler`) |
| `enums` | 响应码、用户角色、用户状态枚举 |
| `utils` | 统一响应 `Response<T>`、分页响应、JSON 工具 |
| `aspect` | `@ApiOperationLog` AOP 日志切面 |

#### weblog-module-jwt（JWT 认证模块）

无状态 JWT 认证与 Spring Security 集成。

| 类 | 职责 |
|---|------|
| `JwtAuthenticationFilter` | 拦截登录请求，验证用户名密码 |
| `TokenAuthenticationFilter` | 每次请求校验 JWT Token |
| `RestAuthenticationSuccessHandler` | 登录成功后生成并返回 Token |
| `JwtTokenHelper` | Token 生成、验证、解析工具 |
| `UserDetailServiceImpl` | 从数据库加载用户信息和角色 |
| `WebSecurityConfig` | 安全规则配置 |

---

## 前端架构

### 路由结构

```
/ (Hash 路由)
├── /                         # 博客首页
├── /archive/list             # 归档列表
├── /category/list            # 分类列表
├── /category/article/list    # 分类下文章
├── /tag/list                 # 标签列表
├── /tag/article/list         # 标签下文章
├── /article/:articleId       # 文章详情
├── /login                    # 登录页
└── /admin/index              # 后台 (需认证)
    ├── /article-stats        # 文章统计仪表盘
    ├── /user-stats           # 用户统计
    ├── /article/list         # 文章管理
    ├── /category/list        # 分类管理
    ├── /tag/list             # 标签管理
    ├── /blog/settings        # 博客设置
    ├── /user/list            # 用户管理
    └── /role/list            # 角色管理
```

### API 层组织

```
src/api/
├── admin/                    # 后台管理 API
│   ├── article.js            # 文章 CRUD
│   ├── blogsettings.js       # 博客设置
│   ├── category.js           # 分类管理
│   ├── tag.js                # 标签管理
│   ├── dashboard.js          # 仪表盘统计
│   ├── file.js               # 文件上传
│   ├── user.js               # 登录/注册/用户信息
│   ├── userManage.js         # 用户管理
│   └── userStats.js          # 用户访问统计
└── frontend/                 # 前台公开 API
    ├── article.js            # 文章列表/详情
    ├── archive.js            # 归档
    ├── blogsettings.js       # 博客设置
    ├── category.js           # 分类
    └── tag.js                # 标签
```

### 状态管理 (Pinia)

| Store | 职责 |
|-------|------|
| `user.js` | 用户信息、登出清除 Token |
| `blogsettings.js` | 博客设置缓存 |
| `menu.js` | 管理端侧边栏宽度 (持久化) |

### 前后端通信

- **协议:** REST over HTTP (JSON)
- **代理:** Vite 开发服务器将 `/api` 代理到 `http://localhost:8080`（去除 `/api` 前缀）
- **请求方法:** 几乎所有接口使用 POST（包括查询）
- **认证:** Axios 拦截器自动添加 `Authorization: Bearer <token>` 请求头
- **Token 存储:** Cookie（key: `Authorization`）

---

## 数据库设计

### 核心表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `t_article` | 文章主表 | id, title, cover, summary, readNum, isDeleted |
| `t_article_content` | 文章内容 | articleId, content (TEXT) |
| `t_article_category_rel` | 文章-分类关联 | articleId, categoryId |
| `t_article_tag_rel` | 文章-标签关联 | articleId, tagId |
| `t_category` | 分类 | id, name |
| `t_tag` | 标签 | id, name |
| `t_user` | 用户 | id, username, password, status, roleId |
| `t_user_role` | 用户角色 (旧表) | username, role |
| `t_role` | 角色 (RBAC) | id, name, code, description |
| `t_permission` | 权限 | id, name, code, type, path, method, parentId |
| `t_role_permission` | 角色-权限关联 | roleId, permissionId |
| `t_blog_settings` | 博客设置 | logo, name, author, introduction, avatar |
| `t_statistics_article_pv` | 文章 PV 统计 | pvDate, pvCount |
| `user_visit_stats` | 用户访问统计 | visitTime, userIp, deviceType, browserName, osName, pageUrl |
| `t_article_update_history` | 文章更新历史 | articleId, updateTime |

### 实体关系

```
User (1) ──── (1) Role
Role (1) ──── (N) RolePermission ──── (N) Permission

Article (1) ──── (1) ArticleContent
Article (1) ──── (N) ArticleCategoryRel ──── (N) Category
Article (1) ──── (N) ArticleTagRel ──── (N) Tag
Article (1) ──── (N) ArticleUpdateHistory
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

### 角色权限

| 角色 | 权限范围 |
|------|----------|
| `ROLE_ADMIN` | 完全访问 (所有 CRUD 操作) |
| `ROLE_USER` | 有限访问 (无用户管理) |
| `ROLE_VISITOR` | 只读 (仅仪表盘) |

### 安全配置要点

- CSRF 关闭（无状态 API）
- Session 策略：STATELESS
- `/admin/**` 路径需认证
- 其余路径公开访问
- 写操作通过 `@PreAuthorize("hasRole('ROLE_ADMIN')")` 保护

---

## API 接口汇总

### 公开接口 (无需认证)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/login` | POST | 用户登录 |
| `/register` | POST | 用户注册 |
| `/article/list` | POST | 文章分页列表 |
| `/article/detail` | POST | 文章详情 |
| `/archive/list` | POST | 归档分页 |
| `/blog/settings/detail` | POST | 博客设置 |
| `/category/list` | POST | 分类列表 |
| `/category/article/list` | POST | 分类下文章 |
| `/tag/list` | POST | 标签列表 |
| `/tag/article/list` | POST | 标签下文章 |

### 管理接口 (需 JWT 认证)

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/admin/article/publish` | POST | 发布文章 | ADMIN |
| `/admin/article/delete` | POST | 删除文章 | ADMIN |
| `/admin/article/list` | POST | 文章列表 | 认证即可 |
| `/admin/article/detail` | POST | 文章详情 | 认证即可 |
| `/admin/article/update` | POST | 更新文章 | ADMIN |
| `/admin/category/add` | POST | 添加分类 | ADMIN |
| `/admin/category/delete` | POST | 删除分类 | ADMIN |
| `/admin/category/list` | POST | 分类分页 | 认证即可 |
| `/admin/tag/add` | POST | 添加标签 | ADMIN |
| `/admin/tag/delete` | POST | 删除标签 | ADMIN |
| `/admin/tag/list` | POST | 标签分页 | 认证即可 |
| `/admin/tag/search` | POST | 标签搜索 | 认证即可 |
| `/admin/blog/settings/update` | POST | 更新博客设置 | ADMIN |
| `/admin/blog/settings/detail` | POST | 获取博客设置 | 认证即可 |
| `/admin/file/upload` | POST | 文件上传 (OSS) | ADMIN |
| `/admin/password/update` | POST | 修改密码 | 认证即可 |
| `/admin/user/info` | POST | 当前用户信息 | 认证即可 |
| `/admin/dashboard/statistics` | POST | 仪表盘基础统计 | 认证即可 |
| `/admin/userStats/os` | GET | 操作系统统计 | 认证即可 |
| `/admin/userStats/device` | GET | 设备统计 | 认证即可 |
| `/admin/userStats/browser` | GET | 浏览器统计 | 认证即可 |
| `/admin/userStats/period` | GET | 时段统计 | 认证即可 |

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
- **JWT 过期时间:** 1440 分钟 (24小时)
- **文件上传限制:** 5MB (单文件)
- **API 文档:** http://localhost:8080/doc.html

### 前端

- **端口:** Vite 默认 (5173)
- **代理规则:** `/api` → `http://localhost:8080` (去除 `/api` 前缀)
- **路由模式:** Hash (`/#/path`)

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
| 全局异常处理 | `@RestControllerAdvice` |
| AOP 日志 | `@ApiOperationLog` 注解式请求日志 |
| MapStruct 转换 | DO ↔ VO 对象转换 |
| 事件驱动 | Spring ApplicationEvent (阅读计数) |
| 定时任务 | `@Scheduled` (PV 记录初始化) |
| 自定义批量插入 | MyBatis-Plus SQL 注入器 |
| 路由守卫 | 前端 `permission.js` Token 检查 |
| Pinia 持久化 | 用户状态/菜单状态跨刷新保持 |
