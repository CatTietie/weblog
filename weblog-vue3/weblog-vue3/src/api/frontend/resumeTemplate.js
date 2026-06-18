import axios from "@/axios"

export function getEnabledTemplateList() {
    return axios.post("/resume/template/enabled/list")
}
