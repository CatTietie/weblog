import axios from "@/axios"

export function getSharedResume(shareCode) {
    return axios.get(`/resume/share/${shareCode}`)
}
