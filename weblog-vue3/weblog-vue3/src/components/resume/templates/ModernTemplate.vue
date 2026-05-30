<template>
    <div class="modern-template font-sans text-gray-800">
        <!-- Header Band -->
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-lg px-6 py-5 mb-5">
            <h1 class="text-2xl font-bold mb-1">{{ name }}</h1>
            <div v-if="personalModule" class="text-sm text-indigo-100 leading-relaxed">
                {{ personalModule.content.split('\n').filter(l => l.trim()).join(' | ') }}
            </div>
        </div>

        <!-- Sections -->
        <div class="space-y-5">
            <template v-for="mod in contentModules" :key="mod.id">
                <section>
                    <h2 class="text-base font-bold text-indigo-700 mb-2 flex items-center gap-2">
                        <span class="w-1 h-5 bg-indigo-500 rounded-full inline-block"></span>
                        {{ mod.title }}
                    </h2>

                    <div v-if="isExperience(mod.type)">
                        <DraggableEntries
                            :entries="entriesMap[mod.id]"
                            :module-id="mod.id"
                            @reorder="handleReorder"
                        >
                            <template #entry="{ entry }">
                                <div class="bg-gray-50 border border-gray-100 rounded-md p-3 mb-2 last:mb-0">
                                    <div v-for="(line, i) in entry.text.split('\n')" :key="i"
                                        :class="i === 0
                                            ? 'text-sm font-semibold text-gray-800'
                                            : 'text-xs text-gray-600 mt-0.5'"
                                    >{{ line }}</div>
                                </div>
                            </template>
                        </DraggableEntries>
                    </div>
                    <div v-else class="text-sm whitespace-pre-wrap text-gray-600 leading-relaxed pl-3 border-l-2 border-indigo-100">
                        {{ mod.content }}
                    </div>
                </section>
            </template>
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

const personalModule = computed(() =>
    props.modules.find(m => m.type === 'personal')
)

const contentModules = computed(() =>
    props.modules.filter(m => m.type !== 'personal')
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
