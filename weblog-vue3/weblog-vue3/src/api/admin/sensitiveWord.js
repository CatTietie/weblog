import axios from "@/axios";

export function getSensitiveWordPageList(data) {
    return axios.post("/admin/sensitive-word/list", data)
}

export function addSensitiveWord(data) {
    return axios.post("/admin/sensitive-word/add", data)
}

export function deleteSensitiveWord(id) {
    return axios.post("/admin/sensitive-word/delete", { id })
}

export function batchImportSensitiveWords(data) {
    return axios.post("/admin/sensitive-word/batch-import", data)
}

export function startSensitiveScan() {
    return axios.post("/admin/sensitive-word/scan/start")
}

export function getScanTasks() {
    return axios.get("/admin/sensitive-word/scan/tasks")
}

export function getScanProgress(taskId) {
    return axios.get(`/admin/sensitive-word/scan/progress/${taskId}`)
}

export function getScanResults(data) {
    return axios.post("/admin/sensitive-word/scan/results", data)
}

export function handleScanResult(data) {
    return axios.post("/admin/sensitive-word/scan/handle", data)
}
