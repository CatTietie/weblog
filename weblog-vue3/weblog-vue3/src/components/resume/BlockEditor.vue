<template>
    <div class="space-y-3">
        <!-- 简历名称 -->
        <div class="bg-white border border-gray-200 rounded-lg p-4 dark:bg-gray-800 dark:border-gray-700">
            <el-input v-model="localName" placeholder="简历名称" size="large" @input="emitName">
                <template #prepend>简历名称</template>
            </el-input>
        </div>

        <!-- 可拖拽模块列表 -->
        <draggable v-model="localModules" item-key="id" handle=".drag-handle" @end="emitModulesImmediate"
            :animation="200">
            <template #item="{ element, index }">
                <div class="bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700 mb-3">
                    <!-- 卡片头部 -->
                    <div class="flex items-center gap-2 p-3 border-b border-gray-100 dark:border-gray-700">
                        <span class="drag-handle cursor-move text-gray-400 hover:text-gray-600 px-1">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                                fill="currentColor">
                                <circle cx="9" cy="6" r="1.5" />
                                <circle cx="15" cy="6" r="1.5" />
                                <circle cx="9" cy="12" r="1.5" />
                                <circle cx="15" cy="12" r="1.5" />
                                <circle cx="9" cy="18" r="1.5" />
                                <circle cx="15" cy="18" r="1.5" />
                            </svg>
                        </span>

                        <span class="text-xs px-2 py-0.5 rounded bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300 whitespace-nowrap">
                            {{ getTypeLabel(element.type) }}
                        </span>

                        <el-input v-model="element.title" size="small" class="flex-1"
                            @input="handleTitleChange(element)" />

                        <el-button size="small" text @click="toggleCollapse(element.id)">
                            <el-icon>
                                <ArrowUp v-if="!collapsed.has(element.id)" />
                                <ArrowDown v-else />
                            </el-icon>
                        </el-button>

                        <el-button size="small" text type="danger" @click="handleDelete(index)">
                            <el-icon><Delete /></el-icon>
                        </el-button>
                    </div>

                    <!-- 卡片内容（可折叠） -->
                    <div v-show="!collapsed.has(element.id)" class="p-3">
                        <MdEditor v-model="element.content"
                            :editor-id="'block-' + element.id"
                            :preview="false"
                            :toolbars="toolbars"
                            :footers="[]"
                            style="height: 200px;"
                            @onChange="onEditorChange(element)">
                            <template #defToolbars>
                                <MarkdownSnippets :type="element.type" />
                            </template>
                        </MdEditor>

                        <!-- 格式校验 + 渲染预览 -->
                        <div class="flex items-center justify-between px-1 pt-2">
                            <FormatWarning :warnings="validationResults[element.id] || []" />
                            <button
                                @click="togglePreview(element.id)"
                                class="text-xs text-gray-400 hover:text-blue-500 transition-colors whitespace-nowrap ml-2"
                            >
                                {{ previewVisible.has(element.id) ? '隐藏预览' : '渲染预览' }}
                            </button>
                        </div>
                        <ModuleRenderPreview
                            :content="element.content"
                            :type="element.type"
                            :module-id="element.id"
                            :visible="previewVisible.has(element.id)"
                        />
                    </div>
                </div>
            </template>
        </draggable>

        <!-- 添加模块 -->
        <div class="text-center pt-2">
            <el-dropdown @command="handleAdd">
                <el-button plain>
                    <el-icon class="mr-1"><Plus /></el-icon>添加模块
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="personal">个人信息</el-dropdown-item>
                        <el-dropdown-item command="education">教育经历</el-dropdown-item>
                        <el-dropdown-item command="work">工作经历</el-dropdown-item>
                        <el-dropdown-item command="project">项目经历</el-dropdown-item>
                        <el-dropdown-item command="skill">技能</el-dropdown-item>
                        <el-dropdown-item command="other">其他</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import draggable from 'vuedraggable'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ArrowUp, ArrowDown, Delete, Plus } from '@element-plus/icons-vue'
import MarkdownSnippets from '@/components/resume/MarkdownSnippets.vue'
import FormatWarning from '@/components/resume/FormatWarning.vue'
import ModuleRenderPreview from '@/components/resume/ModuleRenderPreview.vue'
import { getTypeLabel, getDefaultContent, detectType, generateId } from '@/utils/resume-parser'
import { validateModuleContent } from '@/utils/resume-validation'

const props = defineProps({
    modelValue: { type: Array, required: true },
    resumeName: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'update:resumeName'])

const localModules = ref([...props.modelValue])
const localName = ref(props.resumeName)
const collapsed = reactive(new Set())
const validationResults = reactive({})
const previewVisible = reactive(new Set())
let internalUpdate = false
let pendingValidationIds = new Set()

const toolbars = [0, '-', 'bold', 'italic', 'strikeThrough', '-', 'title', 'unorderedList', 'orderedList', '-', 'link', 'code', 'codeRow']

watch(() => props.modelValue, (val) => {
    if (internalUpdate) return
    localModules.value = [...val]
})

watch(() => props.resumeName, (val) => {
    localName.value = val
})

const debouncedEmit = useDebounceFn(() => {
    internalUpdate = true
    emit('update:modelValue', localModules.value)
    nextTick(() => { internalUpdate = false })
}, 150)

function emitModules() {
    debouncedEmit()
}

function emitModulesImmediate() {
    internalUpdate = true
    emit('update:modelValue', localModules.value)
    nextTick(() => { internalUpdate = false })
}

function emitName() {
    emit('update:resumeName', localName.value)
}

function handleTitleChange(element) {
    element.type = detectType(element.title)
    emitModulesImmediate()
}

function toggleCollapse(id) {
    if (collapsed.has(id)) {
        collapsed.delete(id)
    } else {
        collapsed.add(id)
    }
}

function handleDelete(index) {
    localModules.value.splice(index, 1)
    emitModulesImmediate()
}

function handleAdd(type) {
    localModules.value.push({
        id: generateId(),
        title: getTypeLabel(type),
        type,
        content: getDefaultContent(type),
    })
    emitModulesImmediate()
}

function togglePreview(id) {
    if (previewVisible.has(id)) {
        previewVisible.delete(id)
    } else {
        previewVisible.add(id)
        scheduleValidation(id)
    }
}

function scheduleValidation(moduleId) {
    pendingValidationIds.add(moduleId)
    runValidation()
}

const runValidation = useDebounceFn(() => {
    const idsToValidate = pendingValidationIds.size > 0
        ? pendingValidationIds
        : new Set(localModules.value.map(m => m.id))

    for (const mod of localModules.value) {
        if (idsToValidate.has(mod.id)) {
            validationResults[mod.id] = validateModuleContent(mod.content, mod.type)
        }
    }
    pendingValidationIds = new Set()
}, 800)

function onEditorChange(element) {
    pendingValidationIds.add(element.id)
    runValidation()
    debouncedEmit()
}
</script>

<style>
.md-editor-toolbar-wrapper {
    overflow: visible !important;
}
</style>
