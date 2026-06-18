import axios from "@/axios"

export function getReminderLogPageList(data) {
    return axios.post("/admin/reminder-log/list", data)
}
