import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading, hidePageLoading } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import i18n from '@/i18n'

const { t } = i18n.global

router.beforeEach((to, from, next) => {
    showPageLoading()

    let token = getToken()

    if (!token && (to.path.startsWith('/admin') || to.path.startsWith('/resume')) && !to.path.startsWith('/resume/s/')) {
        showMessage(t('message.pleaseLogin'), 'warning')
        next({ path: '/login' })
    } else if (token && to.path == '/login') {
        showMessage(t('message.noRepeatLogin'), 'warning')
        next({ path: '/admin/index' })
    } else if (!to.path.startsWith('/admin')) {
        let blogSettingsStore = useBlogSettingsStore()
        blogSettingsStore.getBlogSettings()
        next()
    } else {
        next()
    }
})

router.afterEach((to, from) => {
    let title = (to.meta.titleKey ? t(to.meta.titleKey) : (to.meta.title || '')) + ' - Weblog'
    document.title = title
    hidePageLoading()
})