<template>
    <div class="flex gap-3 overflow-x-auto py-2">
        <div
            v-for="tpl in templates"
            :key="tpl.id"
            class="cursor-pointer rounded-lg border-2 p-2 text-center w-28 flex-shrink-0 transition-all"
            :class="modelValue === tpl.id
                ? 'border-blue-500 bg-blue-50 shadow-sm'
                : 'border-gray-200 hover:border-gray-300 bg-white'"
            @click="$emit('update:modelValue', tpl.id)"
        >
            <div class="h-16 bg-gray-50 rounded mb-1.5 flex items-center justify-center">
                <img v-if="tpl.thumbnail" :src="tpl.thumbnail" class="w-10 h-12 object-cover rounded" />
                <!-- Classic: two-column layout -->
                <svg v-else-if="tpl.id === 'default'" class="w-10 h-12" viewBox="0 0 40 52" fill="none">
                    <rect x="1" y="1" width="38" height="50" rx="2" stroke="#cbd5e1" stroke-width="1"/>
                    <rect x="3" y="3" width="11" height="46" rx="1" fill="#475569"/>
                    <rect x="16" y="5" width="20" height="2" rx="1" fill="#94a3b8"/>
                    <rect x="16" y="10" width="18" height="1.5" rx="0.75" fill="#e2e8f0"/>
                    <rect x="16" y="14" width="20" height="1.5" rx="0.75" fill="#e2e8f0"/>
                    <rect x="16" y="18" width="15" height="1.5" rx="0.75" fill="#e2e8f0"/>
                    <rect x="16" y="24" width="20" height="2" rx="1" fill="#94a3b8"/>
                    <rect x="16" y="29" width="18" height="1.5" rx="0.75" fill="#e2e8f0"/>
                    <rect x="16" y="33" width="20" height="1.5" rx="0.75" fill="#e2e8f0"/>
                    <rect x="16" y="37" width="14" height="1.5" rx="0.75" fill="#e2e8f0"/>
                </svg>
                <!-- Modern: header band + cards -->
                <svg v-else-if="tpl.id === 'modern'" class="w-10 h-12" viewBox="0 0 40 52" fill="none">
                    <rect x="1" y="1" width="38" height="50" rx="2" stroke="#cbd5e1" stroke-width="1"/>
                    <rect x="3" y="3" width="34" height="10" rx="2" fill="url(#grad1)"/>
                    <defs><linearGradient id="grad1"><stop stop-color="#6366f1"/><stop offset="1" stop-color="#a855f7"/></linearGradient></defs>
                    <rect x="3" y="16" width="1.5" height="4" rx="0.75" fill="#6366f1"/>
                    <rect x="7" y="16" width="14" height="2" rx="1" fill="#94a3b8"/>
                    <rect x="5" y="21" width="32" height="6" rx="1" stroke="#e2e8f0" stroke-width="0.5" fill="#f9fafb"/>
                    <rect x="5" y="29" width="32" height="6" rx="1" stroke="#e2e8f0" stroke-width="0.5" fill="#f9fafb"/>
                    <rect x="3" y="39" width="1.5" height="4" rx="0.75" fill="#6366f1"/>
                    <rect x="7" y="39" width="12" height="2" rx="1" fill="#94a3b8"/>
                    <rect x="5" y="44" width="32" height="5" rx="1" stroke="#e2e8f0" stroke-width="0.5" fill="#f9fafb"/>
                </svg>
                <!-- Simple: centered single column -->
                <svg v-else-if="tpl.id === 'simple'" class="w-10 h-12" viewBox="0 0 40 52" fill="none">
                    <rect x="1" y="1" width="38" height="50" rx="2" stroke="#cbd5e1" stroke-width="1"/>
                    <rect x="12" y="4" width="16" height="2.5" rx="1" fill="#374151"/>
                    <rect x="10" y="9" width="20" height="1" rx="0.5" fill="#d1d5db"/>
                    <line x1="5" y1="13" x2="35" y2="13" stroke="#e5e7eb"/>
                    <rect x="5" y="16" width="10" height="1.5" rx="0.75" fill="#9ca3af"/>
                    <rect x="5" y="20" width="30" height="1" rx="0.5" fill="#e5e7eb"/>
                    <rect x="5" y="23" width="28" height="1" rx="0.5" fill="#e5e7eb"/>
                    <line x1="5" y1="28" x2="35" y2="28" stroke="#e5e7eb"/>
                    <rect x="5" y="31" width="10" height="1.5" rx="0.75" fill="#9ca3af"/>
                    <rect x="5" y="35" width="30" height="1" rx="0.5" fill="#e5e7eb"/>
                    <rect x="5" y="38" width="26" height="1" rx="0.5" fill="#e5e7eb"/>
                    <line x1="5" y1="43" x2="35" y2="43" stroke="#e5e7eb"/>
                    <rect x="5" y="46" width="10" height="1.5" rx="0.75" fill="#9ca3af"/>
                </svg>
                <!-- Generic fallback for unknown templates -->
                <svg v-else class="w-10 h-12" viewBox="0 0 40 52" fill="none">
                    <rect x="1" y="1" width="38" height="50" rx="2" stroke="#cbd5e1" stroke-width="1"/>
                    <rect x="8" y="12" width="24" height="3" rx="1.5" fill="#94a3b8"/>
                    <rect x="8" y="20" width="24" height="2" rx="1" fill="#e2e8f0"/>
                    <rect x="8" y="26" width="20" height="2" rx="1" fill="#e2e8f0"/>
                    <rect x="8" y="32" width="24" height="2" rx="1" fill="#e2e8f0"/>
                    <rect x="8" y="38" width="16" height="2" rx="1" fill="#e2e8f0"/>
                </svg>
            </div>
            <span class="text-xs text-gray-600 font-medium">{{ tpl.name }}</span>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { TEMPLATES } from './templates/index.js'
import { getEnabledTemplateList } from '@/api/frontend/resumeTemplate'

defineProps({
    modelValue: { type: String, default: 'default' },
})

defineEmits(['update:modelValue'])

const templates = ref(TEMPLATES.map(t => ({ id: t.id, name: t.name, thumbnail: '' })))

async function fetchTemplates() {
    try {
        const res = await getEnabledTemplateList()
        if (res.success && res.data && res.data.length > 0) {
            templates.value = res.data.map(t => ({
                id: t.componentName,
                name: t.name,
                thumbnail: t.thumbnail || '',
                description: t.description || '',
            }))
        }
    } catch (e) {
        // Fallback to local hardcoded templates
    }
}

function onVisibilityChange() {
    if (document.visibilityState === 'visible') {
        fetchTemplates()
    }
}

onMounted(() => {
    fetchTemplates()
    document.addEventListener('visibilitychange', onVisibilityChange)
})

onUnmounted(() => {
    document.removeEventListener('visibilitychange', onVisibilityChange)
})
</script>
