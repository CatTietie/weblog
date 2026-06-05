import axios from "@/axios"

export function getUnreadCount() {
    return axios.post("/notification/unreadCount")
}

export function getNotificationList(data) {
    return axios.post("/notification/list", data)
}

export function readNotification(data) {
    return axios.post("/notification/read", data)
}

export function readAllNotifications() {
    return axios.post("/notification/readAll")
}
