<template>
  <div class="rounded-lg border-2 border-green-500 bg-green-50 p-3 min-w-[180px] shadow-sm">
    <div class="flex items-center gap-2">
      <el-icon class="text-green-600" :size="18"><Lightning /></el-icon>
      <span class="font-semibold text-sm text-green-800">触发器</span>
    </div>
    <div class="text-xs text-gray-600 mt-1">{{ triggerLabel }}</div>
    <Handle type="source" :position="Position.Bottom" class="!bg-green-500" />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { computed } from 'vue'
import { Lightning } from '@element-plus/icons-vue'

const props = defineProps({ data: { type: Object, default: () => ({}) } })

const triggerLabel = computed(() => {
  const map = { ARTICLE_PUBLISHED: '文章发布时', COMMENT_RECEIVED: '收到评论时', USER_REGISTERED: '新用户注册时' }
  return map[props.data.triggerType] || '未配置触发类型'
})
</script>
