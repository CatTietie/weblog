import SockJS from 'sockjs-client/dist/sockjs'
import Stomp from 'stompjs'
import { getToken } from '@/composables/cookie'
import { ref } from 'vue'

const connected = ref(false)
let stompClient = null
let reconnectTimer = null
let authFailed = false
const MAX_RECONNECT_DELAY = 30000
let reconnectDelay = 2000

function isAuthError(frame) {
    if (!frame) return false
    const msg = (typeof frame === 'string') ? frame : (frame.headers?.message || frame.body || '')
    return /401|403|Unauthorized|Forbidden/i.test(msg)
}

export function useWebSocket() {
    const connect = (userId, onNotification) => {
        const token = getToken()
        if (!token || authFailed) return

        const socket = new SockJS(`/api/ws?token=${token}`)
        stompClient = Stomp.over(socket)
        stompClient.debug = null

        stompClient.connect({}, () => {
            connected.value = true
            reconnectDelay = 2000
            authFailed = false

            stompClient.subscribe(`/user/${userId}/queue/notifications`, (message) => {
                const notification = JSON.parse(message.body)
                onNotification(notification)
            })

            stompClient.subscribe('/topic/global', (message) => {
                const notification = JSON.parse(message.body)
                onNotification(notification)
            })
        }, (errorFrame) => {
            connected.value = false
            if (isAuthError(errorFrame)) {
                authFailed = true
                return
            }
            scheduleReconnect(userId, onNotification)
        })

        socket.onclose = (event) => {
            if (connected.value) {
                connected.value = false
            }
            // HTTP 状态码映射：SockJS close code 1002 通常伴随握手失败
            if (authFailed || event.code === 1002 || event.reason?.includes('401') || event.reason?.includes('403')) {
                authFailed = true
                return
            }
            if (!authFailed) {
                scheduleReconnect(userId, onNotification)
            }
        }
    }

    const scheduleReconnect = (userId, onNotification) => {
        if (reconnectTimer || authFailed) return
        // 重连前再次检查 token 是否存在
        if (!getToken()) {
            authFailed = true
            return
        }
        reconnectTimer = setTimeout(() => {
            reconnectTimer = null
            reconnectDelay = Math.min(reconnectDelay * 2, MAX_RECONNECT_DELAY)
            connect(userId, onNotification)
        }, reconnectDelay)
    }

    const disconnect = () => {
        if (reconnectTimer) {
            clearTimeout(reconnectTimer)
            reconnectTimer = null
        }
        if (stompClient && stompClient.connected) {
            stompClient.disconnect()
        }
        connected.value = false
        authFailed = false
    }

    const resetAuth = () => {
        authFailed = false
    }

    return { connect, disconnect, connected, resetAuth }
}
