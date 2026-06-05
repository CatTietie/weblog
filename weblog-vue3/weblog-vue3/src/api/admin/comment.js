import axios from "@/axios";

export function getCommentPageList(data) {
    return axios.post("/admin/comment/list", data)
}

export function deleteComment(id) {
    return axios.post("/admin/comment/delete", { id })
}

export function batchUpdateCommentStatus(data) {
    return axios.post("/admin/comment/status/batch-update", data)
}
