<template>
    <div v-if="recommendations.length > 0" class="mb-6">
        <h3 class="text-lg font-bold text-gray-900 dark:text-white mb-3 px-1">
            {{ t('recommendation.title') }}
        </h3>
        <div class="recommend-scroll-container">
            <div class="recommend-scroll-inner">
                <div v-for="article in recommendations" :key="article.id"
                    class="recommend-card"
                    @click="handleClick(article)">
                    <img v-if="article.cover" :src="article.cover"
                        class="rounded-t-lg h-28 sm:h-32 w-full object-cover" />
                    <div class="p-3">
                        <div class="mb-2" v-if="article.tags && article.tags.length > 0">
                            <span v-for="tag in article.tags.slice(0, 2)" :key="tag.id"
                                class="bg-green-100 text-green-800 text-xs font-medium mr-1 px-2 py-0.5 rounded dark:bg-green-900 dark:text-green-300">
                                {{ tag.name }}
                            </span>
                        </div>
                        <h4 class="text-sm font-semibold text-gray-900 dark:text-white line-clamp-2 leading-snug">
                            {{ article.title }}
                        </h4>
                        <p class="mt-1.5 text-xs text-gray-400">{{ article.createDate }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getHomeRecommendations, reportRecommendClick } from '@/api/frontend/recommendation'

const { t } = useI18n()
const router = useRouter()
const recommendations = ref([])

onMounted(() => {
    getHomeRecommendations().then(res => {
        if (res.success && res.data) {
            recommendations.value = res.data
        }
    }).catch(() => {})
})

function handleClick(article) {
    reportRecommendClick(article.id, 1).catch(() => {})
    router.push('/article/' + article.id)
}
</script>

<style scoped>
.recommend-scroll-container {
    position: relative;
    overflow: hidden;
    border-radius: 0.5rem;
}

.recommend-scroll-inner {
    display: flex;
    gap: 1rem;
    padding: 0.25rem 0.25rem 0.75rem;
    overflow-x: auto;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
}

/* 自定义滚动条 */
.recommend-scroll-inner::-webkit-scrollbar {
    height: 6px;
}

.recommend-scroll-inner::-webkit-scrollbar-track {
    background: transparent;
    border-radius: 3px;
    margin: 0 0.5rem;
}

.recommend-scroll-inner::-webkit-scrollbar-thumb {
    background: #d1d5db;
    border-radius: 3px;
    transition: background 0.2s;
}

.recommend-scroll-inner::-webkit-scrollbar-thumb:hover {
    background: #9ca3af;
}

/* Firefox 滚动条 */
.recommend-scroll-inner {
    scrollbar-width: thin;
    scrollbar-color: #d1d5db transparent;
}

.recommend-card {
    flex-shrink: 0;
    width: 15rem;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    cursor: pointer;
    transition: box-shadow 0.2s, transform 0.2s;
    overflow: hidden;
}

.recommend-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}

/* 移动端适配 */
@media (max-width: 640px) {
    .recommend-card {
        width: 11rem;
    }

    .recommend-scroll-inner {
        gap: 0.75rem;
        padding-left: 0.25rem;
        padding-right: 0.25rem;
    }
}

/* 暗色模式 */
:deep(.dark) .recommend-card {
    background: #1f2937;
    border-color: #374151;
}

:deep(.dark) .recommend-scroll-inner::-webkit-scrollbar-thumb {
    background: #4b5563;
}

:deep(.dark) .recommend-scroll-inner::-webkit-scrollbar-thumb:hover {
    background: #6b7280;
}
</style>
