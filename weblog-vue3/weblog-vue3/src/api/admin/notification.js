import axios from "@/axios"

export function sendGlobalNotification(data) {
    return axios.post("/admin/notification/sendGlobal", data)
}
