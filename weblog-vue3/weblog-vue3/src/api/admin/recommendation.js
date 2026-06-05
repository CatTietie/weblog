import axios from "@/axios"

// 获取推荐效果看板
export function getRecommendDashboard() {
    return axios.post("/admin/recommendation/dashboard")
}

// 获取推荐配置列表
export function getRecommendConfigList() {
    return axios.post("/admin/recommendation/config/list")
}

// 添加推荐配置
export function addRecommendConfig(data) {
    return axios.post("/admin/recommendation/config/add", data)
}

// 更新推荐配置
export function updateRecommendConfig(data) {
    return axios.post("/admin/recommendation/config/update", data)
}

// 删除推荐配置
export function deleteRecommendConfig(data) {
    return axios.post("/admin/recommendation/config/delete", data)
}

// 获取用户画像列表
export function getProfileList(current, size) {
    return axios.post(`/admin/recommendation/profile/list?current=${current}&size=${size}`)
}

// 更新用户画像
export function updateUserProfile(data) {
    return axios.post("/admin/recommendation/profile/update", data)
}
