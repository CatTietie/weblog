<template>
    <div class="fixed overflow-y-auto bg-slate-800 h-screen text-white menu-container transition-all duration-300 shadow-2xl" :style="{ width: menuStore.menuWidth }">
        <!-- 顶部 Logo, 指定高度为 64px, 和右边的 Header 头保持一样高 -->
        <div class="flex items-center justify-center h-[64px]">
            <img v-if="menuStore.menuWidth == '250px'" src="@/assets/weblog-logo.png" class="h-[60px]">
            <img v-else src="@/assets/weblog-logo-mini.png" class="h-[60px]">
        </div>

        <!-- 下方菜单 -->
        <el-menu :default-active="defaultActive" @select="handleSelect" :collapse="isCollapse" :collapse-transition="false">
            <template v-for="(item, index) in menus" :key="index">
                <el-sub-menu v-if="item.children" :index="item.path">
                    <template #title>
                        <el-icon>
                            <component :is="item.icon"></component>
                        </el-icon>
                        <span>{{ t(item.nameKey) }}</span>
                    </template>
                    <el-menu-item v-for="(child, childIndex) in item.children" :key="childIndex" :index="child.path">
                        <span>{{ t(child.nameKey) }}</span>
                    </el-menu-item>
                </el-sub-menu>
                <el-menu-item v-else :index="item.path">
                    <el-icon>
                        <component :is="item.icon"></component>
                    </el-icon>
                    <span>{{ t(item.nameKey) }}</span>
                </el-menu-item>
            </template>
        </el-menu>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMenuStore } from '@/stores/menu'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const menuStore = useMenuStore()
const userStore = useUserStore()

const route = useRoute()
const router = useRouter()

const isCollapse = computed(() => !(menuStore.menuWidth == '250px'))

const defaultActive = ref('/admin/index/article-stats')

const handleSelect = (path) => {
    router.push(path)
}

const isAdmin = computed(() => {
    const roles = userStore.userInfo?.roles
    return roles && roles.includes('ROLE_ADMIN')
})

const allMenus = computed(() => [
    {
        'nameKey': 'admin.menu.dashboard',
        'icon': 'Monitor',
        'path': '/admin/index/article-stats',
        'permission': 'dashboard',
        'children': [
            {
                'nameKey': 'admin.menu.articleStats',
                'path': '/admin/index/article-stats'
            },
            {
                'nameKey': 'admin.menu.userStats',
                'path': '/admin/index/user-stats'
            }
        ]
    },
    {
        'nameKey': 'admin.menu.articleManage',
        'icon': 'Document',
        'path': '/admin/article/list',
        'permission': 'article',
    },
    {
        'nameKey': 'admin.menu.categoryManage',
        'icon': 'FolderOpened',
        'path': '/admin/category/list',
        'permission': 'category',
    },
    {
        'nameKey': 'admin.menu.tagManage',
        'icon': 'PriceTag',
        'path': '/admin/tag/list',
        'permission': 'tag',
    },
    {
        'nameKey': 'admin.menu.blogSettings',
        'icon': 'Setting',
        'path': '/admin/blog/settings',
        'permission': 'blogsettings',
    },
    {
        'nameKey': 'admin.menu.commentManage',
        'icon': 'ChatDotRound',
        'path': '/admin/comment/list',
        'permission': 'comment',
    },
    {
        'nameKey': 'admin.menu.notificationManage',
        'icon': 'Bell',
        'path': '/admin/notification/send',
        'permission': 'notification',
    },
    {
        'nameKey': 'admin.menu.userManage',
        'icon': 'User',
        'path': '/admin/user/list',
        'permission': 'user',
    },
    {
        'nameKey': 'admin.menu.roleManage',
        'icon': 'Avatar',
        'path': '/admin/role/list',
        'permission': 'role',
    },
    {
        'nameKey': 'admin.menu.templateManage',
        'icon': 'Files',
        'path': '/admin/resume-template/list',
        'permission': 'template',
    },
    {
        'nameKey': 'admin.menu.recommendManage',
        'icon': 'TrendCharts',
        'path': '/admin/recommendation/dashboard',
        'permission': 'recommendation',
        'children': [
            {
                'nameKey': 'admin.menu.recommendDashboard',
                'path': '/admin/recommendation/dashboard'
            },
            {
                'nameKey': 'admin.menu.recommendConfig',
                'path': '/admin/recommendation/config'
            }
        ]
    },
    {
        'nameKey': 'admin.menu.staticSite',
        'icon': 'Upload',
        'path': '/admin/static-site/config',
        'permission': 'static-site',
        'children': [
            {
                'nameKey': 'admin.menu.staticSiteConfig',
                'path': '/admin/static-site/config'
            },
            {
                'nameKey': 'admin.menu.staticSiteTasks',
                'path': '/admin/static-site/tasks'
            }
        ]
    },
    {
        'nameKey': 'admin.menu.workflowManage',
        'icon': 'Connection',
        'path': '/admin/workflow/list',
        'permission': 'workflow',
    },
    {
        'nameKey': 'admin.menu.reminderLog',
        'icon': 'AlarmClock',
        'path': '/admin/reminder-log/list',
        'permission': 'reminder-log',
    },
    {
        'nameKey': 'admin.menu.sensitiveWord',
        'icon': 'Warning',
        'path': '/admin/sensitive-word/list',
        'permission': 'sensitive_word',
    },
])

const menus = computed(() => {
    const permissions = userStore.userInfo?.permissions || []
    return allMenus.value.filter(item => permissions.includes(item.permission))
})
</script>

<style>
.el-menu {
    background-color: rgb(30 41 59 / 1);
    border-right: 0;
}

.el-sub-menu__title {
    color: #fff;
}

.el-sub-menu__title:hover {
    background-color: #ffffff10;
}

.el-menu-item.is-active {
    background-color: #409eff10;
    color: #fff;
}

.el-menu-item.is-active:before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 2px;
    height: 100%;
    background-color: var(--el-color-primary);
}

.el-menu-item {
    color: #fff;
}

.el-menu-item:hover {
    background-color: #ffffff10;
}
</style>