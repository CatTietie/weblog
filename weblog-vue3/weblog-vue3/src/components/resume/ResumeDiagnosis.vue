<template>
    <el-dialog v-model="dialogVisible" title="简历诊断报告" width="600px" :close-on-click-modal="true">
        <div v-if="result" class="diagnosis-content">
            <!-- Overall score -->
            <div class="text-center mb-6">
                <div class="overall-score" :class="scoreColorClass(result.overall)">
                    <span class="score-number">{{ result.overall }}</span>
                    <span class="score-label">综合评分</span>
                </div>
                <p class="overall-comment">{{ overallComment }}</p>
            </div>

            <!-- Dimension scores -->
            <div class="dimensions-section">
                <div v-for="dim in result.dimensions" :key="dim.key" class="dimension-row">
                    <div class="dimension-header">
                        <span class="dimension-label">{{ dim.label }}</span>
                        <span class="dimension-score" :style="{ color: progressColor(dim.score) }">{{ dim.score }}分</span>
                    </div>
                    <el-progress
                        :percentage="dim.score"
                        :color="progressColor(dim.score)"
                        :stroke-width="10"
                        :show-text="false"
                    />
                </div>
            </div>

            <!-- Suggestions -->
            <div class="suggestions-section">
                <h4 class="suggestions-title">改进建议</h4>
                <div v-if="result.suggestions.length > 0" class="suggestions-list">
                    <div
                        v-for="(s, i) in result.suggestions"
                        :key="i"
                        class="suggestion-item"
                        :class="'priority-' + s.priority"
                    >
                        <span class="priority-badge">{{ priorityLabel(s.priority) }}</span>
                        <span class="suggestion-text">{{ s.message }}</span>
                    </div>
                </div>
                <div v-else class="no-suggestions">
                    简历质量很好，暂无改进建议
                </div>
            </div>
        </div>

        <template #footer>
            <div class="dialog-footer">
                <el-button @click="runDiagnosis" :icon="Refresh">重新分析</el-button>
                <el-button @click="dialogVisible = false">关闭</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { diagnoseResume } from '@/utils/resume-diagnosis'

const props = defineProps({
    modules: { type: Array, required: true },
    coverData: { type: Object, required: true },
})

const dialogVisible = ref(false)
const result = ref(null)

function runDiagnosis() {
    result.value = diagnoseResume(props.modules, props.coverData)
}

function open() {
    runDiagnosis()
    dialogVisible.value = true
}

const overallComment = computed(() => {
    if (!result.value) return ''
    const s = result.value.overall
    if (s >= 90) return '简历质量优秀！'
    if (s >= 75) return '简历质量良好，仍有提升空间'
    if (s >= 60) return '简历基本合格，建议按下方建议优化'
    if (s >= 40) return '简历需要较多改进，请参考建议完善内容'
    return '简历内容严重不足，请补充关键模块和描述'
})

function scoreColorClass(score) {
    if (score >= 80) return 'score-excellent'
    if (score >= 60) return 'score-good'
    if (score >= 40) return 'score-fair'
    return 'score-poor'
}

function progressColor(score) {
    if (score >= 80) return '#10b981'
    if (score >= 60) return '#3b82f6'
    if (score >= 40) return '#f59e0b'
    return '#ef4444'
}

function priorityLabel(priority) {
    const map = { high: '重要', medium: '建议', low: '可选' }
    return map[priority] || '建议'
}

defineExpose({ open })
</script>

<style scoped>
.diagnosis-content {
    max-height: 65vh;
    overflow-y: auto;
    padding: 0 4px;
}

.overall-score {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 4px solid;
}

.overall-score.score-excellent { border-color: #10b981; }
.overall-score.score-good { border-color: #3b82f6; }
.overall-score.score-fair { border-color: #f59e0b; }
.overall-score.score-poor { border-color: #ef4444; }

.score-number {
    font-size: 32px;
    font-weight: 700;
    line-height: 1;
}

.score-excellent .score-number { color: #10b981; }
.score-good .score-number { color: #3b82f6; }
.score-fair .score-number { color: #f59e0b; }
.score-poor .score-number { color: #ef4444; }

.score-label {
    font-size: 12px;
    color: #6b7280;
    margin-top: 4px;
}

.overall-comment {
    font-size: 14px;
    color: #6b7280;
    margin-top: 12px;
}

.dimensions-section {
    margin-bottom: 24px;
}

.dimension-row {
    margin-bottom: 16px;
}

.dimension-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 6px;
}

.dimension-label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
}

.dimension-score {
    font-size: 14px;
    font-weight: 500;
}

.suggestions-section {
    border-top: 1px solid #e5e7eb;
    padding-top: 16px;
}

.suggestions-title {
    font-size: 14px;
    font-weight: 600;
    color: #374151;
    margin-bottom: 12px;
}

.suggestions-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.suggestion-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    padding: 8px 12px;
    border-radius: 6px;
    border-left: 3px solid;
    background: #f9fafb;
}

.suggestion-item.priority-high {
    border-left-color: #ef4444;
    background: #fef2f2;
}

.suggestion-item.priority-medium {
    border-left-color: #f59e0b;
    background: #fffbeb;
}

.suggestion-item.priority-low {
    border-left-color: #9ca3af;
    background: #f9fafb;
}

.priority-badge {
    flex-shrink: 0;
    font-size: 12px;
    padding: 1px 6px;
    border-radius: 4px;
    font-weight: 500;
}

.priority-high .priority-badge {
    background: #fee2e2;
    color: #dc2626;
}

.priority-medium .priority-badge {
    background: #fef3c7;
    color: #d97706;
}

.priority-low .priority-badge {
    background: #f3f4f6;
    color: #6b7280;
}

.suggestion-text {
    font-size: 13px;
    color: #4b5563;
    line-height: 1.5;
}

.no-suggestions {
    text-align: center;
    color: #9ca3af;
    padding: 16px 0;
    font-size: 14px;
}

.dialog-footer {
    display: flex;
    justify-content: space-between;
}

:deep(.dark) .dimension-label,
:deep(.dark) .suggestions-title {
    color: #e5e7eb;
}

:deep(.dark) .suggestion-text {
    color: #d1d5db;
}

:deep(.dark) .overall-comment,
:deep(.dark) .score-label {
    color: #9ca3af;
}
</style>
