<template>
    <div class="relative ml-2 mr-2" ref="bellRef">
        <!-- 铃铛按钮 -->
        <button @click="toggleDropdown"
            class="relative text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-200 rounded-lg p-2">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9">
                </path>
            </svg>
            <!-- 未读数 badge -->
            <span v-if="unreadCount > 0"
                class="absolute -top-0.5 -right-0.5 bg-red-500 text-white text-xs rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1 leading-none">
                {{ unreadCount > 99 ? '99+' : unreadCount }}
            </span>
        </button>

        <!-- 下拉面板 -->
        <div v-show="showDropdown"
            class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
            <!-- 头部 -->
            <div class="p-3 border-b border-gray-200 flex justify-between items-center">
                <span class="font-medium text-gray-900 text-sm">{{ t('notification.title') }}</span>
                <button v-if="unreadCount > 0" @click="handleReadAll"
                    class="text-xs text-blue-600 hover:underline">{{ t('notification.markAllRead') }}</button>
            </div>
            <!-- 通知列表 -->
            <div class="max-h-80 overflow-y-auto">
                <div v-for="item in notifications" :key="item.id" @click="handleClickNotification(item)" :class="[
                    item.isRead ? 'bg-white' : 'bg-blue-50'
                ]" class="p-3 border-b border-gray-100 cursor-pointer hover:bg-gray-50 transition-colors">
                    <p class="text-sm font-medium text-gray-900 truncate">{{ item.title }}</p>
                    <p class="text-xs text-gray-500 mt-1 line-clamp-2">{{ item.content }}</p>
                    <p class="text-xs text-gray-400 mt-1">{{ formatTime(item.createTime) }}</p>
                </div>
                <div v-if="notifications.length === 0" class="p-6 text-center text-gray-400 text-sm">
                    {{ t('notification.noNotification') }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUnreadCount, getNotificationList, readNotification, readAllNotifications } from '@/api/frontend/notification'
import { useWebSocket } from '@/composables/useWebSocket'
import { useDesktopNotification } from '@/composables/useDesktopNotification'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const router = useRouter()
const userStore = useUserStore()
const bellRef = ref(null)
const unreadCount = ref(0)
const notifications = ref([])
const showDropdown = ref(false)
let timer = null

const { connect, disconnect, resetAuth } = useWebSocket()
const { requestPermission, showNotification } = useDesktopNotification()

const fetchUnreadCount = () => {
    getUnreadCount().then(res => {
        if (res.success) {
            unreadCount.value = res.data || 0
        }
    })
}

const fetchNotifications = () => {
    getNotificationList({ current: 1, size: 10 }).then(res => {
        if (res.success) {
            notifications.value = res.data || []
        }
    })
}

const handleWebSocketMessage = (notification) => {
    unreadCount.value++
    notifications.value.unshift({
        ...notification,
        isRead: false
    })
    if (notifications.value.length > 10) {
        notifications.value.pop()
    }
    showNotification(notification.title, notification.content)
}

const toggleDropdown = () => {
    showDropdown.value = !showDropdown.value
    if (showDropdown.value) {
        fetchNotifications()
    }
}

const handleClickNotification = (item) => {
    if (!item.isRead) {
        readNotification({ id: item.id }).then(res => {
            if (res.success) {
                item.isRead = true
                unreadCount.value = Math.max(0, unreadCount.value - 1)
            }
        })
    }
    showDropdown.value = false
    if (item.articleId) {
        router.push('/article/' + item.articleId)
    }
}

const handleReadAll = () => {
    readAllNotifications().then(res => {
        if (res.success) {
            unreadCount.value = 0
            notifications.value.forEach(item => {
                item.isRead = true
            })
        }
    })
}

const formatTime = (timeStr) => {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    const now = new Date()
    const diff = now - date
    const minutes = Math.floor(diff / 60000)
    if (minutes < 1) return t('time.justNow')
    if (minutes < 60) return minutes + ' ' + t('time.minutesAgo')
    const hours = Math.floor(minutes / 60)
    if (hours < 24) return hours + ' ' + t('time.hoursAgo')
    const days = Math.floor(hours / 24)
    if (days < 30) return days + ' ' + t('time.daysAgo')
    return timeStr.substring(0, 10)
}

const handleClickOutside = (event) => {
    if (bellRef.value && !bellRef.value.contains(event.target)) {
        showDropdown.value = false
    }
}

onMounted(() => {
    fetchUnreadCount()
    requestPermission()

    const userId = userStore.userInfo?.id
    if (userId) {
        resetAuth()
        connect(userId, handleWebSocketMessage)
    }

    // 5分钟兜底轮询
    timer = setInterval(fetchUnreadCount, 300000)
    document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
    disconnect()
    if (timer) clearInterval(timer)
    document.removeEventListener('click', handleClickOutside)
})
</script>
