<template>
  <div class="rounded-lg border-2 border-teal-500 bg-teal-50 p-3 min-w-[180px] shadow-sm">
    <Handle type="target" :position="Position.Top" class="!bg-teal-500" />
    <div class="flex items-center gap-2">
      <el-icon class="text-teal-600" :size="18"><Bell /></el-icon>
      <span class="font-semibold text-sm text-teal-800">站内通知</span>
    </div>
    <div class="text-xs text-gray-600 mt-1 truncate max-w-[160px]">
      {{ data.title || '未配置标题' }}
    </div>
    <Handle type="source" :position="Position.Bottom" class="!bg-teal-500" />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { Bell } from '@element-plus/icons-vue'

defineProps({ data: { type: Object, default: () => ({}) } })
</script>
