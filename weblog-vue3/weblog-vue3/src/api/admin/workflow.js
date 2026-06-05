import axios from "@/axios"

export function createWorkflow(data) {
    return axios.post("/admin/workflow/create", data)
}

export function updateWorkflow(data) {
    return axios.post("/admin/workflow/update", data)
}

export function deleteWorkflow(id) {
    return axios.post("/admin/workflow/delete", id)
}

export function getWorkflowDetail(id) {
    return axios.post("/admin/workflow/detail", id)
}

export function getWorkflowPageList(data) {
    return axios.post("/admin/workflow/list", data)
}

export function toggleWorkflow(id) {
    return axios.post("/admin/workflow/toggle", id)
}

export function debugWorkflow(data) {
    return axios.post("/admin/workflow/debug", data)
}

export function getExecutionPageList(params) {
    return axios.post(`/admin/workflow/execution/list?workflowId=${params.workflowId}&current=${params.current}&size=${params.size}`)
}

export function getExecutionLogs(executionId) {
    return axios.post("/admin/workflow/execution/logs", executionId)
}
