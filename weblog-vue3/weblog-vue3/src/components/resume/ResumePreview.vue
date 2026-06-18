<template>
    <div ref="wrapperRef" class="resume-preview-wrapper">
        <!-- 封面页 -->
        <div v-if="showCoverPage" class="paper-container" :style="containerStyle(A4_HEIGHT_PX)">
            <div ref="coverPageRef" class="resume-paper" :style="coverPageStyle">
                <div class="cover-page">
                    <img v-if="coverData.avatar" :src="coverData.avatar" class="cover-avatar" />
                    <h1 class="cover-title">{{ coverData.title }}</h1>
                    <p v-if="coverData.tagline" class="cover-tagline">{{ coverData.tagline }}</p>
                </div>
            </div>
        </div>

        <!-- 求职信页 -->
        <div v-if="showCoverLetter" class="paper-container" :style="containerStyle(A4_HEIGHT_PX)">
            <div ref="coverLetterRef" class="resume-paper" :style="coverLetterStyle">
                <div class="cover-letter-page">
                    <MdPreview :modelValue="coverData.coverLetterContent" />
                </div>
            </div>
        </div>

        <!-- 简历正文 -->
        <div ref="paperRef" class="resume-paper" :style="paperStyle">
            <component
                :is="activeTemplate.component"
                :name="name"
                :modules="modules"
                @update:module-content="$emit('update:module-content', $event)"
            />
        </div>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useElementSize } from '@vueuse/core'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'
import { getTemplateById } from './templates/index.js'

const props = defineProps({
    templateId: { type: String, default: 'default' },
    name: { type: String, required: true },
    modules: { type: Array, required: true },
    coverData: { type: Object, default: () => ({}) },
})

defineEmits(['update:module-content'])

const wrapperRef = ref(null)
const paperRef = ref(null)
const coverPageRef = ref(null)
const coverLetterRef = ref(null)
const { width: wrapperWidth } = useElementSize(wrapperRef)

const showCoverPage = computed(() => !!(props.coverData.avatar || props.coverData.title))
const showCoverLetter = computed(() => !!(props.coverData.coverLetterEnabled && props.coverData.coverLetterContent))

defineExpose({
    getPaperElement: () => paperRef.value,
    getCoverPageElement: () => coverPageRef.value,
    getCoverLetterElement: () => coverLetterRef.value,
    hasCoverPage: () => showCoverPage.value,
    hasCoverLetter: () => showCoverLetter.value,
})

const A4_WIDTH_PX = 794 // 210mm at 96dpi
const A4_HEIGHT_PX = Math.round(A4_WIDTH_PX * 1.414) // 1123

const activeTemplate = computed(() => getTemplateById(props.templateId))

const scale = computed(() => {
    const available = wrapperWidth.value - 32
    if (available <= 0) return 1
    return Math.min(1, available / A4_WIDTH_PX)
})

const paperStyle = computed(() => {
    if (scale.value === 1) return { width: `${A4_WIDTH_PX}px`, minHeight: `${A4_HEIGHT_PX}px` }
    return {
        transform: `scale(${scale.value})`,
        transformOrigin: 'top center',
        width: `${A4_WIDTH_PX}px`,
        minHeight: `${A4_HEIGHT_PX}px`,
    }
})

const coverPageStyle = computed(() => {
    return {
        transform: `scale(${scale.value})`,
        transformOrigin: 'top left',
        width: `${A4_WIDTH_PX}px`,
        height: `${A4_HEIGHT_PX}px`,
    }
})

const coverLetterStyle = computed(() => {
    return {
        transform: `scale(${scale.value})`,
        transformOrigin: 'top left',
        width: `${A4_WIDTH_PX}px`,
        minHeight: `${A4_HEIGHT_PX}px`,
    }
})

function containerStyle(height) {
    const s = scale.value
    return {
        width: `${A4_WIDTH_PX * s}px`,
        height: `${height * s}px`,
        marginBottom: '16px',
        position: 'relative',
        overflow: 'hidden',
    }
}
</script>

<style scoped>
.resume-preview-wrapper {
    @apply flex flex-col items-center overflow-y-auto h-full bg-gray-100 p-4 rounded-lg;
}
.resume-paper {
    @apply bg-white shadow-2xl p-8 rounded;
    box-shadow: 0 4px 24px rgba(0,0,0,0.12), 0 1px 4px rgba(0,0,0,0.08);
}
.cover-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
}
.cover-avatar {
    width: 128px;
    height: 128px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 24px;
    border: 3px solid #e5e7eb;
}
.cover-title {
    font-size: 36px;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 12px;
}
.cover-tagline {
    font-size: 18px;
    color: #6b7280;
}
.cover-letter-page {
    padding: 16px;
}
</style>
