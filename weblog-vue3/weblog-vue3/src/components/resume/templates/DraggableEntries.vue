<template>
    <draggable
        v-model="localEntries"
        item-key="id"
        handle=".entry-drag-handle"
        :animation="200"
        @end="onDragEnd"
    >
        <template #item="{ element }">
            <div class="group relative pl-5 py-1.5">
                <div class="entry-drag-handle absolute left-0 top-1/2 -translate-y-1/2 cursor-grab opacity-0 group-hover:opacity-100 transition-opacity">
                    <svg class="w-3.5 h-3.5 text-gray-400" viewBox="0 0 10 16" fill="currentColor">
                        <circle cx="3" cy="2" r="1.2"/>
                        <circle cx="7" cy="2" r="1.2"/>
                        <circle cx="3" cy="6" r="1.2"/>
                        <circle cx="7" cy="6" r="1.2"/>
                        <circle cx="3" cy="10" r="1.2"/>
                        <circle cx="7" cy="10" r="1.2"/>
                        <circle cx="3" cy="14" r="1.2"/>
                        <circle cx="7" cy="14" r="1.2"/>
                    </svg>
                </div>
                <slot name="entry" :entry="element">
                    <div class="text-sm whitespace-pre-wrap">{{ element.text }}</div>
                </slot>
            </div>
        </template>
    </draggable>
</template>

<script setup>
import { ref, watch } from 'vue'
import draggable from 'vuedraggable'

const props = defineProps({
    entries: { type: Array, required: true },
    moduleId: { type: String, required: true },
})

const emit = defineEmits(['reorder'])

const localEntries = ref([...props.entries])

watch(() => props.entries, (newVal, oldVal) => {
    const newTexts = newVal.map(e => e.text).join('\n')
    const oldTexts = (oldVal || []).map(e => e.text).join('\n')
    if (newTexts !== oldTexts) {
        localEntries.value = [...newVal]
    }
})

function onDragEnd() {
    emit('reorder', props.moduleId, localEntries.value)
}
</script>
