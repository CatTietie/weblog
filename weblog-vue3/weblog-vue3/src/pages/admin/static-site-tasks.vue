<template>
    <div class="p-5">
        <h2 class="text-xl font-bold mb-6">生成任务日志</h2>

        <el-table :data="taskList" border stripe class="w-full">
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="taskType" label="类型" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.taskType === 'FULL' ? 'primary' : 'warning'" size="small">
                        {{ row.taskType === 'FULL' ? '全量' : '增量' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="statusType(row.status)" size="small">
                        {{ statusLabel(row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="deployTarget" label="部署目标" width="110" />
            <el-table-column label="进度" width="120">
                <template #default="{ row }">
                    {{ row.generatedPages }} / {{ row.totalPages }}
                </template>
            </el-table-column>
            <el-table-column label="耗时" width="100">
                <template #default="{ row }">
                    {{ row.durationMs ? (row.durationMs / 1000).toFixed(1) + 's' : '-' }}
                </template>
            </el-table-column>
            <el-table-column prop="deployedUrl" label="访问 URL" min-width="200">
                <template #default="{ row }">
                    <a v-if="row.deployedUrl" :href="row.deployedUrl" target="_blank" class="text-blue-600 hover:underline">
                        {{ row.deployedUrl }}
                    </a>
                    <span v-else class="text-gray-400">-</span>
                </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="170" />
            <el-table-column label="错误信息" width="200">
                <template #default="{ row }">
                    <el-tooltip v-if="row.errorMessage" :content="row.errorMessage" placement="top">
                        <span class="text-red-500 truncate block max-w-[180px]">{{ row.errorMessage }}</span>
                    </el-tooltip>
                    <span v-else class="text-gray-400">-</span>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="mt-4 flex justify-end">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="total"
                layout="total, prev, pager, next"
                @current-change="loadTasks"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getGenTaskList } from '@/api/admin/staticsite'

const taskList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
    loadTasks()
})

async function loadTasks() {
    const res = await getGenTaskList({ current: currentPage.value, size: pageSize.value })
    if (res.data.success) {
        taskList.value = res.data.data || []
        total.value = res.data.total || 0
    }
}

function statusType(status) {
    const map = { SUCCESS: 'success', FAILED: 'danger', RUNNING: 'warning', PENDING: 'info' }
    return map[status] || 'info'
}

function statusLabel(status) {
    const map = { SUCCESS: '成功', FAILED: '失败', RUNNING: '执行中', PENDING: '等待中' }
    return map[status] || status
}
</script>
