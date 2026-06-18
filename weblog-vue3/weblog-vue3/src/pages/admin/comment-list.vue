<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center flex-wrap gap-3">
                <div class="flex items-center">
                    <el-text class="mr-2">{{ t('common.status') }}</el-text>
                    <el-select v-model="searchForm.status" :placeholder="t('comment.selectStatus')" style="width: 150px" clearable @clear="searchForm.status = null">
                        <el-option :label="t('comment.pending')" :value="0" />
                        <el-option :label="t('common.published')" :value="1" />
                    </el-select>
                </div>
                <div class="flex items-center">
                    <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('common.query') }}</el-button>
                    <el-button class="ml-2" :icon="RefreshRight" @click="resetSearch">{{ t('common.reset') }}</el-button>
                </div>
            </div>
        </el-card>

        <el-card shadow="never">
            <div class="mb-5">
                <el-button v-if="userStore.hasPermission('comment:update')" type="success" :disabled="selectedIds.length === 0" @click="batchApprove">
                    {{ t('comment.batchApprove') }}
                </el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50" />
                <el-table-column prop="content" :label="t('comment.content')" min-width="200" show-overflow-tooltip />
                <el-table-column prop="username" :label="t('comment.author')" min-width="100" />
                <el-table-column prop="articleTitle" :label="t('comment.belongArticle')" min-width="150" show-overflow-tooltip />
                <el-table-column prop="createTime" :label="t('comment.commentTime')" min-width="180" />
                <el-table-column prop="statusName" :label="t('common.status')" min-width="90">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" effect="dark">
                            {{ scope.row.statusName }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column :label="t('common.actions')" fixed="right" min-width="150">
                    <template #default="scope">
                        <el-button v-if="scope.row.status === 0 && userStore.hasPermission('comment:update')" type="success" link size="small" @click="approveComment(scope.row)">{{ t('comment.approve') }}</el-button>
                        <el-button v-if="userStore.hasPermission('comment:delete')" type="danger" link size="small" @click="handleDelete(scope.row)">{{ t('common.delete') }}</el-button>
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
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { ref, reactive, onMounted } from 'vue'
import { getCommentPageList, deleteComment, batchUpdateCommentStatus } from '@/api/admin/comment'
import { showMessage, showModel } from '@/composables/util'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const userStore = useUserStore()

const searchForm = reactive({
    status: null
})

const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)
const selectedIds = ref([])

function getTableData() {
    tableLoading.value = true

    const params = {
        current: current.value,
        size: size.value
    }

    if (searchForm.status !== null && searchForm.status !== undefined && searchForm.status !== '') {
        params.status = searchForm.status
    }

    getCommentPageList(params)
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

const handleSearch = () => {
    current.value = 1
    getTableData()
}

const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    current.value = 1
    getTableData()
}

const resetSearch = () => {
    searchForm.status = null
    current.value = 1
    getTableData()
}

const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map(item => item.id)
}

const approveComment = (row) => {
    batchUpdateCommentStatus({ ids: [row.id], status: 1 }).then((res) => {
        if (res.success == true) {
            showMessage(t('message.approveSuccess'))
            getTableData()
        } else {
            showMessage(res.message, 'error')
        }
    })
}

const batchApprove = () => {
    batchUpdateCommentStatus({ ids: selectedIds.value, status: 1 }).then((res) => {
        if (res.success == true) {
            showMessage(t('message.batchApproveSuccess'))
            getTableData()
        } else {
            showMessage(res.message, 'error')
        }
    })
}

const handleDelete = (row) => {
    showModel(t('confirm.deleteComment')).then(() => {
        deleteComment(row.id).then((res) => {
            if (res.success == true) {
                showMessage(t('common.deleteSuccess'))
                if (tableData.value.length === 1 && current.value > 1) {
                    current.value--
                }
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        })
    })
}
</script>