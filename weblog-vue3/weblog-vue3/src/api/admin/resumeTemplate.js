import axios from "@/axios"

export function getResumeTemplatePageList(data) {
    return axios.post("/admin/resume/template/list", data)
}

export function addResumeTemplate(data) {
    return axios.post("/admin/resume/template/add", data)
}

export function updateResumeTemplate(data) {
    return axios.post("/admin/resume/template/update", data)
}

export function deleteResumeTemplate(data) {
    return axios.post("/admin/resume/template/delete", data)
}

export function updateResumeTemplateStatus(data) {
    return axios.post("/admin/resume/template/status/update", data)
}
