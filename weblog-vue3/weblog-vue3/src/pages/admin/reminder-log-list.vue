<template>
    <div>
        <el-card shadow="never">
            <template #header>
                <div class="flex items-center justify-between">
                    <span class="font-bold text-lg">过期投递提醒日志</span>
                    <el-button type="primary" :icon="Refresh" @click="getTableData">刷新</el-button>
                </div>
            </template>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="executeTime" label="执行时间" min-width="180" />
                <el-table-column prop="scannedCount" label="扫描过期投递数" min-width="130" align="center">
                    <template #default="scope">
                        <el-tag type="info">{{ scope.row.scannedCount }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="userCount" label="涉及用户数" min-width="110" align="center">
                    <template #default="scope">
                        <el-tag>{{ scope.row.userCount }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="sentCount" label="成功发送" min-width="100" align="center">
                    <template #default="scope">
                        <el-tag type="success">{{ scope.row.sentCount }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="failedCount" label="发送失败" min-width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.failedCount > 0 ? 'danger' : 'info'">{{ scope.row.failedCount }}</el-tag>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mt-10 flex justify-center">
                <el-pagination
                    v-model:current-page="current"
                    v-model:page-size="size"
                    :page-sizes="[10, 20, 50]"
                    :small="false"
                    :background="true"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="getTableData"
                />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { Refresh } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { getReminderLogPageList } from '@/api/admin/reminderLog'

const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)

function getTableData() {
    tableLoading.value = true

    getReminderLogPageList({
        current: current.value,
        size: size.value
    })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                current.value = res.current
                size.value = res.size
                total.value = res.total
            }
        })
        .finally(() => tableLoading.value = false)
}

onMounted(() => {
    getTableData()
})

const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    current.value = 1
    getTableData()
}
</script>
