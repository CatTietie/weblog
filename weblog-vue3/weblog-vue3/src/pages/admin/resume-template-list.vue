<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center">
                <el-text>{{ t('template.name') }}</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchName" :placeholder="t('search.placeholder')" /></div>
                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">{{ t('common.query') }}</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">{{ t('common.reset') }}</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <div class="mb-5">
                <el-button type="primary" @click="openDialog('add')">
                    <el-icon class="mr-1"><Plus /></el-icon>
                    {{ t('common.add') }}
                </el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column :label="t('template.thumbnail')" width="100">
                    <template #default="scope">
                        <el-image v-if="scope.row.thumbnail" :src="scope.row.thumbnail" style="width: 60px; height: 40px" fit="cover" />
                        <span v-else class="text-gray-400 text-xs">{{ t('common.none') }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="name" :label="t('template.name')" width="150" />
                <el-table-column prop="componentName" :label="t('template.componentId')" width="130" />
                <el-table-column prop="description" :label="t('template.layout')" min-width="180" />
                <el-table-column :label="t('common.status')" width="100">
                    <template #default="scope">
                        <el-switch
                            v-model="scope.row.status"
                            :active-value="1"
                            :inactive-value="0"
                            @change="handleStatusChange(scope.row)"
                        />
                    </template>
                </el-table-column>
                <el-table-column prop="sortOrder" :label="t('template.sort')" width="80" />
                <el-table-column prop="createTime" :label="t('common.createTime')" width="180" />
                <el-table-column :label="t('common.actions')" width="150">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="openDialog('edit', scope.row)">{{ t('common.edit') }}</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(scope.row)">{{ t('common.delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                    :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper"
                    :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
            </div>
        </el-card>

        <FormDialog ref="formDialogRef" :title="dialogTitle" destroyOnClose @submit="onSubmit">
            <el-form ref="formRef" :rules="rules" :model="form" label-width="90px">
                <el-form-item :label="t('template.name')" prop="name">
                    <el-input v-model="form.name" :placeholder="t('template.namePlaceholder')" maxlength="50" show-word-limit clearable />
                </el-form-item>
                <el-form-item :label="t('template.componentId')" prop="componentName">
                    <el-input v-model="form.componentName" :placeholder="t('template.componentPlaceholder')" maxlength="30" clearable />
                </el-form-item>
                <el-form-item :label="t('template.thumbnailUrl')" prop="thumbnail">
                    <el-input v-model="form.thumbnail" :placeholder="t('template.thumbnailPlaceholder')" clearable />
                </el-form-item>
                <el-form-item :label="t('template.layout')" prop="description">
                    <el-input v-model="form.description" type="textarea" :rows="3" :placeholder="t('template.layoutPlaceholder')" maxlength="200" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('template.sort')" prop="sortOrder">
                    <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
                </el-form-item>
            </el-form>
        </FormDialog>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { getResumeTemplatePageList, addResumeTemplate, updateResumeTemplate, deleteResumeTemplate, updateResumeTemplateStatus } from '@/api/admin/resumeTemplate'
import { showMessage, showModel } from '@/composables/util'
import FormDialog from '@/components/FormDialog.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const searchName = ref('')
const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)

function getTableData() {
    tableLoading.value = true
    getResumeTemplatePageList({ current: current.value, size: size.value, name: searchName.value })
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
getTableData()

const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    getTableData()
}

const reset = () => {
    searchName.value = ''
    getTableData()
}

const formDialogRef = ref(null)
const formRef = ref(null)
const dialogTitle = ref(t('template.addTemplate'))
const dialogType = ref('add')

const form = reactive({
    id: null,
    name: '',
    componentName: '',
    thumbnail: '',
    description: '',
    sortOrder: 0,
})

const rules = {
    name: [{ required: true, message: t('validation.templateNameRequired'), trigger: 'blur' }],
    componentName: [{ required: true, message: t('validation.templateComponentRequired'), trigger: 'blur' }],
}

function openDialog(type, row) {
    dialogType.value = type
    if (type === 'add') {
        dialogTitle.value = t('template.addTemplate')
        form.id = null
        form.name = ''
        form.componentName = ''
        form.thumbnail = ''
        form.description = ''
        form.sortOrder = 0
    } else {
        dialogTitle.value = t('template.editTemplate')
        form.id = row.id
        form.name = row.name
        form.componentName = row.componentName
        form.thumbnail = row.thumbnail || ''
        form.description = row.description || ''
        form.sortOrder = row.sortOrder || 0
    }
    formDialogRef.value.open()
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) return false

        formDialogRef.value.showBtnLoading()
        const api = dialogType.value === 'add' ? addResumeTemplate : updateResumeTemplate

        api(form).then((res) => {
            if (res.success == true) {
                showMessage(dialogType.value === 'add' ? t('common.addSuccess') : t('message.updateSuccess'))
                formDialogRef.value.close()
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading())
    })
}

function handleStatusChange(row) {
    updateResumeTemplateStatus({ id: row.id, status: row.status }).then((res) => {
        if (res.success == true) {
            showMessage(t('message.statusUpdateSuccess'))
        } else {
            row.status = row.status === 1 ? 0 : 1
            showMessage(res.message, 'error')
        }
    }).catch(() => {
        row.status = row.status === 1 ? 0 : 1
    })
}

function handleDelete(row) {
    showModel(t('confirm.deleteTemplate')).then(() => {
        deleteResumeTemplate({ id: row.id }).then((res) => {
            if (res.success == true) {
                showMessage(t('common.deleteSuccess'))
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        })
    }).catch(() => {})
}
</script>