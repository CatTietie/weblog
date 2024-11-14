import axios from "@/axios";


export function getOsCount(data) {
    return axios.get("/admin/userStats/os", {
        params: {
            device: data
        }
    })
}

export function getDeviceCount(data) {
    return axios.get("/admin/userStats/device", data)
}