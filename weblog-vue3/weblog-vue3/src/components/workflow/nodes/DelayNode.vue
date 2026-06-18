<template>
  <div class="rounded-lg border-2 border-purple-500 bg-purple-50 p-3 min-w-[180px] shadow-sm">
    <Handle type="target" :position="Position.Top" class="!bg-purple-500" />
    <div class="flex items-center gap-2">
      <el-icon class="text-purple-600" :size="18"><Clock /></el-icon>
      <span class="font-semibold text-sm text-purple-800">延时等待</span>
    </div>
    <div class="text-xs text-gray-600 mt-1">
      等待 {{ data.delayMinutes || '?' }} 分钟
    </div>
    <Handle type="source" :position="Position.Bottom" class="!bg-purple-500" />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { Clock } from '@element-plus/icons-vue'

defineProps({ data: { type: Object, default: () => ({}) } })
</script>
