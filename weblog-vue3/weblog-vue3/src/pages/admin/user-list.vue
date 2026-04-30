<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center flex-wrap gap-3">
                <div class="flex items-center">
                    <el-text class="mr-2">用户名</el-text>
                    <el-input v-model="searchForm.username" placeholder="请输入（模糊查询）" style="width: 180px" clearable />
                </div>
                <div class="flex items-center">
                    <el-text class="mr-2">状态</el-text>
                    <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 150px" clearable>
                        <el-option label="全部" :value="null" />
                        <el-option label="启用" :value="0" />
                        <el-option label="禁用" :value="1" />
                    </el-select>
                </div>
                <div class="flex items-center">
                    <el-text class="mr-2">角色</el-text>
                    <el-select v-model="searchForm.roleId" placeholder="请选择角色" style="width: 180px" clearable>
                        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
                    </el-select>
                </div>
                <div class="flex items-center">
                    <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                    <el-button class="ml-2" :icon="RefreshRight" @click="resetSearch">重置</el-button>
                </div>
            </div>
        </el-card>

        <el-card shadow="never">
            <div class="mb-5">
                <el-button type="primary" @click="addUserBtnClick">
                    <el-icon class="mr-1"><Plus /></el-icon>
                    新增用户
                </el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="username" label="用户名" min-width="120" />
                <el-table-column prop="roleName" label="角色" min-width="100" />
                <el-table-column prop="statusName" label="状态" min-width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" effect="dark">
                            {{ scope.row.statusName }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" min-width="180" />
                <el-table-column label="操作" fixed="right" min-width="200">
                    <template #default="scope">
                        <el-button type="primary" link size="small" @click="editUser(scope.row)">编辑</el-button>
                        <el-button type="warning" link size="small" @click="resetPasswordDialog(scope.row)">重置密码</el-button>
                        <el-button type="danger" link size="small" @click="deleteUser(scope.row)">删除</el-button>
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

        <el-dialog 
            v-model="userDialogVisible" 
            :title="dialogType === 'add' ? '新增用户' : '编辑用户'" 
            width="500px"
            :close-on-click-modal="false"
            @closed="resetForm"
        >
            <el-form ref="userFormRef" :rules="userFormRules" :model="userForm" label-width="80px">
                <el-form-item label="用户名" prop="username" v-if="dialogType === 'add'">
                    <el-input v-model="userForm.username" placeholder="请输入用户名" maxlength="20" show-word-limit clearable />
                </el-form-item>
                <el-form-item label="用户名" v-else>
                    <el-input v-model="userForm.username" disabled />
                </el-form-item>
                <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
                    <el-input type="password" v-model="userForm.password" placeholder="请输入密码" show-password clearable />
                </el-form-item>
                <el-form-item label="角色" prop="roleId">
                    <el-select v-model="userForm.roleId" placeholder="请选择角色" style="width: 100%">
                        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="userForm.status">
                        <el-radio :label="0">启用</el-radio>
                        <el-radio :label="1">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="userDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="submitUserForm">确定</el-button>
            </template>
        </el-dialog>

        <el-dialog 
            v-model="resetPwdDialogVisible" 
            title="重置密码" 
            width="400px"
            :close-on-click-modal="false"
        >
            <el-form ref="resetPwdFormRef" :rules="resetPwdRules" :model="resetPwdForm" label-width="80px">
                <el-form-item label="用户名">
                    <el-input v-model="resetPwdForm.username" disabled />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input type="password" v-model="resetPwdForm.newPassword" placeholder="请输入新密码" show-password clearable />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input type="password" v-model="resetPwdForm.confirmPassword" placeholder="请确认新密码" show-password clearable />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="resetPwdDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="resetPwdLoading" @click="submitResetPassword">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { 
    getUserPageList, 
    createUser, 
    updateUser,
    deleteUser,
    resetPassword,
    getRoleSelectList 
} from '@/api/admin/userManage'
import { showMessage, showConfirm } from '@/composables/util'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const isAdmin = computed(() => {
    const roles = userStore.userInfo?.roles || []
    return roles.includes('ROLE_ADMIN')
})

const searchForm = reactive({
    username: '',
    status: null,
    roleId: null
})

const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)

const roleList = ref([])

function getTableData() {
    tableLoading.value = true
    
    const params = {
        current: current.value,
        size: size.value,
        username: searchForm.username || undefined,
        status: searchForm.status,
        roleId: searchForm.roleId
    }
    
    getUserPageList(params)
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

function getRoleList() {
    getRoleSelectList().then((res) => {
        if (res.success == true) {
            roleList.value = res.data || []
        }
    })
}

onMounted(() => {
    getTableData()
    getRoleList()
})

const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    getTableData()
}

const resetSearch = () => {
    searchForm.username = ''
    searchForm.status = null
    searchForm.roleId = null
}

const userDialogVisible = ref(false)
const dialogType = ref('add')
const submitLoading = ref(false)
const userFormRef = ref(null)

const userForm = reactive({
    id: null,
    username: '',
    password: '',
    roleId: null,
    status: 0
})

const validatePassword = (rule, value, callback) => {
    if (dialogType.value === 'add' && !value) {
        callback(new Error('密码不能为空'))
    } else if (dialogType.value === 'add' && value.length < 4) {
        callback(new Error('密码长度不能少于4位'))
    } else {
        callback()
    }
}

const userFormRules = {
    username: [
        { required: true, message: '用户名不能为空', trigger: 'blur' },
        { min: 1, max: 20, message: '用户名字数要求大于 1 个字符，小于 20 个字符', trigger: 'blur' },
    ],
    password: [
        { validator: validatePassword, trigger: 'blur' }
    ],
    roleId: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ]
}

const addUserBtnClick = () => {
    dialogType.value = 'add'
    resetForm()
    userDialogVisible.value = true
}

const editUser = (row) => {
    dialogType.value = 'edit'
    userForm.id = row.id
    userForm.username = row.username
    userForm.roleId = row.role ? roleList.value.find(r => r.code === row.role)?.id : null
    if (userForm.roleId === null && row.id) {
        userForm.roleId = row.roleId || null
    }
    userForm.status = row.status
    userDialogVisible.value = true
}

const resetForm = () => {
    userForm.id = null
    userForm.username = ''
    userForm.password = ''
    userForm.roleId = null
    userForm.status = 0
    if (userFormRef.value) {
        userFormRef.value.resetFields()
    }
}

const submitUserForm = () => {
    userFormRef.value.validate((valid) => {
        if (!valid) return false
        
        submitLoading.value = true
        
        const promise = dialogType.value === 'add' 
            ? createUser(userForm)
            : updateUser(userForm.id, { roleId: userForm.roleId, status: userForm.status })
        
        promise.then((res) => {
            if (res.success == true) {
                showMessage(dialogType.value === 'add' ? '添加成功' : '修改成功')
                userDialogVisible.value = false
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => {
            submitLoading.value = false
        })
    })
}

const deleteUser = (row) => {
    showConfirm(`确定要删除用户「${row.username}」吗？`).then(() => {
        deleteUser(row.id).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        })
    })
}

const resetPwdDialogVisible = ref(false)
const resetPwdLoading = ref(false)
const resetPwdFormRef = ref(null)

const resetPwdForm = reactive({
    id: null,
    username: '',
    newPassword: '',
    confirmPassword: ''
})

const validateConfirmPwd = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请确认密码'))
    } else if (value !== resetPwdForm.newPassword) {
        callback(new Error('两次密码输入不一致'))
    } else {
        callback()
    }
}

const resetPwdRules = {
    newPassword: [
        { required: true, message: '新密码不能为空', trigger: 'blur' },
        { min: 4, message: '密码长度不能少于4位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPwd, trigger: 'blur' }
    ]
}

const resetPasswordDialog = (row) => {
    resetPwdForm.id = row.id
    resetPwdForm.username = row.username
    resetPwdForm.newPassword = ''
    resetPwdForm.confirmPassword = ''
    resetPwdDialogVisible.value = true
}

const submitResetPassword = () => {
    resetPwdFormRef.value.validate((valid) => {
        if (!valid) return false
        
        resetPwdLoading.value = true
        
        resetPassword({ id: resetPwdForm.id, newPassword: resetPwdForm.newPassword })
            .then((res) => {
                if (res.success == true) {
                    showMessage('密码重置成功')
                    resetPwdDialogVisible.value = false
                } else {
                    showMessage(res.message, 'error')
                }
            }).finally(() => {
                resetPwdLoading.value = false
            })
    })
}
</script>
