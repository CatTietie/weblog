import axios from "@/axios"

// 获取首页推荐文章
export function getHomeRecommendations() {
    return axios.post("/recommendation/home")
}

// 获取相关推荐文章
export function getRelatedArticles(articleId) {
    return axios.post("/recommendation/related", { articleId })
}

// 批量上报行为事件
export function reportBehavior(events) {
    return axios.post("/behavior/report", { events })
}

// 上报推荐点击
export function reportRecommendClick(articleId, source) {
    return axios.post("/behavior/click", { articleId, source })
}
