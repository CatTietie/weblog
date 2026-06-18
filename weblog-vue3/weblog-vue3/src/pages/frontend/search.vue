<template>
    <Header></Header>

    <!-- 主内容区域 -->
    <main class="container max-w-screen-xl mx-auto p-4">
        <!-- 搜索标题 -->
        <div class="mb-6">
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
                {{ t('frontend.searchLabel') }}<span class="text-blue-600">{{ keyword }}</span>
            </h1>
            <p class="text-gray-500 mt-2" v-if="total > 0">{{ t('frontend.searchResult', { total: total }) }}</p>
            <p class="text-gray-500 mt-2" v-else-if="searched">{{ t('frontend.noSearchResult') }}</p>
        </div>

        <!-- 文章列表 -->
        <div class="grid grid-cols-1 gap-4">
            <div v-for="(article, index) in articles" :key="index"
                class="bg-white border border-gray-200 rounded-lg p-5 hover:shadow-md transition-shadow dark:bg-gray-800 dark:border-gray-700">
                <div class="flex gap-4">
                    <!-- 封面图 -->
                    <a @click="goArticleDetailPage(article.id)" class="cursor-pointer flex-shrink-0" v-if="article.cover">
                        <img class="rounded-lg w-48 h-32 object-cover" :src="article.cover" />
                    </a>
                    <div class="flex-1">
                        <!-- 文章标题（高亮） -->
                        <a @click="goArticleDetailPage(article.id)" class="cursor-pointer">
                            <h2 class="mb-2 text-xl font-bold tracking-tight text-gray-900 dark:text-white"
                                v-html="article.title"></h2>
                        </a>
                        <!-- 文章摘要（高亮） -->
                        <p class="mb-3 font-normal text-gray-500 dark:text-gray-400" v-html="article.summary"></p>
                        <!-- 发布时间 -->
                        <p class="flex items-center font-normal text-gray-400 text-sm dark:text-gray-400">
                            <svg class="inline w-3 h-3 mr-2 text-gray-400 dark:text-white" aria-hidden="true"
                                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                    stroke-width="2"
                                    d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                            </svg>
                            {{ article.createDate }}
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- 分页 -->
        <nav aria-label="Page navigation" class="mt-10 flex justify-center" v-if="pages > 1">
            <ul class="flex items-center -space-x-px h-10 text-base">
                <!-- 上一页 -->
                <li>
                    <a @click="doSearch(current - 1)"
                        class="flex items-center justify-center px-4 h-10 ml-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-l-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                        :class="[current > 1 ? '' : 'cursor-not-allowed']">
                        <span class="sr-only">{{ t('common.prevPage') }}</span>
                        <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                            viewBox="0 0 6 10">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                stroke-width="2" d="M5 1 1 5l4 4" />
                        </svg>
                    </a>
                </li>
                <!-- 页码 -->
                <li v-for="(pageNo, index) in pages" :key="index">
                    <a @click="doSearch(pageNo)"
                        class="flex items-center justify-center px-4 h-10 leading-tight border dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                        :class="[pageNo == current ? 'text-blue-600 bg-blue-50 border-blue-300 hover:bg-blue-100 hover:text-blue-700' : 'text-gray-500 border-gray-300 bg-white hover:bg-gray-100 hover:text-gray-700']">
                        {{ index + 1 }}
                    </a>
                </li>
                <!-- 下一页 -->
                <li>
                    <a @click="doSearch(current + 1)"
                        class="flex items-center justify-center px-4 h-10 leading-tight text-gray-500 bg-white border border-gray-300 rounded-r-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                        :class="[current < pages ? '' : 'cursor-not-allowed']">
                        <span class="sr-only">{{ t('common.nextPage') }}</span>
                        <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                            viewBox="0 0 6 10">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                stroke-width="2" d="m1 9 4-4-4-4" />
                        </svg>
                    </a>
                </li>
            </ul>
        </nav>
    </main>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchArticles } from '@/api/frontend/article'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const route = useRoute()
const router = useRouter()

const keyword = ref(route.query.keyword || '')
const articles = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)
const searched = ref(false)

function doSearch(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    if (!keyword.value.trim()) return

    searchArticles({ current: currentNo, size: size.value, keyword: keyword.value.trim() }).then((res) => {
        if (res.success) {
            articles.value = res.data || []
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
            searched.value = true
        }
    })
}

// 监听路由变化（keyword 参数变更时重新搜索）
watch(() => route.query.keyword, (newKeyword) => {
    if (newKeyword) {
        keyword.value = newKeyword
        current.value = 1
        doSearch(1)
    }
}, { immediate: true })

const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}
</script>
