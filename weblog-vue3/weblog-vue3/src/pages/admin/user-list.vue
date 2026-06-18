<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center flex-wrap gap-3">
                <div class="flex items-center">
                    <el-text class="mr-2">{{ t('user.username') }}</el-text>
                    <el-input v-model="searchForm.username" :placeholder="t('search.placeholder')" style="width: 180px" clearable />
                </div>
                <div class="flex items-center">
                    <el-text class="mr-2">{{ t('common.status') }}</el-text>
                    <el-select v-model="searchForm.status" :placeholder="t('user.statusPlaceholder')" style="width: 150px" clearable>
                        <el-option :label="t('common.all')" :value="null" />
                        <el-option :label="t('common.enabled')" :value="0" />
                        <el-option :label="t('common.disabled')" :value="1" />
                    </el-select>
                </div>
                <div class="flex items-center">
                    <el-text class="mr-2">{{ t('user.role') }}</el-text>
                    <el-select v-model="searchForm.roleId" :placeholder="t('user.rolePlaceholder')" style="width: 180px" clearable>
                        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
                    </el-select>
                </div>
                <div class="flex items-center">
                    <el-button type="primary" :icon="Search" @click="getTableData">{{ t('common.query') }}</el-button>
                    <el-button class="ml-2" :icon="RefreshRight" @click="resetSearch">{{ t('common.reset') }}</el-button>
                </div>
            </div>
        </el-card>

        <el-card shadow="never">
            <div class="mb-5">
                <el-button v-if="userStore.hasPermission('user:create')" type="primary" @click="addUserBtnClick">
                    <el-icon class="mr-1"><Plus /></el-icon>
                    {{ t('user.addUser') }}
                </el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="username" :label="t('user.username')" min-width="120" />
                <el-table-column prop="roleName" :label="t('user.role')" min-width="100" />
                <el-table-column prop="statusName" :label="t('common.status')" min-width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" effect="dark">
                            {{ scope.row.statusName }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :label="t('common.createTime')" min-width="180" />
                <el-table-column :label="t('common.actions')" fixed="right" min-width="200">
                    <template #default="scope">
                        <el-button v-if="userStore.hasPermission('user:update')" type="primary" link size="small" @click="editUser(scope.row)">{{ t('common.edit') }}</el-button>
                        <el-button v-if="userStore.hasPermission('user:password')" type="warning" link size="small" @click="resetPasswordDialog(scope.row)">{{ t('user.resetPassword') }}</el-button>
                        <el-button v-if="userStore.hasPermission('user:delete')" type="danger" link size="small" @click="deleteUser(scope.row)">{{ t('common.delete') }}</el-button>
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
            :title="dialogType === 'add' ? t('user.addUser') : t('user.editUser')"
            width="500px"
            :close-on-click-modal="false"
            @closed="resetForm"
        >
            <el-form ref="userFormRef" :rules="userFormRules" :model="userForm" label-width="80px">
                <el-form-item :label="t('user.username')" prop="username" v-if="dialogType === 'add'">
                    <el-input v-model="userForm.username" :placeholder="t('user.usernamePlaceholder')" maxlength="20" show-word-limit clearable />
                </el-form-item>
                <el-form-item :label="t('user.username')" v-else>
                    <el-input v-model="userForm.username" disabled />
                </el-form-item>
                <el-form-item :label="t('user.password')" prop="password" v-if="dialogType === 'add'">
                    <el-input type="password" v-model="userForm.password" :placeholder="t('user.passwordPlaceholder')" show-password clearable />
                </el-form-item>
                <el-form-item :label="t('user.role')" prop="roleId">
                    <el-select v-model="userForm.roleId" :placeholder="t('user.rolePlaceholder')" style="width: 100%">
                        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('common.status')" prop="status">
                    <el-radio-group v-model="userForm.status">
                        <el-radio :label="0">{{ t('common.enabled') }}</el-radio>
                        <el-radio :label="1">{{ t('common.disabled') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="userDialogVisible = false">{{ t('common.cancel') }}</el-button>
                <el-button type="primary" :loading="submitLoading" @click="submitUserForm">{{ t('common.confirm') }}</el-button>
            </template>
        </el-dialog>

        <el-dialog
            v-model="resetPwdDialogVisible"
            :title="t('user.resetPassword')"
            width="400px"
            :close-on-click-modal="false"
        >
            <el-form ref="resetPwdFormRef" :rules="resetPwdRules" :model="resetPwdForm" label-width="80px">
                <el-form-item :label="t('user.username')">
                    <el-input v-model="resetPwdForm.username" disabled />
                </el-form-item>
                <el-form-item :label="t('user.newPassword')" prop="newPassword">
                    <el-input type="password" v-model="resetPwdForm.newPassword" :placeholder="t('user.newPasswordPlaceholder')" show-password clearable />
                </el-form-item>
                <el-form-item :label="t('user.confirmPasswordShort')" prop="confirmPassword">
                    <el-input type="password" v-model="resetPwdForm.confirmPassword" :placeholder="t('user.confirmPasswordPlaceholder')" show-password clearable />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="resetPwdDialogVisible = false">{{ t('common.cancel') }}</el-button>
                <el-button type="primary" :loading="resetPwdLoading" @click="submitResetPassword">{{ t('common.confirm') }}</el-button>
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
    deleteUser as deleteUserApi,
    resetPassword,
    getRoleSelectList
} from '@/api/admin/userManage'
import { showMessage, showModel } from '@/composables/util'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
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

    // 处理参数：空字符串、null、"null" 都不传
    const params = {
        current: current.value,
        size: size.value
    }

    // 只有当 username 有有效值时才添加
    if (searchForm.username && searchForm.username.trim() !== '' && searchForm.username !== 'null') {
        params.username = searchForm.username.trim()
    }

    // 只有当 status 不是 null 时才添加
    if (searchForm.status !== null && searchForm.status !== undefined) {
        params.status = searchForm.status
    }

    // 只有当 roleId 不是 null 时才添加
    if (searchForm.roleId !== null && searchForm.roleId !== undefined) {
        params.roleId = searchForm.roleId
    }

    console.log('用户列表查询参数:', params)

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
        callback(new Error(t('validation.passwordRequired')))
    } else if (dialogType.value === 'add' && value.length < 4) {
        callback(new Error(t('validation.passwordMinLength')))
    } else {
        callback()
    }
}

const userFormRules = {
    username: [
        { required: true, message: t('validation.usernameRequired'), trigger: 'blur' },
        { min: 1, max: 20, message: t('validation.usernameLength'), trigger: 'blur' },
    ],
    password: [
        { validator: validatePassword, trigger: 'blur' }
    ],
    roleId: [
        { required: true, message: t('validation.selectRole'), trigger: 'change' }
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
                showMessage(dialogType.value === 'add' ? t('common.addSuccess') : t('common.editSuccess'))
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
    showModel(t('confirm.deleteUser', { username: row.username })).then(() => {
        deleteUserApi(row.id).then((res) => {
            if (res.success == true) {
                showMessage(t('common.deleteSuccess'))
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
        callback(new Error(t('validation.confirmPassword')))
    } else if (value !== resetPwdForm.newPassword) {
        callback(new Error(t('validation.passwordMismatch')))
    } else {
        callback()
    }
}

const resetPwdRules = {
    newPassword: [
        { required: true, message: t('validation.newPasswordRequired'), trigger: 'blur' },
        { min: 4, message: t('validation.passwordMinLength'), trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: t('validation.confirmPassword'), trigger: 'blur' },
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
                    showMessage(t('message.passwordResetSuccessShort'))
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