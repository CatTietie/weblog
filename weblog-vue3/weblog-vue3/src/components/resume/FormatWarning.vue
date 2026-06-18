<template>
    <div v-if="warnings.length > 0" class="format-warning">
        <div class="format-warning-bar" @click="expanded = !expanded">
            <span class="format-warning-badge">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15v-2h2v2h-2zm0-4V7h2v6h-2z"/>
                </svg>
            </span>
            <span class="format-warning-text">{{ warnings.length }} 条格式建议</span>
            <span class="format-warning-toggle">{{ expanded ? '收起' : '展开' }}</span>
        </div>
        <ul v-show="expanded" class="format-warning-list">
            <li v-for="(w, i) in warnings" :key="i" :class="'format-warning-item--' + w.level">
                {{ w.message }}
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
    warnings: { type: Array, default: () => [] }
})

const expanded = ref(false)
</script>

<style scoped>
.format-warning {
    margin-top: 6px;
    border-radius: 6px;
    border: 1px solid #fde68a;
    background: #fffbeb;
    font-size: 12px;
    overflow: hidden;
}

.dark .format-warning {
    background: #422006;
    border-color: #854d0e;
}

.format-warning-bar {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 6px 10px;
    cursor: pointer;
    user-select: none;
}

.format-warning-badge {
    color: #d97706;
    display: flex;
    align-items: center;
}

.dark .format-warning-badge {
    color: #fbbf24;
}

.format-warning-text {
    color: #92400e;
    flex: 1;
}

.dark .format-warning-text {
    color: #fde68a;
}

.format-warning-toggle {
    color: #b45309;
    font-size: 11px;
}

.dark .format-warning-toggle {
    color: #f59e0b;
}

.format-warning-list {
    list-style: none;
    padding: 0 10px 8px;
    margin: 0;
}

.format-warning-list li {
    padding: 3px 0;
    color: #78350f;
    line-height: 1.5;
}

.dark .format-warning-list li {
    color: #fde68a;
}

.format-warning-item--info {
    opacity: 0.75;
}

.format-warning-item--info::before {
    content: "💡 ";
}

.format-warning-item--warn::before {
    content: "⚠ ";
}
</style>
