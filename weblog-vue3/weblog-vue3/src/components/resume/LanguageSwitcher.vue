<template>
    <div class="language-switcher flex items-center gap-2">
        <el-select
            :modelValue="modelValue"
            @update:modelValue="$emit('update:modelValue', $event)"
            size="default"
            style="width: 120px;"
        >
            <el-option
                v-for="lang in langs"
                :key="lang"
                :label="getLanguageLabel(lang)"
                :value="lang"
            />
        </el-select>

        <el-popover
            :visible="showAddPopover"
            placement="bottom"
            :width="280"
            trigger="click"
        >
            <template #reference>
                <el-button @click="showAddPopover = !showAddPopover" :icon="Plus" circle size="small" />
            </template>
            <div class="p-2">
                <p class="text-sm font-medium mb-2">添加语言版本</p>
                <el-select v-model="newLangCode" placeholder="选择语言" class="w-full mb-2">
                    <el-option
                        v-for="opt in availableLanguages"
                        :key="opt.code"
                        :label="opt.label"
                        :value="opt.code"
                    />
                </el-select>
                <el-checkbox v-model="copyFromEnabled" class="mb-2">从现有语言复制内容</el-checkbox>
                <el-select
                    v-if="copyFromEnabled"
                    v-model="copyFromLang"
                    placeholder="选择来源语言"
                    class="w-full mb-3"
                >
                    <el-option
                        v-for="lang in langs"
                        :key="lang"
                        :label="getLanguageLabel(lang)"
                        :value="lang"
                    />
                </el-select>
                <div class="flex justify-end gap-2">
                    <el-button size="small" @click="cancelAdd">取消</el-button>
                    <el-button size="small" type="primary" :disabled="!newLangCode" @click="confirmAdd">确定</el-button>
                </div>
            </div>
        </el-popover>

        <el-tag
            v-for="lang in removableLangs"
            :key="lang"
            closable
            size="small"
            type="info"
            @close="$emit('remove-language', lang)"
        >
            {{ getLanguageLabel(lang) }}
        </el-tag>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { LANGUAGE_OPTIONS, getLanguageLabel } from '@/utils/resume-languages'

const props = defineProps({
    modelValue: { type: String, required: true },
    langs: { type: Array, required: true },
    defaultLang: { type: String, default: 'zh' },
})

const emit = defineEmits(['update:modelValue', 'add-language', 'remove-language'])

const showAddPopover = ref(false)
const newLangCode = ref('')
const copyFromEnabled = ref(true)
const copyFromLang = ref('')

const availableLanguages = computed(() => {
    return LANGUAGE_OPTIONS.filter(opt => !props.langs.includes(opt.code))
})

const removableLangs = computed(() => {
    return props.langs.filter(l => l !== props.defaultLang)
})

function cancelAdd() {
    showAddPopover.value = false
    newLangCode.value = ''
    copyFromEnabled.value = true
    copyFromLang.value = ''
}

function confirmAdd() {
    emit('add-language', {
        code: newLangCode.value,
        copyFrom: copyFromEnabled.value ? (copyFromLang.value || props.langs[0]) : null,
    })
    cancelAdd()
}
</script>
