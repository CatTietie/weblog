<template>
    <div class="simple-template font-serif text-gray-700">
        <!-- Name -->
        <h1 class="text-2xl font-bold text-center text-gray-900 mb-1">{{ name }}</h1>

        <!-- Personal info inline -->
        <div v-if="personalModule" class="text-center text-xs text-gray-500 mb-5">
            {{ personalModule.content.split('\n').filter(l => l.trim()).join(' · ') }}
        </div>

        <hr v-if="personalModule" class="border-gray-200 mb-5" />

        <!-- Sections -->
        <div class="space-y-4">
            <template v-for="mod in contentModules" :key="mod.id">
                <section>
                    <h2 class="text-sm font-bold uppercase tracking-wider text-gray-500 mb-2">
                        {{ mod.title }}
                    </h2>

                    <div v-if="isExperience(mod.type)">
                        <DraggableEntries
                            :entries="entriesMap[mod.id]"
                            :module-id="mod.id"
                            @reorder="handleReorder"
                        >
                            <template #entry="{ entry }">
                                <div class="mb-2 last:mb-0 pb-2 border-b border-gray-100 last:border-0">
                                    <div v-for="(line, i) in entry.text.split('\n')" :key="i"
                                        :class="i === 0
                                            ? 'text-sm font-medium text-gray-800'
                                            : 'text-xs text-gray-500 mt-0.5'"
                                    >{{ line }}</div>
                                </div>
                            </template>
                        </DraggableEntries>
                    </div>
                    <div v-else class="text-sm whitespace-pre-wrap text-gray-600 leading-relaxed">
                        {{ mod.content }}
                    </div>

                    <hr class="border-gray-100 mt-3" />
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
