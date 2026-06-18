<template>
    <div>
        <!-- 表头分页查询条件， shadow="never" 指定 card 卡片组件没有阴影 -->
        <el-card shadow="never" class="mb-5">
            <!-- flex 布局，内容垂直居中 -->
            <div class="flex items-center">
                <el-text>{{ t('tag.name') }}</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchTagName" :placeholder="t('search.placeholder')" /></div>

                <el-text>{{ t('common.createDate') }}</el-text>
                <div class="ml-3 w-30 mr-5">
                    <!-- 日期选择组件（区间选择） -->
                    <el-date-picker v-model="pickDate" type="daterange" :range-separator="t('datepicker.rangeSeparator')" :start-placeholder="t('datepicker.startPlaceholder')"
                        :end-placeholder="t('datepicker.endPlaceholder')" size="default" :shortcuts="shortcuts" @change="datepickerChange" />
                </div>

                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">{{ t('common.query') }}</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">{{ t('common.reset') }}</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- 新增按钮 -->
            <div class="mb-5">
                <el-button v-if="userStore.hasPermission('tag:create')" type="primary" @click="addCategoryBtnClick">
                    <el-icon class="mr-1">
                        <Plus />
                    </el-icon>
                    {{ t('common.add') }}</el-button>
            </div>

            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="name" :label="t('tag.name')" width="180">
                    <template #default="scope">
                        <el-tag class="ml-2" type="success">{{ scope.row.name }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :label="t('common.createTime')" width="180" />
                <el-table-column :label="t('common.actions')">
                    <template #default="scope">
                        <el-button v-if="userStore.hasPermission('tag:delete')" type="danger" size="small" @click="deleteTagSubmit(scope.row)">
                            <el-icon class="mr-1">
                                <Delete />
                            </el-icon>
                            {{ t('common.delete') }}
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                    :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

        <!-- 添加标签 -->
        <FormDialog ref="formDialogRef" :title="t('tag.addTag')" destroyOnClose @submit="onSubmit">
            <el-form ref="formRef" :model="form">
                <el-form-item prop="name">
                    <el-tag v-for="tag in dynamicTags" :key="tag" class="mx-1" closable :disable-transitions="false"
                        @close="handleClose(tag)">
                        {{ tag }}
                    </el-tag>
                    <span class="w-20">
                        <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" class="ml-1 w-20" size="small"
                        @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
                    <el-button v-else class="button-new-tag ml-1" size="small" @click="showInput">
                        {{ t('tag.addNewTag') }}
                    </el-button>
                    </span>
                </el-form-item>
            </el-form>
        </FormDialog>

    </div>
</template>

<script setup>
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { ref, reactive, nextTick, computed } from 'vue'
import { getTagPageList, addTag, deleteTag } from '@/api/admin/tag'
import moment from 'moment'
import { showMessage, showModel } from '@/composables/util'
import FormDialog from '@/components/FormDialog.vue'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const userStore = useUserStore()

// 分页查询的标签名称
const searchTagName = ref('')
// 日期
const pickDate = ref('')

// 查询条件：开始结束时间
const startDate = reactive({})
const endDate = reactive({})

// 监听日期组件改变事件，并将开始结束时间设置到变量中
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')

    console.log('开始时间：' + startDate.value + ', 结束时间：' + endDate.value)
}

const shortcuts = computed(() => [
    {
        text: t('datepicker.lastWeek'),
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: t('datepicker.lastMonth'),
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: t('datepicker.lastThreeMonths'),
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
])

// 表格加载 Loading
const tableLoading = ref(false)
// 表格数据
const tableData = ref([])
// 当前页码，给了一个默认值 1
const current = ref(1)
// 总数据量，给了个默认值 0
const total = ref(0)
// 每页显示的数据量，给了个默认值 10
const size = ref(10)


// 获取分页数据
function getTableData() {
    // 显示表格 loading
    tableLoading.value = true
    // 调用后台分页接口，并传入所需参数

    getTagPageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, name: searchTagName.value })
        .then((res) => {
            if (res.success == true) {

                tableData.value = res.data
                current.value = res.current
                size.value = res.size
                total.value = res.total
            }
        })
        .finally(() => tableLoading.value = false) // 隐藏表格 loading
}
getTableData()

// 每页展示数量变更事件
const handleSizeChange = (chooseSize) => {
    console.log('选择的页码' + chooseSize)
    size.value = chooseSize
    getTableData()
}

// 重置查询条件
const reset = () => {
    searchTagName.value = ''
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
}

// 对话框是否显示
const formDialogRef = ref(null)

// 新增分类按钮点击事件
const addCategoryBtnClick = () => {
    formDialogRef.value.open()
}


// 表单引用
const formRef = ref(null)

// 添加文章分类表单对象
const form = reactive({
    tags: []
})


const onSubmit = () => {
    // 先验证 form 表单字段
    formRef.value.validate((valid) => {
        // 显示提交按钮 loading
        formDialogRef.value.showBtnLoading()
        form.tags = dynamicTags.value
        addTag(form).then((res) => {
            if (res.success == true) {
                showMessage(t('common.addSuccess'))
                // 将表单中标签数组置空
                form.tags = []
                dynamicTags.value = []
                // 隐藏对话框
                formDialogRef.value.close()
                // 重新请求分页接口，渲染数据
                getTableData()
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading()) // 隐藏提交按钮 loading
    })
}

// 删除标签
const deleteTagSubmit = (row) => {
    console.log(row)
    showModel(t('confirm.deleteTag')).then(() => {
        deleteTag(row.id).then((res) => {
            if (res.success == true) {
                showMessage(t('common.deleteSuccess'))
                // 重新请求分页接口，渲染数据
                getTableData()
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        })
    }).catch(() => {
        console.log('取消了')
    })
}

// 标签输入框值
const inputValue = ref('')
// 已输入的标签数组
const dynamicTags = ref([])
// 标签输入框是否显示
const inputVisible = ref(false)
// 标签输入框的引用
const InputRef = ref('')

const handleClose = (tag) => {
  dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value.input.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    dynamicTags.value.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

</script>