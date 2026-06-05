<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="mb-5">
                <el-button type="primary" @click="addRoleBtnClick">
                    <el-icon class="mr-1"><Plus /></el-icon>
                    {{ t('role.addRole') }}
                </el-button>
            </div>

            <el-table :data="roleList" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="name" :label="t('role.name')" min-width="120" />
                <el-table-column prop="code" :label="t('role.code')" min-width="150" />
                <el-table-column prop="description" :label="t('role.description')" min-width="200" />
                <el-table-column prop="permissionCount" :label="t('role.permissions')" min-width="100">
                    <template #default="scope">
                        <el-tag type="primary">{{ scope.row.permissionCount }} {{ t('common.unit') }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :label="t('common.createTime')" min-width="180" />
                <el-table-column :label="t('common.actions')" fixed="right" min-width="250">
                    <template #default="scope">
                        <el-button type="primary" link size="small" @click="editRole(scope.row)">{{ t('common.edit') }}</el-button>
                        <el-button type="warning" link size="small" @click="assignPermissionsDialog(scope.row)">{{ t('role.assignPermission') }}</el-button>
                        <el-button type="danger" link size="small" @click="handleDeleteRole(scope.row)" :disabled="scope.row.code === 'ROLE_ADMIN'">{{ t('common.delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog
            v-model="roleDialogVisible"
            :title="dialogType === 'add' ? t('role.addRole') : t('role.editRole')"
            width="500px"
            :close-on-click-modal="false"
            @closed="resetRoleForm"
        >
            <el-form ref="roleFormRef" :rules="roleFormRules" :model="roleForm" label-width="80px">
                <el-form-item :label="t('role.name')" prop="name">
                    <el-input v-model="roleForm.name" :placeholder="t('role.namePlaceholder')" clearable />
                </el-form-item>
                <el-form-item :label="t('role.code')" prop="code" v-if="dialogType === 'add'">
                    <el-input v-model="roleForm.code" :placeholder="t('role.codePlaceholder')" clearable />
                    <div class="text-xs text-gray-500 mt-1">{{ t('role.codeHelper') }}</div>
                </el-form-item>
                <el-form-item :label="t('role.code')" v-else>
                    <el-input v-model="roleForm.code" disabled />
                </el-form-item>
                <el-form-item :label="t('role.description')" prop="description">
                    <el-input v-model="roleForm.description" type="textarea" :rows="3" :placeholder="t('role.descPlaceholder')" clearable />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="roleDialogVisible = false">{{ t('common.cancel') }}</el-button>
                <el-button type="primary" :loading="submitLoading" @click="submitRoleForm">{{ t('common.confirm') }}</el-button>
            </template>
        </el-dialog>

        <el-dialog
            v-model="permissionDialogVisible"
            :title="t('role.assignPermission')"
            width="500px"
            :close-on-click-modal="false"
        >
            <div class="mb-3">
                <span>{{ t('role.currentRole') }}</span>
                <el-tag type="primary">{{ currentRoleName }}</el-tag>
            </div>
            <div style="max-height: 400px; overflow-y: auto; border: 1px solid #ebeef5; border-radius: 4px; padding: 10px;">
                <el-tree
                    ref="permissionTreeRef"
                    :data="permissionTree"
                    show-checkbox
                    node-key="id"
                    :default-checked-keys="selectedPermissions"
                    :props="{ label: 'name', children: 'children' }"
                    default-expand-all
                >
                    <template #default="{ node, data }">
                        <span>{{ data.name }}</span>
                        <span style="color: #8492a6; font-size: 12px; margin-left: 8px;">{{ data.code }}</span>
                    </template>
                </el-tree>
            </div>
            <template #footer>
                <el-button @click="permissionDialogVisible = false">{{ t('common.cancel') }}</el-button>
                <el-button type="primary" :loading="permissionLoading" @click="submitAssignPermissions">{{ t('common.confirm') }}</el-button>
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
    deleteRole as deleteRoleApi,
    assignPermissions,
    getAllPermissions
} from '@/api/admin/userManage'
import { showMessage, showModel } from '@/composables/util'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
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
        { required: true, message: t('validation.roleNameRequired'), trigger: 'blur' }
    ],
    code: [
        { required: true, message: t('validation.roleCodeRequired'), trigger: 'blur' }
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
                showMessage(dialogType.value === 'add' ? t('common.addSuccess') : t('common.editSuccess'))
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

const handleDeleteRole = (row) => {
    showModel(t('confirm.deleteRole', { name: row.name })).then(() => {
        deleteRoleApi(row.id).then((res) => {
            if (res.success == true) {
                showMessage(t('common.deleteSuccess'))
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
const permissionTree = ref([])
const permissionTreeRef = ref(null)

function buildPermissionTree(flatList) {
    const map = {}
    const tree = []
    flatList.forEach(item => {
        map[item.id] = { ...item, children: [] }
    })
    flatList.forEach(item => {
        if (item.parentId && item.parentId !== 0 && map[item.parentId]) {
            map[item.parentId].children.push(map[item.id])
        } else {
            tree.push(map[item.id])
        }
    })
    // 移除空 children 数组（叶子节点）
    const clean = (nodes) => {
        nodes.forEach(node => {
            if (node.children.length === 0) {
                delete node.children
            } else {
                clean(node.children)
            }
        })
    }
    clean(tree)
    return tree
}

// 从已选权限 ID 中筛选出叶子节点 ID（避免 el-tree 自动级联选中所有子节点）
function getLeafCheckedIds(allIds, treeData) {
    const parentIds = new Set()
    const collectParents = (nodes) => {
        nodes.forEach(node => {
            if (node.children && node.children.length > 0) {
                parentIds.add(node.id)
                collectParents(node.children)
            }
        })
    }
    collectParents(treeData)
    return allIds.filter(id => !parentIds.has(id))
}

const assignPermissionsDialog = (row) => {
    currentRoleId.value = row.id
    currentRoleName.value = row.name
    selectedPermissions.value = []
    permissionTree.value = []

    getAllPermissions().then((res) => {
        if (res.success == true) {
            allPermissions.value = res.data || []
            permissionTree.value = buildPermissionTree(allPermissions.value)

            // 加载当前角色已有权限
            getRoleById(row.id).then((roleRes) => {
                if (roleRes.success == true) {
                    const ids = roleRes.data.permissionIds || []
                    // 只设置叶子节点为 checked，父节点会自动半选
                    selectedPermissions.value = getLeafCheckedIds(ids, permissionTree.value)
                }
            })
        }
    })

    permissionDialogVisible.value = true
}

const submitAssignPermissions = () => {
    permissionLoading.value = true
    // 合并全选节点和半选节点（半选的父菜单也需要保存）
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]

    assignPermissions({
        roleId: currentRoleId.value,
        permissionIds: allKeys
    }).then((res) => {
        if (res.success == true) {
            showMessage(t('message.permissionAssignSuccess'))
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