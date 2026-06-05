<template>
    <!-- 工具箱悬浮按钮 -->
    <div class="fixed bottom-16 right-2 md:bottom-28 md:right-10 z-20">
        <!-- 弹出面板 -->
        <div v-show="showPanel"
            class="absolute bottom-16 right-0 bg-white border border-gray-200 rounded-xl shadow-xl p-4 w-48 dark:bg-gray-800 dark:border-gray-700">
            <p class="text-xs font-medium text-gray-400 mb-3 uppercase tracking-wide">{{ t('toolbox.title') }}</p>
            <a v-if="isLogined" @click="goResume"
                class="flex items-center gap-3 px-3 py-2.5 text-sm text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-600 cursor-pointer transition-colors dark:text-gray-300 dark:hover:bg-gray-700">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                {{ t('toolbox.myResume') }}
            </a>
            <p v-if="!isLogined" class="text-sm text-gray-400 text-center py-3">{{ t('message.pleaseLogin') }}</p>
        </div>

        <!-- 工具箱按钮 -->
        <div @click="showPanel = !showPanel"
            class="w-12 h-12 flex items-center justify-center border border-gray-200 cursor-pointer bg-white hover:bg-blue-50 hover:border-blue-300 rounded-full shadow-md transition-all dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
            <svg class="w-5 h-5 text-gray-600 dark:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const router = useRouter()
const userStore = useUserStore()

const showPanel = ref(false)
const isLogined = ref(Object.keys(userStore.userInfo).length > 0)

const goResume = () => {
    showPanel.value = false
    router.push('/resume/list')
}

const handleClickOutside = (e) => {
    if (showPanel.value && !e.target.closest('.fixed.bottom-16')) {
        showPanel.value = false
    }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))
</script>
