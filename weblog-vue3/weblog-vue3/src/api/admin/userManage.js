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
