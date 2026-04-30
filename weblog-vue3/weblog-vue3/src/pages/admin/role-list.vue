<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="mb-5">
                <el-button type="primary" @click="addRoleBtnClick">
                    <el-icon class="mr-1"><Plus /></el-icon>
                    新增角色
                </el-button>
            </div>

            <el-table :data="roleList" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="name" label="角色名称" min-width="120" />
                <el-table-column prop="code" label="角色编码" min-width="150" />
                <el-table-column prop="description" label="描述" min-width="200" />
                <el-table-column prop="permissionCount" label="拥有权限" min-width="100">
                    <template #default="scope">
                        <el-tag type="primary">{{ scope.row.permissionCount }} 个</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" min-width="180" />
                <el-table-column label="操作" fixed="right" min-width="250">
                    <template #default="scope">
                        <el-button type="primary" link size="small" @click="editRole(scope.row)">编辑</el-button>
                        <el-button type="warning" link size="small" @click="assignPermissionsDialog(scope.row)">分配权限</el-button>
                        <el-button type="danger" link size="small" @click="deleteRole(scope.row)" :disabled="scope.row.code === 'ROLE_ADMIN'">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog 
            v-model="roleDialogVisible" 
            :title="dialogType === 'add' ? '新增角色' : '编辑角色'" 
            width="500px"
            :close-on-click-modal="false"
            @closed="resetRoleForm"
        >
            <el-form ref="roleFormRef" :rules="roleFormRules" :model="roleForm" label-width="80px">
                <el-form-item label="角色名称" prop="name">
                    <el-input v-model="roleForm.name" placeholder="请输入角色名称" clearable />
                </el-form-item>
                <el-form-item label="角色编码" prop="code" v-if="dialogType === 'add'">
                    <el-input v-model="roleForm.code" placeholder="请输入角色编码，如：ROLE_EDITOR" clearable />
                    <el-text type="info" size="small">角色编码以 ROLE_ 开头，如 ROLE_EDITOR</el-text>
                </el-form-item>
                <el-form-item label="角色编码" v-else>
                    <el-input v-model="roleForm.code" disabled />
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="roleForm.description" type="textarea" :rows="3" placeholder="请输入角色描述" clearable />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="roleDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="submitRoleForm">确定</el-button>
            </template>
        </el-dialog>

        <el-dialog 
            v-model="permissionDialogVisible" 
            title="分配权限" 
            width="600px"
            :close-on-click-modal="false"
        >
            <div class="mb-3">
                <el-text>当前角色：</el-text>
                <el-tag type="primary">{{ currentRoleName }}</el-tag>
            </div>
            <el-transfer
                v-model="selectedPermissions"
                :data="allPermissions"
                :titles="['可选权限', '已选权限']"
                :props="{
                    key: 'id',
                    label: 'name',
                    disabled: 'disabled'
                }"
                filterable
                filter-placeholder="搜索权限"
            >
                <template #default="{ option }">
                    <span>{{ option.name }}</span>
                    <span style="color: #8492a6; font-size: 12px; margin-left: 8px;">{{ option.code }}</span>
                </template>
            </el-transfer>
            <template #footer>
                <el-button @click="permissionDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="permissionLoading" @click="submitAssignPermissions">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { 
    getAllRoles, 
    getRoleById,
    createRole, 
    updateRole,
    deleteRole,
    assignPermissions,
    getAllPermissions 
} from '@/api/admin/userManage'
import { showMessage, showConfirm } from '@/composables/util'

const userStore = useUserStore()

const isAdmin = computed(() => {
    const roles = userStore.userInfo?.roles || []
    return roles.includes('ROLE_ADMIN')
})

const tableLoading = ref(false)
const roleList = ref([])

function getRoleList() {
    tableLoading.value = true
    getAllRoles().then((res) => {
        if (res.success == true) {
            roleList.value = res.data || []
        }
    }).finally(() => {
        tableLoading.value = false
    })
}

onMounted(() => {
    getRoleList()
})

const roleDialogVisible = ref(false)
const dialogType = ref('add')
const submitLoading = ref(false)
const roleFormRef = ref(null)

const roleForm = reactive({
    id: null,
    name: '',
    code: '',
    description: ''
})

const roleFormRules = {
    name: [
        { required: true, message: '角色名称不能为空', trigger: 'blur' }
    ],
    code: [
        { required: true, message: '角色编码不能为空', trigger: 'blur' }
    ]
}

const addRoleBtnClick = () => {
    dialogType.value = 'add'
    resetRoleForm()
    roleDialogVisible.value = true
}

const editRole = (row) => {
    dialogType.value = 'edit'
    roleForm.id = row.id
    roleForm.name = row.name
    roleForm.code = row.code
    roleForm.description = row.description
    roleDialogVisible.value = true
}

const resetRoleForm = () => {
    roleForm.id = null
    roleForm.name = ''
    roleForm.code = ''
    roleForm.description = ''
    if (roleFormRef.value) {
        roleFormRef.value.resetFields()
    }
}

const submitRoleForm = () => {
    roleFormRef.value.validate((valid) => {
        if (!valid) return false
        
        submitLoading.value = true
        
        const promise = dialogType.value === 'add' 
            ? createRole({
                name: roleForm.name,
                code: roleForm.code,
                description: roleForm.description
            })
            : updateRole(roleForm.id, {
                name: roleForm.name,
                description: roleForm.description
            })
        
        promise.then((res) => {
            if (res.success == true) {
                showMessage(dialogType.value === 'add' ? '添加成功' : '修改成功')
                roleDialogVisible.value = false
                getRoleList()
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => {
            submitLoading.value = false
        })
    })
}

const deleteRole = (row) => {
    showConfirm(`确定要删除角色「${row.name}」吗？`).then(() => {
        deleteRole(row.id).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                getRoleList()
            } else {
                showMessage(res.message, 'error')
            }
        })
    })
}

const permissionDialogVisible = ref(false)
const permissionLoading = ref(false)
const currentRoleId = ref(null)
const currentRoleName = ref('')
const selectedPermissions = ref([])
const allPermissions = ref([])

const assignPermissionsDialog = (row) => {
    currentRoleId.value = row.id
    currentRoleName.value = row.name
    selectedPermissions.value = []
    allPermissions.value = []
    
    getRoleById(row.id).then((res) => {
        if (res.success == true) {
            selectedPermissions.value = res.data.permissionIds || []
        }
    })
    
    getAllPermissions().then((res) => {
        if (res.success == true) {
            allPermissions.value = res.data || []
        }
    })
    
    permissionDialogVisible.value = true
}

const submitAssignPermissions = () => {
    permissionLoading.value = true
    assignPermissions({
        roleId: currentRoleId.value,
        permissionIds: selectedPermissions.value
    }).then((res) => {
        if (res.success == true) {
            showMessage('权限分配成功')
            permissionDialogVisible.value = false
            getRoleList()
        } else {
            showMessage(res.message, 'error')
        }
    }).finally(() => {
        permissionLoading.value = false
    })
}
</script>
