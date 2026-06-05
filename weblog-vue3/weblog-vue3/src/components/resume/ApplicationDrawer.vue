<template>
    <el-drawer v-model="drawerVisible" title="投递记录" direction="rtl" size="55%" @close="emit('close')">
        <div class="p-4">
            <div class="mb-4 flex items-center justify-between">
                <div class="flex items-center gap-3">
                    <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="getTableData" style="width: 130px" size="default">
                        <el-option label="已投递" :value="0" />
                        <el-option label="面试中" :value="1" />
                        <el-option label="已录用" :value="2" />
                        <el-option label="已拒绝" :value="3" />
                        <el-option label="已放弃" :value="4" />
                    </el-select>
                    <el-button @click="resetFilter">重置</el-button>
                </div>
                <el-button type="primary" @click="openFormDialog('add')">新增记录</el-button>
            </div>

            <el-table :data="tableData" border stripe v-loading="tableLoading" style="width: 100%">
                <el-table-column prop="company" label="公司" min-width="120" />
                <el-table-column prop="applyTime" label="投递时间" width="160" />
                <el-table-column prop="channel" label="渠道" width="100" />
                <el-table-column label="状态" width="90">
                    <template #default="scope">
                        <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
                <el-table-column label="操作" width="130" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="primary" @click="openFormDialog('edit', scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mt-4 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size"
                    :page-sizes="[10, 20, 50]" :background="true"
                    layout="total, sizes, prev, pager, next"
                    :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
            </div>
        </div>

        <FormDialog ref="formDialogRef" :title="formDialogTitle" destroyOnClose @submit="onFormSubmit">
            <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
                <el-form-item label="公司名称" prop="company">
                    <el-input v-model="form.company" placeholder="请输入公司名称" maxlength="100" show-word-limit clearable />
                </el-form-item>
                <el-form-item label="投递时间" prop="applyTime">
                    <el-date-picker v-model="form.applyTime" type="datetime" placeholder="选择投递时间" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" />
                </el-form-item>
                <el-form-item label="投递渠道" prop="channel">
                    <el-input v-model="form.channel" placeholder="如：BOSS直聘、猎聘、官网等" maxlength="60" clearable />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
                        <el-option label="已投递" :value="0" />
                        <el-option label="面试中" :value="1" />
                        <el-option label="已录用" :value="2" />
                        <el-option label="已拒绝" :value="3" />
                        <el-option label="已放弃" :value="4" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选备注信息" maxlength="500" show-word-limit />
                </el-form-item>
            </el-form>
        </FormDialog>
    </el-drawer>
</template>

<script setup>
import { ref, reactive } from 'vue'
import FormDialog from '@/components/FormDialog.vue'
import { getApplicationPageList, addApplication, updateApplication, deleteApplication } from '@/api/admin/resumeApplication'
import { showMessage, showModel } from '@/composables/util'

const emit = defineEmits(['close'])

const drawerVisible = ref(false)
const resumeId = ref(null)

const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const filterStatus = ref(null)

const statusMap = { 0: '已投递', 1: '面试中', 2: '已录用', 3: '已拒绝', 4: '已放弃' }
const statusTagTypeMap = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }

const statusLabel = (s) => statusMap[s] || '未知'
const statusTagType = (s) => statusTagTypeMap[s] || 'info'

function open(id) {
    resumeId.value = id
    tableData.value = []
    total.value = 0
    current.value = 1
    filterStatus.value = null
    drawerVisible.value = true
    getTableData()
}

function getTableData() {
    tableLoading.value = true
    const params = { current: current.value, size: size.value, resumeId: resumeId.value }
    if (filterStatus.value !== null && filterStatus.value !== '') {
        params.status = filterStatus.value
    }
    getApplicationPageList(params)
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

const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    getTableData()
}

const resetFilter = () => {
    filterStatus.value = null
    current.value = 1
    getTableData()
}

const formDialogRef = ref(null)
const formRef = ref(null)
const formDialogTitle = ref('新增投递记录')
const dialogType = ref('add')

const form = reactive({
    id: null,
    company: '',
    applyTime: '',
    channel: '',
    status: 0,
    remark: '',
})

const formRules = {
    company: [{ required: true, message: '公司名称不能为空', trigger: 'blur' }],
}

function openFormDialog(type, row) {
    dialogType.value = type
    if (type === 'add') {
        formDialogTitle.value = '新增投递记录'
        form.id = null
        form.company = ''
        form.applyTime = ''
        form.channel = ''
        form.status = 0
        form.remark = ''
    } else {
        formDialogTitle.value = '编辑投递记录'
        form.id = row.id
        form.company = row.company || ''
        form.applyTime = row.applyTime || ''
        form.channel = row.channel || ''
        form.status = row.status
        form.remark = row.remark || ''
    }
    formDialogRef.value.open()
}

const onFormSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) return false

        formDialogRef.value.showBtnLoading()

        const payload = { ...form, resumeId: resumeId.value }
        const api = dialogType.value === 'add' ? addApplication : updateApplication

        api(payload).then((res) => {
            if (res.success == true) {
                showMessage(dialogType.value === 'add' ? '添加成功' : '更新成功')
                formDialogRef.value.close()
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading())
    })
}

function handleDelete(row) {
    showModel('确定要删除该记录吗？').then(() => {
        deleteApplication({ id: row.id }).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        })
    }).catch(() => {})
}

defineExpose({ open })
</script>
