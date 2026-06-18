<template>
  <div class="rounded-lg border-2 border-blue-500 bg-blue-50 p-3 min-w-[180px] shadow-sm">
    <Handle type="target" :position="Position.Top" class="!bg-blue-500" />
    <div class="flex items-center gap-2">
      <el-icon class="text-blue-600" :size="18"><Message /></el-icon>
      <span class="font-semibold text-sm text-blue-800">发送邮件</span>
    </div>
    <div class="text-xs text-gray-600 mt-1 truncate max-w-[160px]">
      {{ data.toEmail || '未配置收件人' }}
    </div>
    <Handle type="source" :position="Position.Bottom" class="!bg-blue-500" />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { Message } from '@element-plus/icons-vue'

defineProps({ data: { type: Object, default: () => ({}) } })
</script>
