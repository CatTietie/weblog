<template>
  <div class="rounded-lg border-2 border-indigo-500 bg-indigo-50 p-3 min-w-[180px] shadow-sm">
    <Handle type="target" :position="Position.Top" class="!bg-indigo-500" />
    <div class="flex items-center gap-2">
      <el-icon class="text-indigo-600" :size="18"><Link /></el-icon>
      <span class="font-semibold text-sm text-indigo-800">Webhook</span>
    </div>
    <div class="text-xs text-gray-600 mt-1 truncate max-w-[160px]">
      {{ data.url || '未配置URL' }}
    </div>
    <Handle type="source" :position="Position.Bottom" class="!bg-indigo-500" />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { Link } from '@element-plus/icons-vue'

defineProps({ data: { type: Object, default: () => ({}) } })
</script>
