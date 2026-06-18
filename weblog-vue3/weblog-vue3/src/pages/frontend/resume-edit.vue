<template>
    <Header></Header>

    <main class="container max-w-screen-xl mx-auto p-4">
        <!-- 顶部操作栏 -->
        <div class="mb-4 flex items-center justify-between">
            <div class="flex items-center gap-2">
                <el-button @click="router.push('/resume/list')">返回列表</el-button>
                <el-button @click="handleImport">
                    <el-icon class="mr-1"><Upload /></el-icon>导入
                </el-button>
                <LanguageSwitcher
                    :modelValue="langState.currentLang"
                    :langs="langState.langs"
                    :default-lang="langState.defaultLang"
                    @update:modelValue="handleLanguageSwitch"
                    @add-language="handleAddLanguage"
                    @remove-language="handleRemoveLanguage"
                />
                <el-button @click="handleDiagnosis">
                    <el-icon class="mr-1"><DataAnalysis /></el-icon>诊断
                </el-button>
            </div>
            <div class="flex items-center gap-2">
                <el-dropdown @command="handleExport" :disabled="exporting">
                    <el-button :loading="exporting">
                        <el-icon class="mr-1"><Download /></el-icon>导出 PDF<el-icon class="ml-1"><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="current">导出当前语言</el-dropdown-item>
                            <el-dropdown-item command="all" :disabled="langState.langs.length <= 1">
                                导出所有语言 (ZIP)
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
            </div>
        </div>

        <!-- 隐藏文件输入 -->
        <input
            ref="fileInputRef"
            type="file"
            accept=".md"
            style="display: none;"
            @change="onFileSelected"
        />

        <!-- 模板选择器 -->
        <TemplateSelector v-model="form.templateId" class="mb-4" />

        <!-- 双栏布局：左侧块编辑器 / 右侧预览 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-4" style="height: calc(100vh - 220px);">
            <!-- 左：模块化编辑器 -->
            <div class="overflow-y-auto pr-2">
                <CoverSettings :modelValue="coverData" @update:modelValue="onCoverChange" />
                <BlockEditor
                    v-model="modules"
                    v-model:resume-name="form.name"
                    @update:modelValue="onModulesChange"
                    @update:resumeName="onNameChange"
                />
            </div>

            <!-- 右：模板实时预览 -->
            <ResumePreview
                ref="previewRef"
                :template-id="form.templateId"
                :name="form.name"
                :modules="modules"
                :cover-data="coverData"
                @update:module-content="handleModuleContentUpdate"
            />
        </div>

        <ResumeDiagnosis ref="diagnosisRef" :modules="modules" :cover-data="coverData" />
    </main>

    <Footer></Footer>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDebounceFn } from '@vueuse/core'
import { Upload, Download, ArrowDown, DataAnalysis } from '@element-plus/icons-vue'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import BlockEditor from '@/components/resume/BlockEditor.vue'
import ResumePreview from '@/components/resume/ResumePreview.vue'
import TemplateSelector from '@/components/resume/TemplateSelector.vue'
import CoverSettings from '@/components/resume/CoverSettings.vue'
import LanguageSwitcher from '@/components/resume/LanguageSwitcher.vue'
import ResumeDiagnosis from '@/components/resume/ResumeDiagnosis.vue'
import { parseResume, serializeResume, validateResumeMarkdown, generateId } from '@/utils/resume-parser'
import { exportResumePdf, exportResumePdfAsBlob } from '@/utils/resume-pdf-export'
import { parseLanguagesData, serializeLanguagesData, getLanguageLabel } from '@/utils/resume-languages'
import { getResumeDetail, updateResume } from '@/api/admin/resume'
import { showMessage } from '@/composables/util'

const route = useRoute()
const router = useRouter()

const form = reactive({
    id: null,
    name: '',
    content: '',
    templateId: ''
})
const modules = ref([])
const saving = ref(false)
const exporting = ref(false)
const previewRef = ref(null)
const fileInputRef = ref(null)
const diagnosisRef = ref(null)
const coverData = reactive({
    avatar: '',
    title: '',
    tagline: '',
    coverLetterEnabled: false,
    coverLetterContent: ''
})

const langState = reactive({
    langs: ['zh'],
    defaultLang: 'zh',
    currentLang: 'zh',
})
const contentMap = reactive({})

let previousModuleIds = []

onMounted(() => {
    const resumeId = route.query.resumeId
    if (resumeId) {
        getResumeDetail({ resumeId: Number(resumeId) }).then(res => {
            if (res.success == true) {
                form.id = res.data.id
                form.templateId = res.data.templateId || 'default'

                if (res.data.coverData) {
                    try {
                        Object.assign(coverData, JSON.parse(res.data.coverData))
                    } catch (e) { /* ignore invalid JSON */ }
                }

                const rawContent = res.data.content || ''

                const langData = parseLanguagesData(res.data.languages)
                langState.langs = langData.langs
                langState.defaultLang = langData.defaultLang
                langState.currentLang = langData.currentLang

                contentMap[langState.defaultLang] = rawContent
                for (const [code, md] of Object.entries(langData.contents)) {
                    contentMap[code] = md
                }

                const currentMarkdown = contentMap[langState.currentLang] || rawContent
                const parsed = parseResume(currentMarkdown)

                form.name = res.data.name || parsed.name
                const personalModule = parsed.modules.find(m => m.type === 'personal')
                if (personalModule && personalModule.content.includes('姓名：你的姓名')) {
                    personalModule.content = personalModule.content.replace('姓名：你的姓名', `姓名：${form.name}`)
                }

                modules.value = parsed.modules
                previousModuleIds = modules.value.map(m => m.id)
                form.content = serializeResume({ name: form.name, modules: modules.value })
            } else {
                showMessage(res.message || '加载失败', 'error')
            }
        }).catch(() => {
            showMessage('加载简历失败，请检查网络或稍后重试', 'error')
        })
    }
})

const debouncedSerialize = useDebounceFn(() => {
    form.content = serializeResume({ name: form.name, modules: modules.value })
    contentMap[langState.currentLang] = form.content
}, 600)

function onModulesChange() {
    const currentIds = modules.value.map(m => m.id)
    const structureChanged = currentIds.length !== previousModuleIds.length ||
        currentIds.some((id, i) => id !== previousModuleIds[i])

    if (structureChanged) {
        syncStructureToOtherLanguages()
    }
    previousModuleIds = currentIds
    debouncedSerialize()
}

function onNameChange(newName) {
    form.name = newName
    debouncedSerialize()
}

const debouncedSaveCover = useDebounceFn(() => {
    if (form.id) {
        updateResume({ id: form.id, coverData: JSON.stringify(coverData) })
    }
}, 800)

function onCoverChange(val) {
    Object.assign(coverData, val)
    debouncedSaveCover()
}

function handleModuleContentUpdate({ moduleId, content }) {
    const mod = modules.value.find(m => m.id === moduleId)
    if (mod) {
        mod.content = content
        debouncedSerialize()
    }
}

// --- Multi-language logic ---

let switchingLang = false

function handleLanguageSwitch(newLang) {
    if (switchingLang) return
    switchingLang = true

    const oldLang = langState.currentLang
    if (oldLang !== newLang) {
        contentMap[oldLang] = serializeResume({ name: form.name, modules: modules.value })
    }

    langState.currentLang = newLang

    const markdown = contentMap[newLang] || ''
    const parsed = parseResume(markdown)
    modules.value = parsed.modules
    previousModuleIds = modules.value.map(m => m.id)
    form.content = markdown

    switchingLang = false
}

function handleAddLanguage({ code, copyFrom }) {
    contentMap[langState.currentLang] = serializeResume({ name: form.name, modules: modules.value })
    langState.langs.push(code)

    if (copyFrom && contentMap[copyFrom]) {
        contentMap[code] = contentMap[copyFrom]
    } else {
        const emptyModules = modules.value.map(m => ({
            id: generateId(),
            title: m.title,
            type: m.type,
            content: '',
        }))
        contentMap[code] = serializeResume({ name: form.name, modules: emptyModules })
    }

    langState.currentLang = code
    const markdown = contentMap[code] || ''
    const parsed = parseResume(markdown)
    modules.value = parsed.modules
    previousModuleIds = modules.value.map(m => m.id)
    form.content = markdown

    showMessage(`已添加 ${getLanguageLabel(code)} 版本`)
}

function handleRemoveLanguage(code) {
    if (code === langState.defaultLang) {
        showMessage('不能删除默认语言', 'warning')
        return
    }
    const idx = langState.langs.indexOf(code)
    if (idx > -1) langState.langs.splice(idx, 1)
    delete contentMap[code]

    if (langState.currentLang === code) {
        handleLanguageSwitch(langState.defaultLang)
    }
    showMessage(`已删除 ${getLanguageLabel(code)} 版本`)
}

function syncStructureToOtherLanguages() {
    const currentIds = modules.value.map(m => m.id)
    const prevIds = previousModuleIds

    if (currentIds.length === prevIds.length && currentIds.every((id, i) => id === prevIds[i])) {
        return
    }

    const added = currentIds.filter(id => !prevIds.includes(id))
    const removed = prevIds.filter(id => !currentIds.includes(id))
    const isReorder = added.length === 0 && removed.length === 0

    for (const lang of langState.langs) {
        if (lang === langState.currentLang) continue
        const md = contentMap[lang]
        if (!md) continue

        const parsed = parseResume(md)
        let otherModules = parsed.modules

        if (removed.length > 0) {
            const removedIndices = removed.map(id => prevIds.indexOf(id))
            otherModules = otherModules.filter((_, i) => !removedIndices.includes(i))
        }

        if (added.length > 0) {
            for (const addedId of added) {
                const addedIdx = currentIds.indexOf(addedId)
                const addedModule = modules.value.find(m => m.id === addedId)
                if (addedModule) {
                    otherModules.splice(addedIdx, 0, {
                        id: generateId(),
                        title: addedModule.title,
                        type: addedModule.type,
                        content: '',
                    })
                }
            }
        }

        if (isReorder && otherModules.length === currentIds.length) {
            const idToIndex = {}
            prevIds.forEach((id, i) => { idToIndex[id] = i })
            const reordered = currentIds.map(id => {
                const oldIdx = idToIndex[id]
                return oldIdx !== undefined && otherModules[oldIdx] ? otherModules[oldIdx] : null
            }).filter(Boolean)
            if (reordered.length === otherModules.length) {
                otherModules = reordered
            }
        }

        contentMap[lang] = serializeResume({ name: form.name, modules: otherModules })
    }
}

// --- Save ---

const handleSave = () => {
    if (!form.name) {
        showMessage('请输入简历名称', 'warning')
        return
    }
    contentMap[langState.currentLang] = serializeResume({ name: form.name, modules: modules.value })
    form.content = contentMap[langState.defaultLang] || ''

    const contents = {}
    for (const lang of langState.langs) {
        if (lang !== langState.defaultLang && contentMap[lang]) {
            contents[lang] = contentMap[lang]
        }
    }
    const languagesJson = serializeLanguagesData({
        langs: langState.langs,
        defaultLang: langState.defaultLang,
        currentLang: langState.currentLang,
        contents,
    })

    saving.value = true
    updateResume({
        id: form.id,
        name: form.name,
        content: form.content,
        templateId: form.templateId,
        coverData: JSON.stringify(coverData),
        languages: languagesJson,
    }).then(res => {
        if (res.success == true) {
            showMessage('保存成功')
        } else {
            showMessage(res.message || '保存失败', 'error')
        }
    }).finally(() => {
        saving.value = false
    })
}

// --- Export ---

async function handleExport(command) {
    if (command === 'current') {
        await handleExportPdf()
    } else if (command === 'all') {
        await handleExportAllLanguages()
    }
}

async function handleExportPdf() {
    const elements = []

    if (previewRef.value?.hasCoverPage()) {
        elements.push(previewRef.value.getCoverPageElement())
    }
    if (previewRef.value?.hasCoverLetter()) {
        elements.push(previewRef.value.getCoverLetterElement())
    }
    const paperEl = previewRef.value?.getPaperElement()
    if (paperEl) {
        elements.push(paperEl)
    }

    if (elements.length === 0) {
        showMessage('预览区域未就绪，请稍后重试', 'warning')
        return
    }
    exporting.value = true
    try {
        const langLabel = getLanguageLabel(langState.currentLang)
        const filename = langState.langs.length > 1 ? `${form.name}_${langLabel}` : form.name || '简历'
        await exportResumePdf(elements, filename)
        showMessage('PDF 导出成功')
    } catch (err) {
        console.error(err)
        showMessage('PDF 导出失败：' + (err.message || '未知错误'), 'error')
    } finally {
        exporting.value = false
    }
}

async function handleExportAllLanguages() {
    const { default: JSZip } = await import('jszip')

    exporting.value = true
    try {
        contentMap[langState.currentLang] = serializeResume({ name: form.name, modules: modules.value })
        const originalLang = langState.currentLang

        const zip = new JSZip()

        for (const lang of langState.langs) {
            const md = contentMap[lang] || ''
            const parsed = parseResume(md)
            modules.value = parsed.modules

            await nextTick()
            await nextTick()
            await new Promise(r => setTimeout(r, 300))

            const elements = []
            if (previewRef.value?.hasCoverPage()) {
                elements.push(previewRef.value.getCoverPageElement())
            }
            if (previewRef.value?.hasCoverLetter()) {
                elements.push(previewRef.value.getCoverLetterElement())
            }
            const paperEl = previewRef.value?.getPaperElement()
            if (paperEl) elements.push(paperEl)

            if (elements.length > 0) {
                const blob = await exportResumePdfAsBlob(elements)
                const langLabel = getLanguageLabel(lang)
                zip.file(`${form.name}_${langLabel}.pdf`, blob)
            }
        }

        const restoreMd = contentMap[originalLang] || ''
        modules.value = parseResume(restoreMd).modules
        previousModuleIds = modules.value.map(m => m.id)
        langState.currentLang = originalLang

        const zipBlob = await zip.generateAsync({ type: 'blob' })
        const url = URL.createObjectURL(zipBlob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${form.name}_all_languages.zip`
        a.click()
        URL.revokeObjectURL(url)

        showMessage('所有语言版本已导出为 ZIP')
    } catch (err) {
        console.error(err)
        showMessage('导出失败：' + (err.message || '未知错误'), 'error')
    } finally {
        exporting.value = false
    }
}

// --- Import ---

function handleImport() {
    fileInputRef.value.click()
}

// --- Diagnosis ---

function handleDiagnosis() {
    diagnosisRef.value.open()
}

function onFileSelected(event) {
    const file = event.target.files?.[0]
    if (!file) return

    const reader = new FileReader()
    reader.onload = (e) => {
        const content = e.target.result
        const result = validateResumeMarkdown(content)

        if (!result.valid) {
            showMessage(result.error, 'error')
        } else {
            form.name = result.data.name || form.name
            modules.value = result.data.modules
            previousModuleIds = modules.value.map(m => m.id)
            contentMap[langState.currentLang] = serializeResume({ name: form.name, modules: modules.value })
            form.content = contentMap[langState.currentLang]
            showMessage(`已导入到 ${getLanguageLabel(langState.currentLang)} 版本`)
        }
    }
    reader.onerror = () => {
        showMessage('文件读取失败', 'error')
    }
    reader.readAsText(file)

    event.target.value = ''
}
</script>
