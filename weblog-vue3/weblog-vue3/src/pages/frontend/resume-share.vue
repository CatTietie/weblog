<template>
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
    </div>
    <div v-else-if="error" class="flex items-center justify-center min-h-screen bg-gray-50">
        <div class="text-center">
            <el-icon :size="48" class="text-gray-300 mb-4"><DocumentDelete /></el-icon>
            <p class="text-xl text-gray-500">{{ error }}</p>
        </div>
    </div>
    <div v-else class="min-h-screen bg-gray-100 py-8">
        <div class="max-w-4xl mx-auto px-4">
            <!-- 多语言切换 -->
            <div v-if="langState.langs.length > 1" class="mb-4 flex justify-center">
                <el-select
                    v-model="langState.currentLang"
                    @change="handleLanguageSwitch"
                    style="width: 140px;"
                >
                    <el-option
                        v-for="lang in langState.langs"
                        :key="lang"
                        :label="getLanguageLabel(lang)"
                        :value="lang"
                    />
                </el-select>
            </div>

            <!-- 简历预览 -->
            <ResumePreview
                :template-id="resumeData.templateId"
                :name="parsedResume.name"
                :modules="parsedResume.modules"
                :cover-data="parsedCoverData"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Loading, DocumentDelete } from '@element-plus/icons-vue'
import ResumePreview from '@/components/resume/ResumePreview.vue'
import { getSharedResume } from '@/api/frontend/resume-share'
import { parseResume } from '@/utils/resume-parser'
import { parseLanguagesData, getLanguageLabel } from '@/utils/resume-languages'

const route = useRoute()
const loading = ref(true)
const error = ref('')
const resumeData = reactive({
    name: '',
    content: '',
    templateId: 'default',
    coverData: '',
    languages: '',
})

const langState = reactive({
    langs: ['zh'],
    defaultLang: 'zh',
    currentLang: 'zh',
})
const contentMap = reactive({})

const parsedResume = computed(() => {
    const md = contentMap[langState.currentLang] || resumeData.content
    if (!md) return { name: '', modules: [] }
    return parseResume(md)
})

const parsedCoverData = computed(() => {
    if (!resumeData.coverData) return {}
    try { return JSON.parse(resumeData.coverData) } catch { return {} }
})

function handleLanguageSwitch(newLang) {
    langState.currentLang = newLang
}

function resetState() {
    loading.value = true
    error.value = ''
    Object.assign(resumeData, { name: '', content: '', templateId: 'default', coverData: '', languages: '' })
    langState.langs = ['zh']
    langState.defaultLang = 'zh'
    langState.currentLang = 'zh'
    Object.keys(contentMap).forEach(key => delete contentMap[key])
}

function fetchResume(shareCode) {
    resetState()

    if (!shareCode) {
        error.value = '无效的分享链接'
        loading.value = false
        return
    }

    getSharedResume(shareCode).then(res => {
        if (res.success) {
            Object.assign(resumeData, res.data)

            const rawContent = resumeData.content || ''
            if (resumeData.languages) {
                const langData = parseLanguagesData(resumeData.languages)
                langState.langs = langData.langs
                langState.defaultLang = langData.defaultLang
                langState.currentLang = langData.currentLang

                contentMap[langState.defaultLang] = rawContent
                for (const [code, md] of Object.entries(langData.contents)) {
                    contentMap[code] = md
                }
            } else {
                contentMap[langState.currentLang] = rawContent
            }
        } else {
            error.value = res.message || '该简历已关闭分享'
        }
    }).catch(() => {
        error.value = '该简历已关闭分享'
    }).finally(() => {
        loading.value = false
    })
}

watch(() => route.params.shareCode, (shareCode) => {
    fetchResume(shareCode)
}, { immediate: true })
</script>
