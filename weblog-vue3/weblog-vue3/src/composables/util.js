import nprogress from "nprogress"
import i18n from '@/i18n'

export function showMessage(message = '', type = 'success', customClass = '') {
    return ElMessage({
        type: type,
        message,
        customClass,
    })
}

export function showModel(content = '', type = 'warning', title = '') {
    const { t } = i18n.global
    return ElMessageBox.confirm(
        content,
        title,
        {
            confirmButtonText: t('common.confirm'),
            cancelButtonText: t('common.cancel'),
            type,
        }
    )
}

export function showPageLoading() {
    nprogress.start()
}

export function hidePageLoading() {
    nprogress.done()
}