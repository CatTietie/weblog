<template>
    <div>
        <!-- 表头分页查询条件 -->
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center">
                <el-text>用户名</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchUsername" placeholder="请输入（模糊查询）" /></div>

                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">查询</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- 新增按钮 -->
            <div class="mb-5">
                <el-button type="primary" @click="addUserBtnClick">
                    <el-icon class="mr-1">
                        <Plus />
                    </el-icon>
                    新增用户</el-button>
            </div>

            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="username" label="用户名" width="180" />
                <el-table-column prop="roleName" label="角色" width="120" />
                <el-table-column prop="statusName" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
                            {{ scope.row.statusName }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
            </el-table>

            <!-- 分页 -->
            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper"
                :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

    <!-- 添加用户 -->
    <FormDialog ref="formDialogRef" title="添加用户" destroyOnClose @submit="onSubmit">
        <el-form ref="formRef" :rules="rules" :model="form" label-width="80px">
            <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" placeholder="请输入用户名" maxlength="20" show-word-limit clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input type="password" v-model="form.password" placeholder="请输入密码" show-password clearable />
            </el-form-item>
            <el-form-item label="角色" prop="role">
                <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                    <el-option label="管理员" value="ROLE_ADMIN" />
                    <el-option label="普通用户" value="ROLE_USER" />
                </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio :value="0">启用</el-radio>
                    <el-radio :value="1">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
    </FormDialog>

    </div>
</template>

<script setup>
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import { ref, reactive, computed } from 'vue'
import { getUserPageList, createUser } from '@/api/admin/userManage'
import { showMessage } from '@/composables/util'
import FormDialog from '@/components/FormDialog.vue'

// 搜索条件
const searchUsername = ref('')

// 表格加载 Loading
const tableLoading = ref(false)
// 表格数据
const tableData = ref([])
// 当前页码
const current = ref(1)
// 总数据量
const total = ref(0)
// 每页显示的数据量
const size = ref(10)

// 获取分页数据
function getTableData() {
    tableLoading.value = true
    
    getUserPageList({current: current.value, size: size.value, username: searchUsername.value})
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

// 每页展示数量变更事件
const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    getTableData()
}

// 重置查询条件
const reset = () => {
    searchUsername.value = ''
}

// 对话框是否显示
const formDialogRef = ref(null)

// 新增用户按钮点击事件
const addUserBtnClick = () => {
    formDialogRef.value.open()
}

// 表单引用
const formRef = ref(null)

// 添加用户表单对象
const form = reactive({
    username: '',
    password: '',
    role: 'ROLE_USER',
    status: 0
})

// 规则校验
const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('密码不能为空'))
    } else if (value.length < 4) {
        callback(new Error('密码长度不能少于4位'))
    } else {
        callback()
    }
}

const rules = {
    username: [
        {
            required: true,
            message: '用户名不能为空',
            trigger: 'blur',
        },
        { min: 1, max: 20, message: '用户名字数要求大于 1 个字符，小于 20 个字符', trigger: 'blur' },
    ],
    password: [
        {
            required: true,
            validator: validatePassword,
            trigger: 'blur',
        }
    ],
    role: [
        {
            required: true,
            message: '请选择角色',
            trigger: 'change',
        }
    ],
    status: [
        {
            required: true,
            message: '请选择状态',
            trigger: 'change',
        }
    ]
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }
        
        formDialogRef.value.showBtnLoading()
        createUser(form).then((res) => {
            if (res.success == true) {
                showMessage('添加成功')
                form.username = ''
                form.password = ''
                form.role = 'ROLE_USER'
                form.status = 0
                formDialogRef.value.close()
                getTableData()
            } else {
                let message = res.message
                showMessage(message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading())
    })
}

</script>
