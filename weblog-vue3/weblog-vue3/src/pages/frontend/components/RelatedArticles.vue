<template>
    <div v-if="relatedArticles.length > 0" class="related-container">
        <h3 class="text-base font-bold text-gray-900 dark:text-white mb-3 px-1">
            {{ t('recommendation.related') }}
        </h3>
        <!-- 桌面端: 垂直列表 -->
        <div class="related-list-desktop">
            <div v-for="article in relatedArticles" :key="article.id"
                class="related-item"
                @click="handleClick(article)">
                <img v-if="article.cover" :src="article.cover"
                    class="related-item-cover" />
                <div class="min-w-0 flex-1">
                    <h4 class="text-sm font-medium text-gray-900 dark:text-white line-clamp-2 leading-snug">
                        {{ article.title }}
                    </h4>
                    <div class="flex items-center mt-1.5 text-xs text-gray-400">
                        <span>{{ article.createDate }}</span>
                        <span class="mx-1.5">·</span>
                        <span>{{ article.readNum || 0 }} {{ t('recommendation.reads') }}</span>
                    </div>
                </div>
            </div>
        </div>
        <!-- 移动端: 横向滚动 -->
        <div class="related-scroll-mobile">
            <div class="related-scroll-inner">
                <div v-for="article in relatedArticles" :key="'m-' + article.id"
                    class="related-card-mobile"
                    @click="handleClick(article)">
                    <img v-if="article.cover" :src="article.cover"
                        class="rounded-t-lg h-24 w-full object-cover" />
                    <div class="p-2.5">
                        <h4 class="text-xs font-semibold text-gray-900 dark:text-white line-clamp-2 leading-snug">
                            {{ article.title }}
                        </h4>
                        <p class="mt-1 text-xs text-gray-400">{{ article.createDate }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getRelatedArticles, reportRecommendClick } from '@/api/frontend/recommendation'

const { t } = useI18n()
const router = useRouter()

const props = defineProps({
    articleId: {
        type: [String, Number],
        required: true
    }
})

const relatedArticles = ref([])

function loadRelated(articleId) {
    if (!articleId) return
    getRelatedArticles(articleId).then(res => {
        if (res.success && res.data) {
            relatedArticles.value = res.data
        }
    }).catch(() => {
        relatedArticles.value = []
    })
}

watch(() => props.articleId, (newId) => {
    loadRelated(newId)
}, { immediate: true })

function handleClick(article) {
    reportRecommendClick(article.id, 2).catch(() => {})
    router.push('/article/' + article.id)
}
</script>

<style scoped>
.related-container {
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    padding: 1rem;
}

/* 桌面端垂直列表 */
.related-list-desktop {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.related-item {
    display: flex;
    gap: 0.75rem;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 0.375rem;
    transition: background-color 0.15s, transform 0.15s;
}

.related-item:hover {
    background-color: #f9fafb;
    transform: translateX(2px);
}

.related-item-cover {
    width: 4.5rem;
    height: 3.5rem;
    border-radius: 0.375rem;
    object-fit: cover;
    flex-shrink: 0;
}

/* 移动端横向滚动 - 默认隐藏 */
.related-scroll-mobile {
    display: none;
    overflow: hidden;
    border-radius: 0.375rem;
}

.related-scroll-inner {
    display: flex;
    gap: 0.75rem;
    padding: 0.25rem 0.25rem 0.5rem;
    overflow-x: auto;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
}

/* 自定义滚动条 - webkit */
.related-scroll-inner::-webkit-scrollbar {
    height: 5px;
}

.related-scroll-inner::-webkit-scrollbar-track {
    background: transparent;
    border-radius: 3px;
    margin: 0 0.25rem;
}

.related-scroll-inner::-webkit-scrollbar-thumb {
    background: #d1d5db;
    border-radius: 3px;
}

.related-scroll-inner::-webkit-scrollbar-thumb:hover {
    background: #9ca3af;
}

/* Firefox 滚动条 */
.related-scroll-inner {
    scrollbar-width: thin;
    scrollbar-color: #d1d5db transparent;
}

.related-card-mobile {
    flex-shrink: 0;
    width: 10rem;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    cursor: pointer;
    transition: box-shadow 0.2s, transform 0.2s;
    overflow: hidden;
}

.related-card-mobile:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-1px);
}

/* 移动端适配 */
@media (max-width: 768px) {
    .related-list-desktop {
        display: none;
    }

    .related-scroll-mobile {
        display: block;
    }

    .related-container {
        padding: 0.75rem;
    }
}

/* 暗色模式 */
:deep(.dark) .related-container {
    background: #1f2937;
    border-color: #374151;
}

:deep(.dark) .related-item:hover {
    background-color: #374151;
}

:deep(.dark) .related-card-mobile {
    background: #1f2937;
    border-color: #374151;
}

:deep(.dark) .related-scroll-inner::-webkit-scrollbar-thumb {
    background: #4b5563;
}

:deep(.dark) .related-scroll-inner::-webkit-scrollbar-thumb:hover {
    background: #6b7280;
}
</style>
