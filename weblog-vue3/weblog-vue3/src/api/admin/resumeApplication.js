import axios from "@/axios"

export function getApplicationPageList(data) {
    return axios.post("/admin/resume/application/list", data)
}

export function addApplication(data) {
    return axios.post("/admin/resume/application/add", data)
}

export function updateApplication(data) {
    return axios.post("/admin/resume/application/update", data)
}

export function deleteApplication(data) {
    return axios.post("/admin/resume/application/delete", data)
}

export function getApplicationStatistics() {
    return axios.post("/admin/resume/application/statistics")
}
