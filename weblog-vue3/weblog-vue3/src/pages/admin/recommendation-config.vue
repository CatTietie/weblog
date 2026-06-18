<template>
    <div class="p-4">
        <el-tabs v-model="activeTab">
            <!-- 置顶文章 -->
            <el-tab-pane :label="t('recommendation.pinArticle')" name="pin">
                <div class="mb-4">
                    <el-button type="primary" @click="openAddDialog(1)">{{ t('recommendation.addConfig') }}</el-button>
                </div>
                <el-table :data="pinnedConfigs" border stripe>
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="articleId" :label="t('recommendation.articleId')" />
                    <el-table-column prop="isActive" :label="t('recommendation.status')" width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.isActive ? 'success' : 'info'">
                                {{ row.isActive ? t('recommendation.active') : t('recommendation.inactive') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('common.operate')" width="200">
                        <template #default="{ row }">
                            <el-button size="small" @click="toggleStatus(row)">
                                {{ row.isActive ? t('recommendation.inactive') : t('recommendation.active') }}
                            </el-button>
                            <el-button size="small" type="danger" @click="handleDelete(row)">{{ t('common.delete') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>

            <!-- 标签屏蔽 -->
            <el-tab-pane :label="t('recommendation.blockTag')" name="block">
                <div class="mb-4">
                    <el-button type="primary" @click="openAddDialog(2)">{{ t('recommendation.addConfig') }}</el-button>
                </div>
                <el-table :data="blockedConfigs" border stripe>
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="tagId" :label="t('recommendation.tagId')" />
                    <el-table-column prop="isActive" :label="t('recommendation.status')" width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.isActive ? 'success' : 'info'">
                                {{ row.isActive ? t('recommendation.active') : t('recommendation.inactive') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('common.operate')" width="200">
                        <template #default="{ row }">
                            <el-button size="small" @click="toggleStatus(row)">
                                {{ row.isActive ? t('recommendation.inactive') : t('recommendation.active') }}
                            </el-button>
                            <el-button size="small" type="danger" @click="handleDelete(row)">{{ t('common.delete') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>

            <!-- 权重调整 -->
            <el-tab-pane :label="t('recommendation.weightAdjust')" name="weight">
                <div class="mb-4">
                    <el-button type="primary" @click="openAddDialog(3)">{{ t('recommendation.addConfig') }}</el-button>
                </div>
                <el-table :data="weightConfigs" border stripe>
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="articleId" :label="t('recommendation.articleId')" />
                    <el-table-column prop="weightAdjustment" :label="t('recommendation.weight')" />
                    <el-table-column prop="isActive" :label="t('recommendation.status')" width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.isActive ? 'success' : 'info'">
                                {{ row.isActive ? t('recommendation.active') : t('recommendation.inactive') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('common.operate')" width="200">
                        <template #default="{ row }">
                            <el-button size="small" @click="toggleStatus(row)">
                                {{ row.isActive ? t('recommendation.inactive') : t('recommendation.active') }}
                            </el-button>
                            <el-button size="small" type="danger" @click="handleDelete(row)">{{ t('common.delete') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>

            <!-- 用户画像 -->
            <el-tab-pane :label="t('recommendation.userProfile')" name="profile">
                <el-table :data="profiles" border stripe>
                    <el-table-column prop="userId" label="User ID" width="100" />
                    <el-table-column prop="tagWeights" :label="t('recommendation.tagWeights')">
                        <template #default="{ row }">
                            <span class="text-xs">{{ row.tagWeights }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="lastCalculatedTime" :label="t('recommendation.lastCalculated')" width="180" />
                    <el-table-column :label="t('common.operate')" width="100">
                        <template #default="{ row }">
                            <el-button size="small" @click="openEditProfile(row)">{{ t('common.edit') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-4 flex justify-center">
                    <el-pagination
                        v-model:current-page="profilePage"
                        :page-size="10"
                        :total="profileTotal"
                        layout="prev, pager, next"
                        @current-change="loadProfiles"
                    />
                </div>
            </el-tab-pane>
        </el-tabs>

        <!-- 添加配置对话框 -->
        <el-dialog v-model="addDialogVisible" :title="t('recommendation.addConfig')" width="400px">
            <el-form :model="addForm" label-width="100px">
                <el-form-item :label="t('recommendation.articleId')" v-if="addForm.configType !== 2">
                    <el-input-number v-model="addForm.articleId" :min="1" />
                </el-form-item>
                <el-form-item :label="t('recommendation.tagId')" v-if="addForm.configType === 2">
                    <el-input-number v-model="addForm.tagId" :min="1" />
                </el-form-item>
                <el-form-item :label="t('recommendation.weight')" v-if="addForm.configType === 3">
                    <el-input-number v-model="addForm.weightAdjustment" :step="0.1" :precision="2" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="addDialogVisible = false">{{ t('common.cancel') }}</el-button>
                <el-button type="primary" @click="handleAdd">{{ t('common.confirm') }}</el-button>
            </template>
        </el-dialog>

        <!-- 编辑画像对话框 -->
        <el-dialog v-model="editProfileVisible" :title="t('recommendation.userProfile')" width="500px">
            <el-form :model="editProfileForm" label-width="100px">
                <el-form-item label="User ID">
                    <el-input :value="editProfileForm.userId" disabled />
                </el-form-item>
                <el-form-item :label="t('recommendation.tagWeights')">
                    <el-input v-model="editProfileForm.tagWeights" type="textarea" :rows="6" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editProfileVisible = false">{{ t('common.cancel') }}</el-button>
                <el-button type="primary" @click="handleUpdateProfile">{{ t('common.confirm') }}</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import {
    getRecommendConfigList,
    addRecommendConfig,
    updateRecommendConfig,
    deleteRecommendConfig,
    getProfileList,
    updateUserProfile
} from '@/api/admin/recommendation'

const { t } = useI18n()

const activeTab = ref('pin')
const configs = ref([])
const profiles = ref([])
const profilePage = ref(1)
const profileTotal = ref(0)

const pinnedConfigs = computed(() => configs.value.filter(c => c.configType === 1))
const blockedConfigs = computed(() => configs.value.filter(c => c.configType === 2))
const weightConfigs = computed(() => configs.value.filter(c => c.configType === 3))

const addDialogVisible = ref(false)
const addForm = ref({ configType: 1, articleId: null, tagId: null, weightAdjustment: 0 })

const editProfileVisible = ref(false)
const editProfileForm = ref({ userId: null, tagWeights: '' })

onMounted(() => {
    loadConfigs()
    loadProfiles(1)
})

function loadConfigs() {
    getRecommendConfigList().then(res => {
        if (res.success) {
            configs.value = res.data || []
        }
    })
}

function loadProfiles(page) {
    profilePage.value = page
    getProfileList(page, 10).then(res => {
        if (res.success && res.data) {
            profiles.value = res.data.records || []
            profileTotal.value = res.data.total || 0
        }
    })
}

function openAddDialog(configType) {
    addForm.value = { configType, articleId: null, tagId: null, weightAdjustment: 0 }
    addDialogVisible.value = true
}

function handleAdd() {
    addRecommendConfig(addForm.value).then(res => {
        if (res.success) {
            addDialogVisible.value = false
            loadConfigs()
            ElMessage.success(t('common.operateSuccess'))
        }
    })
}

function toggleStatus(row) {
    updateRecommendConfig({ id: row.id, isActive: !row.isActive }).then(res => {
        if (res.success) {
            loadConfigs()
        }
    })
}

function handleDelete(row) {
    ElMessageBox.confirm(t('common.confirmDelete'), t('common.tip'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
    }).then(() => {
        deleteRecommendConfig({ id: row.id }).then(res => {
            if (res.success) {
                loadConfigs()
                ElMessage.success(t('common.operateSuccess'))
            }
        })
    }).catch(() => {})
}

function openEditProfile(row) {
    editProfileForm.value = { userId: row.userId, tagWeights: row.tagWeights }
    editProfileVisible.value = true
}

function handleUpdateProfile() {
    updateUserProfile(editProfileForm.value).then(res => {
        if (res.success) {
            editProfileVisible.value = false
            loadProfiles(profilePage.value)
            ElMessage.success(t('common.operateSuccess'))
        }
    })
}
</script>
