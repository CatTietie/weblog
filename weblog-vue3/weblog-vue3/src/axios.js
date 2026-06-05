import axios from "axios";
import { getToken } from "@/composables/cookie"
import { showMessage} from '@/composables/util'
import { useUserStore } from '@/stores/user'
import i18n from '@/i18n'

const { t } = i18n.global

// 创建 Axios 实例
const instance = axios.create({
    baseURL: "/api", // 你的 API 基础 URL
    timeout: 7000, // 请求超时时间
})


// 添加请求拦截器
instance.interceptors.request.use(function (config) {
    const token = getToken()
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }

    const locale = localStorage.getItem('weblog-locale') || 'zh'
    config.headers['Accept-Language'] = locale === 'zh' ? 'zh-CN' : 'en-US'

    return config;
}, function (error) {
    return Promise.reject(error)
});

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
    return response.data
}, function (error) {
    let status = error.response.status

    if (status == 401) {
        let userStore = useUserStore()
        userStore.logout()
        location.reload()
        return Promise.reject(error)
    }

    if (status == 403) {
        showMessage(t('message.noPermission'), 'error')
        return Promise.reject(error)
    }

    let errorMsg = error.response.data.message || t('message.requestFail')
    showMessage(errorMsg, 'error')

    return Promise.reject(error)
})

// 暴露出去
export default instance;