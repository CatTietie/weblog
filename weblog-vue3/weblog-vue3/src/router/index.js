import Index from '@/pages/frontend/index.vue'
import ArchiveList from '@/pages/frontend/archive-list.vue'
import CategoryList from '@/pages/frontend/category-list.vue'
import CategoryArticleList from '@/pages/frontend/category-article-list.vue'
import TagList from '@/pages/frontend/tag-list.vue'
import TagArticleList from '@/pages/frontend/tag-article-list.vue'
import ArticleDetail from '@/pages/frontend/article-detail.vue'
import ResumeList from '@/pages/frontend/resume-list.vue'
import ResumeEdit from '@/pages/frontend/resume-edit.vue'
import ResumeShare from '@/pages/frontend/resume-share.vue'
import NotFound from '@/pages/frontend/404.vue'
import Login from '@/pages/admin/login.vue'
import AdminIndex from '@/pages/admin/index.vue'
import AdminArticleList from '@/pages/admin/article-list.vue'
import AdminCategoryList from '@/pages/admin/category-list.vue'
import AdminTagList from '@/pages/admin/tag-list.vue'
import AdminBlogSettings from '@/pages/admin/blog-settings.vue'
import AdminUserStatistics from '@/pages/admin/userStatistics.vue'
import AdminUserList from '@/pages/admin/user-list.vue'
import AdminRoleList from '@/pages/admin/role-list.vue'
import AdminResumeTemplateList from '@/pages/admin/resume-template-list.vue'
import AdminCommentList from '@/pages/admin/comment-list.vue'
import AdminNotificationSend from '@/pages/admin/notification-send.vue'
import AdminRecommendDashboard from '@/pages/admin/recommendation-dashboard.vue'
import AdminRecommendConfig from '@/pages/admin/recommendation-config.vue'
import AdminStaticSiteConfig from '@/pages/admin/static-site-config.vue'
import AdminStaticSiteTasks from '@/pages/admin/static-site-tasks.vue'
import AdminWorkflowList from '@/pages/admin/workflow-list.vue'
import AdminWorkflowEditor from '@/pages/admin/workflow-editor.vue'
import AdminReminderLogList from '@/pages/admin/reminder-log-list.vue'
import AdminSensitiveWordList from '@/pages/admin/sensitive-word-list.vue'
import { createRouter, createWebHashHistory } from 'vue-router'

import Admin from '@/layouts/admin/admin.vue'

// 统一在这里声明所有路由
const routes = [
    {
        path: '/',
        component: Index,
        meta: {
            titleKey: 'page.home'
        }
    },
    {
        path: '/archive/list',
        component: ArchiveList,
        meta: {
            titleKey: 'page.archive'
        }
    },
    {
        path: '/category/list',
        component: CategoryList,
        meta: {
            titleKey: 'page.categoryList'
        }
    },
    {
        path: '/category/article/list',
        component: CategoryArticleList,
        meta: {
            titleKey: 'page.categoryArticles'
        }
    },
    {
        path: '/tag/list',
        component: TagList,
        meta: {
            titleKey: 'page.tagList'
        }
    },
    {
        path: '/tag/article/list',
        component: TagArticleList,
        meta: {
            titleKey: 'page.tagArticles'
        }
    },
    {
        path: '/article/:articleId',
        component: ArticleDetail,
        meta: {
            titleKey: 'page.articleDetail'
        }
    },
    {
        path: '/login',
        component: Login,
        meta: {
            titleKey: 'page.login'
        }
    },
    {
        path: '/resume/list',
        component: ResumeList,
        meta: {
            titleKey: 'page.resume'
        }
    },
    {
        path: '/resume/edit',
        component: ResumeEdit,
        meta: {
            titleKey: 'page.resumeEdit'
        }
    },
    {
        path: '/resume/s/:shareCode',
        component: ResumeShare,
        meta: {
            titleKey: 'page.resumePreview'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound,
        meta: {
            titleKey: 'page.notFound'
        }
    },
    {
        path: "/admin/index",
        component: Admin,
        children: [
            {
                path: "/admin/index/article-stats",
                component: AdminIndex,
                meta: {
                    titleKey: 'page.articleStats'
                }
            },
            {
                path:"/admin/index/user-stats",
                component: AdminUserStatistics,
                meta:{
                    titleKey: 'page.userStats'
                }
            },
            {
                path: "/admin/article/list",
                component: AdminArticleList,
                meta: {
                    titleKey: 'page.articleManage'
                }
            },
            {
                path: "/admin/category/list",
                component: AdminCategoryList,
                meta: {
                    titleKey: 'page.categoryManage'
                }
            },
            {
                path: "/admin/tag/list",
                component: AdminTagList,
                meta: {
                    titleKey: 'page.tagManage'
                }
            },
            {
                path: "/admin/blog/settings",
                component: AdminBlogSettings,
                meta: {
                    titleKey: 'page.blogSettings'
                }
            },
            {
                path: "/admin/user/list",
                component: AdminUserList,
                meta: {
                    titleKey: 'page.userManage'
                }
            },
            {
                path: "/admin/role/list",
                component: AdminRoleList,
                meta: {
                    titleKey: 'page.roleManage'
                }
            },
            {
                path: "/admin/resume-template/list",
                component: AdminResumeTemplateList,
                meta: {
                    titleKey: 'page.templateManage'
                }
            },
            {
                path: "/admin/comment/list",
                component: AdminCommentList,
                meta: {
                    titleKey: 'page.commentManage'
                }
            },
            {
                path: "/admin/notification/send",
                component: AdminNotificationSend,
                meta: {
                    titleKey: 'page.sendNotification'
                }
            },
            {
                path: "/admin/recommendation/dashboard",
                component: AdminRecommendDashboard,
                meta: {
                    titleKey: 'page.recommendDashboard'
                }
            },
            {
                path: "/admin/recommendation/config",
                component: AdminRecommendConfig,
                meta: {
                    titleKey: 'page.recommendConfig'
                }
            },
            {
                path: "/admin/static-site/config",
                component: AdminStaticSiteConfig,
                meta: {
                    titleKey: 'page.staticSiteConfig'
                }
            },
            {
                path: "/admin/static-site/tasks",
                component: AdminStaticSiteTasks,
                meta: {
                    titleKey: 'page.staticSiteTasks'
                }
            },
            {
                path: "/admin/workflow/list",
                component: AdminWorkflowList,
                meta: {
                    titleKey: 'page.workflowList'
                }
            },
            {
                path: "/admin/workflow/editor",
                component: AdminWorkflowEditor,
                meta: {
                    titleKey: 'page.workflowEditor'
                }
            },
            {
                path: "/admin/workflow/editor/:id",
                component: AdminWorkflowEditor,
                meta: {
                    titleKey: 'page.workflowEditor'
                }
            },
            {
                path: "/admin/reminder-log/list",
                component: AdminReminderLogList,
                meta: {
                    titleKey: 'page.reminderLog'
                }
            },
            {
                path: "/admin/sensitive-word/list",
                component: AdminSensitiveWordList,
                meta: {
                    titleKey: 'page.sensitiveWord'
                }
            },
        ]

    }
]

// 创建路由
const router = createRouter({
    // 指定路由的历史管理方式，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
    history: createWebHashHistory(),
    // routes: routes 的缩写
    routes, 
})

// 暴露出去
export default router

