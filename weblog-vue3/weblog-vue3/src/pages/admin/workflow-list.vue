<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center">
                <el-text>工作流名称</el-text>
                <div class="ml-3 w-52 mr-5">
                    <el-input v-model="searchName" placeholder="请输入名称搜索" />
                </div>
                <el-text>触发类型</el-text>
                <div class="ml-3 w-40 mr-5">
                    <el-select v-model="searchTriggerType" placeholder="全部" clearable>
                        <el-option label="文章发布" value="ARTICLE_PUBLISHED" />
                        <el-option label="收到评论" value="COMMENT_RECEIVED" />
                        <el-option label="新用户注册" value="USER_REGISTERED" />
                    </el-select>
                </div>
                <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                <el-button :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <div class="mb-5">
                <el-button type="primary" @click="$router.push('/admin/workflow/editor')">
                    <el-icon class="mr-1"><Plus /></el-icon>
                    新建工作流
                </el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="name" label="名称" min-width="160" />
                <el-table-column prop="triggerType" label="触发类型" width="140">
                    <template #default="scope">
                        <el-tag :type="triggerTagType(scope.row.triggerType)">
                            {{ triggerLabel(scope.row.triggerType) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
                <el-table-column label="状态" width="100">
                    <template #default="scope">
                        <el-switch v-model="scope.row.isEnabled" @change="handleToggle(scope.row)" />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" />
                <el-table-column label="操作" width="240" fixed="right">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="$router.push('/admin/workflow/editor/' + scope.row.id)">
                            编辑
                        </el-button>
                        <el-button type="info" size="small" @click="viewExecutions(scope.row)">
                            日志
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDelete(scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size"
                    :page-sizes="[10, 20, 50]" :background="true"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
            </div>
        </el-card>

        <!-- 执行日志抽屉 -->
        <el-drawer v-model="executionDrawerVisible" title="执行记录" size="60%">
            <el-table :data="executionData" border stripe v-loading="executionLoading">
                <el-table-column prop="startTime" label="触发时间" width="170" />
                <el-table-column prop="status" label="状态" width="120">
                    <template #default="scope">
                        <el-tag :type="statusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="isDebug" label="调试" width="70">
                    <template #default="scope">
                        <el-tag v-if="scope.row.isDebug" type="warning" size="small">调试</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" width="170" />
                <el-table-column label="操作" width="100">
                    <template #default="scope">
                        <el-button size="small" @click="viewLogs(scope.row)">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-drawer>

        <!-- 节点日志对话框 -->
        <el-dialog v-model="logDialogVisible" title="节点执行日志" width="70%">
            <el-timeline>
                <el-timeline-item v-for="log in logData" :key="log.id"
                    :timestamp="log.executeTime"
                    :type="log.status === 'SUCCESS' ? 'success' : log.status === 'FAILED' ? 'danger' : 'info'">
                    <div class="flex items-center gap-2">
                        <el-tag size="small">{{ log.nodeType }}</el-tag>
                        <span class="font-semibold">{{ log.nodeLabel || log.nodeId }}</span>
                        <el-tag :type="log.status === 'SUCCESS' ? 'success' : 'danger'" size="small">
                            {{ log.status }}
                        </el-tag>
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
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { getWorkflowPageList, toggleWorkflow, deleteWorkflow, getExecutionPageList, getExecutionLogs } from '@/api/admin/workflow'
import { showMessage, showModel } from '@/composables/util'

const searchName = ref('')
const searchTriggerType = ref('')
const current = ref(1)
const size = ref(10)
const total = ref(0)
const tableData = ref([])
const tableLoading = ref(false)

const executionDrawerVisible = ref(false)
const executionData = ref([])
const executionLoading = ref(false)
const currentWorkflowId = ref(null)

const logDialogVisible = ref(false)
const logData = ref([])

const triggerLabel = (type) => {
    const map = { ARTICLE_PUBLISHED: '文章发布', COMMENT_RECEIVED: '收到评论', USER_REGISTERED: '新用户注册' }
    return map[type] || type
}

const triggerTagType = (type) => {
    const map = { ARTICLE_PUBLISHED: 'success', COMMENT_RECEIVED: 'warning', USER_REGISTERED: '' }
    return map[type] || 'info'
}

const statusTagType = (status) => {
    const map = { COMPLETED: 'success', RUNNING: '', WAITING_DELAY: 'warning', FAILED: 'danger' }
    return map[status] || 'info'
}

function getTableData() {
    tableLoading.value = true
    getWorkflowPageList({
        current: current.value,
        size: size.value,
        name: searchName.value || undefined,
        triggerType: searchTriggerType.value || undefined
    }).then(res => {
        tableData.value = res.data
        total.value = res.total
    }).finally(() => {
        tableLoading.value = false
    })
}

function reset() {
    searchName.value = ''
    searchTriggerType.value = ''
    current.value = 1
    getTableData()
}

function handleSizeChange(val) {
    size.value = val
    current.value = 1
    getTableData()
}

function handleToggle(row) {
    toggleWorkflow(row.id).then(() => {
        showMessage(row.isEnabled ? '已启用' : '已禁用')
    }).catch(() => {
        row.isEnabled = !row.isEnabled
    })
}

function handleDelete(row) {
    showModel('确定要删除该工作流吗？').then(() => {
        deleteWorkflow(row.id).then(() => {
            showMessage('删除成功')
            getTableData()
        })
    })
}

function viewExecutions(row) {
    currentWorkflowId.value = row.id
    executionDrawerVisible.value = true
    executionLoading.value = true
    getExecutionPageList({ workflowId: row.id, current: 1, size: 20 }).then(res => {
        executionData.value = res.data
    }).finally(() => {
        executionLoading.value = false
    })
}

function viewLogs(execution) {
    getExecutionLogs(execution.id).then(res => {
        logData.value = res.data
        logDialogVisible.value = true
    })
}

onMounted(() => {
    getTableData()
})
</script>
