<template>
  <div class="rounded-lg border-2 border-amber-500 bg-amber-50 p-3 min-w-[180px] shadow-sm rotate-0">
    <Handle type="target" :position="Position.Top" class="!bg-amber-500" />
    <div class="flex items-center gap-2">
      <el-icon class="text-amber-600" :size="18"><QuestionFilled /></el-icon>
      <span class="font-semibold text-sm text-amber-800">条件判断</span>
    </div>
    <div class="text-xs text-gray-600 mt-1">
      {{ conditionLabel }}
    </div>
    <Handle id="true" type="source" :position="Position.Bottom" :style="{ left: '30%' }" class="!bg-green-500" />
    <Handle id="false" type="source" :position="Position.Bottom" :style="{ left: '70%' }" class="!bg-red-500" />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { computed } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'

const props = defineProps({ data: { type: Object, default: () => ({}) } })

const operatorMap = { EQUALS: '=', NOT_EQUALS: '≠', CONTAINS: '包含', NOT_CONTAINS: '不包含', GT: '>', LT: '<', GTE: '≥', LTE: '≤' }

const conditionLabel = computed(() => {
  if (!props.data.field) return '未配置条件'
  return `${props.data.field} ${operatorMap[props.data.operator] || '?'} ${props.data.value || ''}`
})
</script>
