<template>
    <div class="bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700 mb-3">
        <!-- 头部 -->
        <div class="flex items-center justify-between p-3 border-b border-gray-100 dark:border-gray-700 cursor-pointer select-none"
            @click="expanded = !expanded">
            <div class="flex items-center gap-2">
                <span class="text-xs px-2 py-0.5 rounded bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300 whitespace-nowrap">封面</span>
                <span class="font-medium text-sm">封面设置</span>
            </div>
            <el-icon>
                <ArrowUp v-if="expanded" />
                <ArrowDown v-else />
            </el-icon>
        </div>

        <!-- 封面内容 -->
        <div v-show="expanded" class="p-4 space-y-4">
            <!-- 头像上传 -->
            <div>
                <label class="block text-sm text-gray-600 mb-2">头像</label>
                <el-upload
                    action="#"
                    :auto-upload="false"
                    :show-file-list="false"
                    accept="image/*"
                    :on-change="handleAvatarUpload"
                >
                    <div v-if="localData.avatar" class="relative group">
                        <img :src="localData.avatar" class="w-24 h-24 rounded-full object-cover border" />
                        <div class="absolute inset-0 flex items-center justify-center bg-black/40 rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
                            <span class="text-white text-xs">更换</span>
                        </div>
                    </div>
                    <div v-else class="w-24 h-24 rounded-full border-2 border-dashed border-gray-300 flex items-center justify-center hover:border-blue-400 transition-colors">
                        <el-icon :size="24" class="text-gray-400"><Plus /></el-icon>
                    </div>
                </el-upload>
            </div>

            <!-- 姓名 -->
            <div>
                <label class="block text-sm text-gray-600 mb-1">姓名 / 标题</label>
                <el-input v-model="localData.title" placeholder="请输入姓名" maxlength="30" @input="emitChange" />
            </div>

            <!-- 个人标语 -->
            <div>
                <label class="block text-sm text-gray-600 mb-1">个人标语</label>
                <el-input v-model="localData.tagline" placeholder="一句话介绍自己" maxlength="80" @input="emitChange" />
            </div>

            <!-- 分割线 -->
            <el-divider />

            <!-- 求职信开关 -->
            <div class="flex items-center justify-between">
                <span class="text-sm font-medium">求职信</span>
                <el-switch v-model="localData.coverLetterEnabled" @change="emitChange" />
            </div>

            <!-- 求职信编辑器 -->
            <div v-if="localData.coverLetterEnabled">
                <label class="block text-sm text-gray-600 mb-2">求职信内容</label>
                <MdEditor
                    v-model="localData.coverLetterContent"
                    language="zh-CN"
                    :preview="false"
                    :toolbars="editorToolbars"
                    style="height: 300px;"
                    @onChange="emitChange"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref, watch, nextTick } from 'vue'
import { ArrowUp, ArrowDown, Plus } from '@element-plus/icons-vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { uploadFile } from '@/api/admin/file'
import { showMessage } from '@/composables/util'

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => ({ avatar: '', title: '', tagline: '', coverLetterEnabled: false, coverLetterContent: '' })
    }
})

const emit = defineEmits(['update:modelValue'])

const expanded = ref(false)

const localData = reactive({
    avatar: '',
    title: '',
    tagline: '',
    coverLetterEnabled: false,
    coverLetterContent: ''
})

let syncing = false

watch(() => props.modelValue, (val) => {
    if (val && !syncing) {
        Object.assign(localData, val)
    }
}, { immediate: true, deep: true })

const editorToolbars = [
    'bold', 'italic', 'strikeThrough', '-',
    'title', 'unorderedList', 'orderedList', '-',
    'link', 'quote', '-',
    'revoke', 'next'
]

function emitChange() {
    syncing = true
    emit('update:modelValue', { ...localData })
    nextTick(() => { syncing = false })
}

async function handleAvatarUpload(file) {
    const formData = new FormData()
    formData.append('file', file.raw)
    try {
        const res = await uploadFile(formData)
        if (res.success) {
            localData.avatar = res.data.url
            emitChange()
        } else {
            showMessage(res.message || '上传失败', 'error')
        }
    } catch {
        showMessage('头像上传失败', 'error')
    }
}
</script>
