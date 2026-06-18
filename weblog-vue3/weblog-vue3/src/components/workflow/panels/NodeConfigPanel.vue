<template>
  <div class="w-[320px] bg-white border-l border-gray-200 p-4 overflow-y-auto">
    <div v-if="!selectedNode" class="text-center text-gray-400 mt-10">
      <p>点击节点进行配置</p>
    </div>

    <!-- 触发器配置 -->
    <div v-else-if="selectedNode.type === 'trigger'">
      <h3 class="font-semibold text-sm mb-4">触发器配置</h3>
      <el-form label-position="top" size="default">
        <el-form-item label="触发类型">
          <el-select v-model="nodeData.triggerType" @change="emitUpdate">
            <el-option label="文章发布" value="ARTICLE_PUBLISHED" />
            <el-option label="收到评论" value="COMMENT_RECEIVED" />
            <el-option label="新用户注册" value="USER_REGISTERED" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 条件配置 -->
    <div v-else-if="selectedNode.type === 'condition'">
      <h3 class="font-semibold text-sm mb-4">条件配置</h3>
      <el-form label-position="top" size="default">
        <el-form-item label="字段名">
          <el-input v-model="nodeData.field" placeholder="如: category, content" @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="操作符">
          <el-select v-model="nodeData.operator" @change="emitUpdate">
            <el-option label="等于" value="EQUALS" />
            <el-option label="不等于" value="NOT_EQUALS" />
            <el-option label="包含" value="CONTAINS" />
            <el-option label="不包含" value="NOT_CONTAINS" />
            <el-option label="大于" value="GT" />
            <el-option label="小于" value="LT" />
            <el-option label="大于等于" value="GTE" />
            <el-option label="小于等于" value="LTE" />
          </el-select>
        </el-form-item>
        <el-form-item label="比较值">
          <el-input v-model="nodeData.value" placeholder="对比的值" @change="emitUpdate" />
        </el-form-item>
      </el-form>
      <el-alert type="info" :closable="false" class="mt-2">
        <p class="text-xs">左侧输出口(绿色)=条件为真<br>右侧输出口(红色)=条件为假</p>
      </el-alert>
    </div>

    <!-- 延时配置 -->
    <div v-else-if="selectedNode.type === 'delay'">
      <h3 class="font-semibold text-sm mb-4">延时配置</h3>
      <el-form label-position="top" size="default">
        <el-form-item label="等待时间（分钟）">
          <el-input-number v-model="nodeData.delayMinutes" :min="1" :max="10080" @change="emitUpdate" />
        </el-form-item>
      </el-form>
    </div>

    <!-- 邮件配置 -->
    <div v-else-if="selectedNode.type === 'action_email'">
      <h3 class="font-semibold text-sm mb-4">邮件配置</h3>
      <el-form label-position="top" size="default">
        <el-form-item label="收件人邮箱">
          <el-input v-model="nodeData.toEmail" placeholder="支持 {{变量}}" @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="邮件主题">
          <el-input v-model="nodeData.subject" placeholder="支持 {{title}} 等变量" @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="邮件内容 (HTML)">
          <el-input v-model="nodeData.body" type="textarea" :rows="4" placeholder="支持 {{变量}}" @change="emitUpdate" />
        </el-form-item>
      </el-form>
    </div>

    <!-- Webhook 配置 -->
    <div v-else-if="selectedNode.type === 'action_webhook'">
      <h3 class="font-semibold text-sm mb-4">Webhook 配置</h3>
      <el-form label-position="top" size="default">
        <el-form-item label="请求 URL">
          <el-input v-model="nodeData.url" placeholder="https://example.com/hook" @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="请求 Body (JSON, 可选)">
          <el-input v-model="nodeData.body" type="textarea" :rows="4" placeholder="留空则发送全部事件数据" @change="emitUpdate" />
        </el-form-item>
      </el-form>
    </div>

    <!-- 站内通知配置 -->
    <div v-else-if="selectedNode.type === 'action_notification'">
      <h3 class="font-semibold text-sm mb-4">站内通知配置</h3>
      <el-form label-position="top" size="default">
        <el-form-item label="接收人用户ID (可选)">
          <el-input v-model="nodeData.receiverId" placeholder="留空则使用事件中的userId" @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="通知标题">
          <el-input v-model="nodeData.title" placeholder="支持 {{变量}}" @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input v-model="nodeData.content" type="textarea" :rows="3" placeholder="支持 {{变量}}" @change="emitUpdate" />
        </el-form-item>
      </el-form>
    </div>

    <!-- 变量提示 -->
    <div v-if="selectedNode" class="mt-4 p-3 bg-gray-50 rounded">
      <p class="text-xs font-semibold text-gray-600 mb-1">可用变量:</p>
      <p class="text-xs text-gray-500">
        文章: {{title}}, {{articleId}}, {{category}}, {{authorId}}, {{articleUrl}}, {{readNum}}<br>
        评论: {{content}}, {{articleId}}, {{userId}}, {{commentId}}, {{articleUrl}}, {{readNum}}, {{title}}<br>
        用户: {{userId}}, {{username}}, {{email}}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  selectedNode: { type: Object, default: null }
})
const emit = defineEmits(['update:nodeData'])

const nodeData = ref({})

watch(() => props.selectedNode, (node) => {
  if (node) {
    nodeData.value = { ...(node.data || {}) }
  } else {
    nodeData.value = {}
  }
}, { immediate: true, deep: true })

function emitUpdate() {
  emit('update:nodeData', { ...nodeData.value })
}
</script>
