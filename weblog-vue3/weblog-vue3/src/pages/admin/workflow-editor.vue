<template>
  <div class="h-[calc(100vh-120px)] flex flex-col">
    <!-- 顶部工具栏 -->
    <div class="flex items-center justify-between px-4 py-2 bg-white border-b">
      <div class="flex items-center gap-3">
        <el-button @click="$router.push('/admin/workflow/list')" :icon="ArrowLeft" text>返回</el-button>
        <el-input v-model="workflowName" placeholder="工作流名称" class="w-60" />
        <el-select v-model="triggerType" placeholder="触发类型" class="w-40">
          <el-option label="文章发布" value="ARTICLE_PUBLISHED" />
          <el-option label="收到评论" value="COMMENT_RECEIVED" />
          <el-option label="新用户注册" value="USER_REGISTERED" />
        </el-select>
      </div>
      <div class="flex items-center gap-2">
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        <el-button @click="handleDebug">调试</el-button>
      </div>
    </div>

    <!-- 主体编辑区 -->
    <div class="flex flex-1 overflow-hidden">
      <!-- 左侧节点面板 -->
      <NodePalette />

      <!-- 中间画布 -->
      <div class="flex-1" ref="flowContainer" @drop="onDrop" @dragover.prevent @dragenter.prevent>
        <VueFlow
          v-model:nodes="nodes"
          v-model:edges="edges"
          :node-types="nodeTypes"
          :default-edge-options="{ type: 'smoothstep', animated: true }"
          fit-view-on-init
          @node-click="onNodeClick"
          @pane-click="onPaneClick"
        >
          <Background />
          <Controls />
          <MiniMap />
        </VueFlow>
      </div>

      <!-- 右侧配置面板 -->
      <NodeConfigPanel
        :selectedNode="selectedNode"
        @update:nodeData="handleNodeDataUpdate"
      />
    </div>

    <!-- 调试对话框 -->
    <el-dialog v-model="debugDialogVisible" title="调试工作流" width="600px">
      <el-form label-position="top">
        <el-form-item label="模拟触发数据 (JSON)">
          <el-input v-model="debugTriggerData" type="textarea" :rows="6"
                    placeholder='{"title":"测试文章","category":"公告","articleId":1}' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="debugDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="executeDebug" :loading="debugging">执行调试</el-button>
      </template>
    </el-dialog>

    <!-- 调试结果对话框 -->
    <el-dialog v-model="debugResultVisible" title="调试结果" width="60%">
      <el-timeline>
        <el-timeline-item v-for="log in debugLogs" :key="log.id"
          :timestamp="log.executeTime"
          :type="log.status === 'SUCCESS' ? 'success' : log.status === 'FAILED' ? 'danger' : 'info'">
          <div class="flex items-center gap-2">
            <el-tag size="small">{{ log.nodeType }}</el-tag>
            <span class="font-semibold">{{ log.nodeLabel || log.nodeId }}</span>
            <el-tag :type="log.status === 'SUCCESS' ? 'success' : 'danger'" size="small">{{ log.status }}</el-tag>
            <span v-if="log.durationMs" class="text-gray-400 text-xs">{{ log.durationMs }}ms</span>
          </div>
          <div v-if="log.outputData" class="mt-1 text-sm text-gray-500">{{ log.outputData }}</div>
          <div v-if="log.errorMessage" class="mt-1 text-sm text-red-500">{{ log.errorMessage }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VueFlow, useVueFlow } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { Controls } from '@vue-flow/controls'
import { MiniMap } from '@vue-flow/minimap'
import { ArrowLeft } from '@element-plus/icons-vue'
import NodePalette from '@/components/workflow/NodePalette.vue'
import NodeConfigPanel from '@/components/workflow/panels/NodeConfigPanel.vue'
import TriggerNode from '@/components/workflow/nodes/TriggerNode.vue'
import ConditionNode from '@/components/workflow/nodes/ConditionNode.vue'
import DelayNode from '@/components/workflow/nodes/DelayNode.vue'
import ActionEmailNode from '@/components/workflow/nodes/ActionEmailNode.vue'
import ActionWebhookNode from '@/components/workflow/nodes/ActionWebhookNode.vue'
import ActionNotificationNode from '@/components/workflow/nodes/ActionNotificationNode.vue'
import { createWorkflow, updateWorkflow, getWorkflowDetail, debugWorkflow } from '@/api/admin/workflow'
import { showMessage } from '@/composables/util'

const route = useRoute()
const router = useRouter()
const { project } = useVueFlow()

const workflowId = ref(route.params.id ? Number(route.params.id) : null)
const workflowName = ref('')
const triggerType = ref('ARTICLE_PUBLISHED')
const nodes = ref([])
const edges = ref([])
const selectedNode = ref(null)
const saving = ref(false)
const flowContainer = ref(null)

const debugDialogVisible = ref(false)
const debugTriggerData = ref('')
const debugging = ref(false)
const debugResultVisible = ref(false)
const debugLogs = ref([])

const nodeTypes = {
  trigger: markRaw(TriggerNode),
  condition: markRaw(ConditionNode),
  delay: markRaw(DelayNode),
  action_email: markRaw(ActionEmailNode),
  action_webhook: markRaw(ActionWebhookNode),
  action_notification: markRaw(ActionNotificationNode),
}

let nodeIdCounter = 0
function generateNodeId(type) {
  return `${type}-${Date.now()}-${++nodeIdCounter}`
}

function onDrop(event) {
  const type = event.dataTransfer.getData('application/vueflow')
  if (!type) return

  const bounds = flowContainer.value.getBoundingClientRect()
  const position = project({
    x: event.clientX - bounds.left,
    y: event.clientY - bounds.top
  })

  const newNode = {
    id: generateNodeId(type),
    type,
    position,
    data: getDefaultData(type),
  }
  nodes.value = [...nodes.value, newNode]
}

function getDefaultData(type) {
  switch (type) {
    case 'trigger': return { triggerType: triggerType.value }
    case 'condition': return { field: '', operator: 'EQUALS', value: '' }
    case 'delay': return { delayMinutes: 10 }
    case 'action_email': return { toEmail: '', subject: '', body: '' }
    case 'action_webhook': return { url: '', body: '' }
    case 'action_notification': return { receiverId: '', title: '', content: '' }
    default: return {}
  }
}

function onNodeClick({ node }) {
  selectedNode.value = node
}

function onPaneClick() {
  selectedNode.value = null
}

function handleNodeDataUpdate(newData) {
  if (!selectedNode.value) return
  const node = nodes.value.find(n => n.id === selectedNode.value.id)
  if (node) {
    node.data = { ...newData }
    nodes.value = [...nodes.value]
  }
}

async function handleSave() {
  if (!workflowName.value.trim()) {
    showMessage('请输入工作流名称', 'warning')
    return
  }
  if (!triggerType.value) {
    showMessage('请选择触发类型', 'warning')
    return
  }

  saving.value = true
  const definitionJson = JSON.stringify({ nodes: nodes.value, edges: edges.value })

  try {
    if (workflowId.value) {
      await updateWorkflow({
        id: workflowId.value,
        name: workflowName.value,
        description: '',
        triggerType: triggerType.value,
        definitionJson
      })
    } else {
      await createWorkflow({
        name: workflowName.value,
        description: '',
        triggerType: triggerType.value,
        definitionJson
      })
    }
    showMessage('保存成功')
    router.push('/admin/workflow/list')
  } catch (e) {
    showMessage('保存失败', 'error')
  } finally {
    saving.value = false
  }
}

function handleDebug() {
  if (!workflowId.value) {
    showMessage('请先保存工作流后再进行调试', 'warning')
    return
  }
  debugDialogVisible.value = true
}

async function executeDebug() {
  if (!debugTriggerData.value.trim()) {
    showMessage('请输入模拟触发数据', 'warning')
    return
  }
  debugging.value = true
  try {
    const res = await debugWorkflow({
      id: workflowId.value,
      triggerData: debugTriggerData.value
    })
    debugLogs.value = res.data
    debugDialogVisible.value = false
    debugResultVisible.value = true
  } catch (e) {
    showMessage('调试执行失败', 'error')
  } finally {
    debugging.value = false
  }
}

onMounted(async () => {
  if (workflowId.value) {
    try {
      const res = await getWorkflowDetail(workflowId.value)
      const data = res.data
      workflowName.value = data.name
      triggerType.value = data.triggerType
      const definition = JSON.parse(data.definitionJson)
      nodes.value = definition.nodes || []
      edges.value = definition.edges || []
    } catch (e) {
      showMessage('加载工作流失败', 'error')
    }
  }
})
</script>

<style>
@import '@vue-flow/core/dist/style.css';
@import '@vue-flow/core/dist/theme-default.css';
@import '@vue-flow/controls/dist/style.css';
@import '@vue-flow/minimap/dist/style.css';
</style>
