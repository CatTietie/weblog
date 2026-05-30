import axios from "@/axios";

export function getResumeList() {
    return axios.post("/admin/resume/list")
}

export function getResumeDetail(data) {
    return axios.post("/admin/resume/detail", data)
}

export function createResume(data) {
    return axios.post("/admin/resume/create", data)
}

export function updateResume(data) {
    return axios.post("/admin/resume/update", data)
}

export function deleteResume(data) {
    return axios.post("/admin/resume/delete", data)
}
