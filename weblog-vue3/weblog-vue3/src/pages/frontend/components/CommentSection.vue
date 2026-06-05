<template>
    <div class="w-full p-5 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
        <!-- 评论数标题 -->
        <h3 class="text-lg font-semibold text-gray-800 mb-4">
            {{ t('comment.title') }} <span class="text-gray-400 text-sm font-normal">({{ totalCount }})</span>
        </h3>

        <!-- 评论输入框 -->
        <div class="mb-6">
            <textarea
                v-model="commentContent"
                rows="3"
                maxlength="500"
                class="w-full p-3 text-sm text-gray-700 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 resize-none"
                :placeholder="t('comment.placeholder')"
            ></textarea>
            <div class="flex justify-between items-center mt-2">
                <span class="text-xs text-gray-400">{{ commentContent.length }}/500</span>
                <button
                    @click="handlePublishComment"
                    class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none"
                >
                    {{ t('comment.send') }}
                </button>
            </div>
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length > 0" class="space-y-4">
            <div v-for="comment in comments" :key="comment.id" class="border-b border-gray-100 pb-4 last:border-b-0">
                <!-- 根评论 -->
                <div class="flex items-start space-x-3">
                    <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0">
                        <span class="text-blue-600 text-xs font-medium">{{ comment.username.charAt(0).toUpperCase() }}</span>
                    </div>
                    <div class="flex-1 min-w-0">
                        <div class="flex items-center space-x-2">
                            <span class="text-sm font-medium text-gray-800">{{ comment.username }}</span>
                            <span class="text-xs text-gray-400">{{ formatTime(comment.createTime) }}</span>
                        </div>
                        <p class="mt-1 text-sm text-gray-600 break-words">{{ comment.content }}</p>
                        <div class="flex items-center space-x-4 mt-2">
                            <!-- 点赞按钮 -->
                            <button
                                @click="handleLike(comment)"
                                class="flex items-center space-x-1 text-xs transition-colors"
                                :class="isLiked(comment.id) ? 'text-red-500' : 'text-gray-400 hover:text-red-500'"
                            >
                                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                    <path v-if="isLiked(comment.id)" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"/>
                                    <path v-else fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656zM10 15.243l5.414-5.414a2.5 2.5 0 00-3.536-3.536L10 8.172 8.122 6.293a2.5 2.5 0 00-3.536 3.536L10 15.243z" clip-rule="evenodd"/>
                                </svg>
                                <span>{{ comment.likeCount }}</span>
                            </button>
                            <!-- 回复按钮 -->
                            <button
                                @click="toggleReplyInput(comment.id)"
                                class="text-xs text-gray-400 hover:text-blue-500 transition-colors"
                            >
                                {{ t('comment.reply') }}
                            </button>                        </div>

                        <!-- 回复输入框 -->
                        <div v-if="replyingTo === comment.id" class="mt-3">
                            <textarea
                                v-model="replyContent"
                                rows="2"
                                maxlength="500"
                                class="w-full p-2 text-sm text-gray-700 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 resize-none"
                                :placeholder="'回复 ' + comment.username + '...'"
                            ></textarea>
                            <div class="flex justify-end space-x-2 mt-2">
                                <button
                                    @click="replyingTo = null; replyContent = ''"
                                    class="px-3 py-1 text-xs text-gray-500 border border-gray-300 rounded-lg hover:bg-gray-100"
                                >
                                    {{ t('common.cancel') }}
                                </button>
                                <button
                                    @click="handleReply(comment.id)"
                                    class="px-3 py-1 text-xs text-white bg-blue-600 rounded-lg hover:bg-blue-700"
                                >
                                    {{ t('comment.send') }}
                                </button>
                            </div>
                        </div>

                        <!-- 子评论（回复） -->
                        <div v-if="comment.replies && comment.replies.length > 0" class="mt-3 space-y-3 pl-4 border-l-2 border-gray-100">
                            <div v-for="reply in comment.replies" :key="reply.id" class="flex items-start space-x-2">
                                <div class="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0">
                                    <span class="text-green-600 text-xs font-medium">{{ reply.username.charAt(0).toUpperCase() }}</span>
                                </div>
                                <div class="flex-1 min-w-0">
                                    <div class="flex items-center space-x-2">
                                        <span class="text-xs font-medium text-gray-800">{{ reply.username }}</span>
                                        <span v-if="reply.replyToUsername" class="text-xs text-gray-400">
                                            {{ t('comment.reply') }} <span class="text-gray-600">{{ reply.replyToUsername }}</span>
                                        </span>
                                        <span class="text-xs text-gray-400">{{ formatTime(reply.createTime) }}</span>
                                    </div>
                                    <p class="mt-1 text-xs text-gray-600 break-words">{{ reply.content }}</p>
                                    <div class="flex items-center space-x-4 mt-1">
                                        <button
                                            @click="handleLike(reply)"
                                            class="flex items-center space-x-1 text-xs transition-colors"
                                            :class="isLiked(reply.id) ? 'text-red-500' : 'text-gray-400 hover:text-red-500'"
                                        >
                                            <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                                                <path v-if="isLiked(reply.id)" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"/>
                                                <path v-else fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656zM10 15.243l5.414-5.414a2.5 2.5 0 00-3.536-3.536L10 8.172 8.122 6.293a2.5 2.5 0 00-3.536 3.536L10 15.243z" clip-rule="evenodd"/>
                                            </svg>
                                            <span>{{ reply.likeCount }}</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="text-center py-8 text-gray-400 text-sm">
            {{ t('comment.noComments') }}
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="text-center mt-4">
            <button
                @click="loadMore"
                class="px-4 py-2 text-sm text-blue-600 border border-blue-300 rounded-lg hover:bg-blue-50"
            >
                {{ t('comment.loadMore') }}
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { getCommentList, publishComment, likeComment } from '@/api/frontend/comment'
import { useUserStore } from '@/stores/user'
import { showMessage } from '@/composables/util'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
    articleId: {
        type: [String, Number],
        required: true
    }
})

const userStore = useUserStore()
const comments = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref(null)

const isLoggedIn = computed(() => Object.keys(userStore.userInfo).length > 0)

const hasMore = computed(() => {
    return comments.value.length < totalCount.value
})

function getLikedSet() {
    try {
        const stored = localStorage.getItem('liked_comments')
        return stored ? new Set(JSON.parse(stored)) : new Set()
    } catch {
        return new Set()
    }
}

function saveLikedSet(likedSet) {
    localStorage.setItem('liked_comments', JSON.stringify([...likedSet]))
}

function isLiked(commentId) {
    return getLikedSet().has(commentId)
}

function formatTime(timeStr) {
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
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

function loadComments(append = false) {
    getCommentList({
        articleId: props.articleId,
        current: currentPage.value,
        size: pageSize.value
    }).then(res => {
        if (res.success) {
            if (append) {
                comments.value = [...comments.value, ...(res.data || [])]
            } else {
                comments.value = res.data || []
            }
            totalCount.value = res.total || 0
        }
    })
}

function loadMore() {
    currentPage.value++
    loadComments(true)
}

function handlePublishComment() {
    if (!isLoggedIn.value) {
        showMessage(t('message.pleaseLogin'), 'warning')
        return
    }
    if (!commentContent.value.trim()) {
        showMessage(t('validation.commentRequired'), 'warning')
        return
    }
    publishComment({
        articleId: props.articleId,
        content: commentContent.value.trim()
    }).then(res => {
        if (res.success) {
            showMessage(t('message.commentSuccess'), 'success')
            commentContent.value = ''
            currentPage.value = 1
            loadComments()
        }
    })
}

function handleReply(parentId) {
    if (!isLoggedIn.value) {
        showMessage(t('message.pleaseLogin'), 'warning')
        return
    }
    if (!replyContent.value.trim()) {
        showMessage(t('validation.replyRequired'), 'warning')
        return
    }
    publishComment({
        articleId: props.articleId,
        content: replyContent.value.trim(),
        parentId: parentId
    }).then(res => {
        if (res.success) {
            showMessage(t('message.replySuccess'), 'success')
            replyContent.value = ''
            replyingTo.value = null
            currentPage.value = 1
            loadComments()
        }
    })
}

function handleLike(comment) {
    const likedSet = getLikedSet()
    const alreadyLiked = likedSet.has(comment.id)

    if (alreadyLiked) {
        likedSet.delete(comment.id)
        comment.likeCount = Math.max(0, comment.likeCount - 1)
    } else {
        likedSet.add(comment.id)
        comment.likeCount = comment.likeCount + 1
    }
    saveLikedSet(likedSet)

    likeComment({
        commentId: comment.id,
        liked: !alreadyLiked
    })
}

function toggleReplyInput(commentId) {
    if (replyingTo.value === commentId) {
        replyingTo.value = null
        replyContent.value = ''
    } else {
        replyingTo.value = commentId
        replyContent.value = ''
    }
}

onMounted(() => {
    loadComments()
})

watch(() => props.articleId, () => {
    currentPage.value = 1
    comments.value = []
    totalCount.value = 0
    loadComments()
})
</script>
