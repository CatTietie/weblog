<template>
  <div class="w-[200px] bg-white border-r border-gray-200 p-4 overflow-y-auto">
    <h3 class="text-sm font-semibold text-gray-700 mb-3">节点面板</h3>
    <div class="space-y-2">
      <div v-for="item in nodeTypes" :key="item.type"
           class="p-2 border rounded cursor-grab hover:shadow-md transition-shadow"
           :class="item.borderClass"
           draggable="true"
           @dragstart="onDragStart($event, item)">
        <div class="flex items-center gap-2">
          <el-icon :class="item.iconClass" :size="16"><component :is="item.icon" /></el-icon>
          <span class="text-sm">{{ item.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Lightning, QuestionFilled, Clock, Message, Link, Bell } from '@element-plus/icons-vue'

const nodeTypes = [
  { type: 'trigger', label: '触发器', icon: Lightning, borderClass: 'border-green-300', iconClass: 'text-green-600' },
  { type: 'condition', label: '条件判断', icon: QuestionFilled, borderClass: 'border-amber-300', iconClass: 'text-amber-600' },
  { type: 'delay', label: '延时等待', icon: Clock, borderClass: 'border-purple-300', iconClass: 'text-purple-600' },
  { type: 'action_email', label: '发送邮件', icon: Message, borderClass: 'border-blue-300', iconClass: 'text-blue-600' },
  { type: 'action_webhook', label: 'Webhook', icon: Link, borderClass: 'border-indigo-300', iconClass: 'text-indigo-600' },
  { type: 'action_notification', label: '站内通知', icon: Bell, borderClass: 'border-teal-300', iconClass: 'text-teal-600' },
]

function onDragStart(event, item) {
  event.dataTransfer.setData('application/vueflow', item.type)
  event.dataTransfer.effectAllowed = 'move'
}
</script>
