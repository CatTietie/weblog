<template>
    <div class="classic-template font-sans text-gray-800">
        <!-- Header: Name -->
        <h1 class="text-2xl font-bold text-center mb-4 text-slate-800">{{ name }}</h1>

        <div class="flex gap-6">
            <!-- Left Sidebar -->
            <aside class="w-[30%] flex-shrink-0">
                <div class="bg-slate-700 text-white rounded-lg p-4 space-y-5">
                    <template v-for="mod in sidebarModules" :key="mod.id">
                        <section>
                            <h2 class="text-sm font-semibold uppercase tracking-wide border-b border-slate-500 pb-1 mb-2">
                                {{ mod.title }}
                            </h2>
                            <div class="text-xs leading-relaxed whitespace-pre-wrap text-slate-200">{{ mod.content }}</div>
                        </section>
                    </template>
                </div>
            </aside>

            <!-- Right Main -->
            <main class="flex-1 space-y-4">
                <template v-for="mod in mainModules" :key="mod.id">
                    <section>
                        <h2 class="text-base font-bold text-slate-700 border-b-2 border-slate-300 pb-1 mb-2">
                            {{ mod.title }}
                        </h2>
                        <div v-if="isExperience(mod.type)">
                            <DraggableEntries
                                :entries="entriesMap[mod.id]"
                                :module-id="mod.id"
                                @reorder="handleReorder"
                            >
                                <template #entry="{ entry }">
                                    <div class="mb-2 last:mb-0">
                                        <div v-for="(line, i) in entry.text.split('\n')" :key="i"
                                            :class="i === 0
                                                ? 'text-sm font-semibold text-slate-700'
                                                : 'text-xs text-slate-600 ml-2'"
                                        >{{ line }}</div>
                                    </div>
                                </template>
                            </DraggableEntries>
                        </div>
                        <div v-else class="text-sm whitespace-pre-wrap text-slate-600 leading-relaxed">
                            {{ mod.content }}
                        </div>
                    </section>
                </template>
            </main>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import DraggableEntries from './DraggableEntries.vue'
import { parseModuleEntries, serializeEntries, isExperienceType } from '@/utils/resume-entries'

const props = defineProps({
    name: { type: String, required: true },
    modules: { type: Array, required: true },
})

const emit = defineEmits(['update:module-content'])

const SIDEBAR_TYPES = ['personal', 'skill']

const sidebarModules = computed(() =>
    props.modules.filter(m => SIDEBAR_TYPES.includes(m.type))
)

const mainModules = computed(() =>
    props.modules.filter(m => !SIDEBAR_TYPES.includes(m.type))
)

const entriesMap = computed(() => {
    const map = {}
    for (const mod of props.modules) {
        if (isExperienceType(mod.type)) {
            map[mod.id] = parseModuleEntries(mod.content, mod.type, mod.id)
        }
    }
    return map
})

function isExperience(type) {
    return isExperienceType(type)
}

function handleReorder(moduleId, newEntries) {
    emit('update:module-content', { moduleId, content: serializeEntries(newEntries) })
}
</script>
