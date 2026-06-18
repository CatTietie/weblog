<template>
    <div v-show="visible" class="module-render-preview">
        <div class="preview-label">渲染预览</div>
        <div class="preview-content">
            <!-- 经验类型：展示条目解析效果 -->
            <template v-if="isExperience">
                <div v-for="(entry, idx) in entries" :key="entry.id" class="preview-entry">
                    <div v-for="(line, lineIdx) in getLines(entry.text)" :key="lineIdx"
                        :class="lineIdx === 0 ? 'preview-line-header' : 'preview-line-body'">
                        {{ line }}
                    </div>
                </div>
                <div v-if="entries.length === 0" class="preview-empty">暂无内容</div>
            </template>
            <!-- 非经验类型：纯文本展示 -->
            <template v-else>
                <div v-if="content && content.trim()" class="preview-plain">{{ content }}</div>
                <div v-else class="preview-empty">暂无内容</div>
            </template>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { parseModuleEntries, isExperienceType } from '@/utils/resume-entries'

const props = defineProps({
    content: { type: String, default: '' },
    type: { type: String, default: 'other' },
    moduleId: { type: String, default: '' },
    visible: { type: Boolean, default: false }
})

const isExperience = computed(() => isExperienceType(props.type))

const entries = computed(() => {
    if (!props.visible || !isExperience.value || !props.content) return []
    return parseModuleEntries(props.content, props.type, props.moduleId)
})

function getLines(text) {
    return text ? text.split('\n').filter(l => l.trim()) : []
}
</script>

<style scoped>
.module-render-preview {
    margin-top: 8px;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    background: #f9fafb;
    max-height: 150px;
    overflow-y: auto;
    padding: 8px 12px;
}

.dark .module-render-preview {
    background: #1f2937;
    border-color: #374151;
}

.preview-label {
    font-size: 11px;
    color: #9ca3af;
    margin-bottom: 6px;
    font-weight: 500;
}

.preview-content {
    font-size: 12px;
}

.preview-entry {
    padding: 4px 0;
    border-bottom: 1px dashed #e5e7eb;
}

.dark .preview-entry {
    border-bottom-color: #374151;
}

.preview-entry:last-child {
    border-bottom: none;
}

.preview-line-header {
    font-weight: 600;
    color: #1f2937;
    line-height: 1.6;
}

.dark .preview-line-header {
    color: #f3f4f6;
}

.preview-line-body {
    color: #6b7280;
    padding-left: 12px;
    line-height: 1.5;
}

.dark .preview-line-body {
    color: #9ca3af;
}

.preview-plain {
    white-space: pre-wrap;
    color: #374151;
    line-height: 1.6;
}

.dark .preview-plain {
    color: #d1d5db;
}

.preview-empty {
    color: #9ca3af;
    font-style: italic;
}
</style>
