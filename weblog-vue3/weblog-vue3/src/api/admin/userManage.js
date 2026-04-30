import axios from "@/axios";

export function getUserPageList(data) {
    return axios.post("/admin/users/list", data)
}

export function createUser(data) {
    return axios.post("/admin/users", data)
}

export function getRoleSelectList() {
    return axios.post("/admin/role/select/list")
}

export function getUserDetail(id) {
    return axios.get(`/admin/users/${id}`)
}

export function updateUser(id, data) {
    return axios.put(`/admin/users/${id}`, data)
}

export function deleteUser(id) {
    return axios.delete(`/admin/users/${id}`)
}

export function resetPassword(data) {
    return axios.post("/admin/users/reset-password", data)
}

export function getAllRoles() {
    return axios.get("/admin/roles")
}

export function getRoleById(id) {
    return axios.get(`/admin/roles/${id}`)
}

export function createRole(data) {
    return axios.post("/admin/roles", data)
}

export function updateRole(id, data) {
    return axios.put(`/admin/roles/${id}`, data)
}

export function deleteRole(id) {
    return axios.delete(`/admin/roles/${id}`)
}

export function assignPermissions(data) {
    return axios.post("/admin/roles/assign-permissions", data)
}

export function getAllPermissions() {
    return axios.get("/admin/permissions")
}
