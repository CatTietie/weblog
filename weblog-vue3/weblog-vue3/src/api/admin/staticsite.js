import axios from '@/axios'

// 获取静态站点配置
export function getStaticSiteConfig() {
    return axios.post('/admin/static-site/config/detail')
}

// 更新静态站点配置
export function updateStaticSiteConfig(data) {
    return axios.post('/admin/static-site/config/update', data)
}

// 触发静态站点生成
export function triggerGeneration(data) {
    return axios.post('/admin/static-site/generate', data)
}

// 触发部署
export function triggerDeploy() {
    return axios.post('/admin/static-site/deploy')
}

// 获取任务列表
export function getGenTaskList(data) {
    return axios.post('/admin/static-site/task/list', data)
}

// 获取当前任务进度
export function getTaskProgress() {
    return axios.post('/admin/static-site/task/progress')
}
