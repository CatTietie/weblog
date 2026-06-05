<template>
    <Header></Header>

    <main class="container max-w-screen-xl mx-auto p-4">
        <div class="mb-6 flex items-center justify-between">
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">我的简历</h1>
            <el-button type="primary" @click="formDialogRef.open()">新建简历</el-button>
        </div>

        <!-- 投递仪表盘 -->
        <div v-if="statisticsData.totalCount > 0"
            class="mb-6 bg-white border border-gray-200 rounded-lg p-5 dark:bg-gray-800 dark:border-gray-700">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 dark:text-white">投递仪表盘</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <h3 class="text-sm text-gray-500 mb-2 dark:text-gray-400">近30日投递趋势</h3>
                    <ResumeApplicationLineChart :dates="statisticsData.dates"
                        :counts="statisticsData.dailyCounts" />
                </div>
                <div>
                    <h3 class="text-sm text-gray-500 mb-2 dark:text-gray-400">投递状态分布</h3>
                    <ResumeApplicationPieChart :data="statisticsData.statusDistribution" />
                </div>
            </div>
        </div>

        <!-- 简历卡片列表 -->
        <div v-if="resumeList.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for="item in resumeList" :key="item.id"
                class="bg-white border border-gray-200 rounded-lg p-5 hover:shadow-md transition-shadow dark:bg-gray-800 dark:border-gray-700">
                <div class="flex flex-col h-full">
                    <h3 class="text-lg font-semibold text-gray-900 mb-2 dark:text-white">{{ item.name }}</h3>
                    <p class="text-sm text-gray-500 mb-4 dark:text-gray-400">
                        更新时间：{{ item.updateTime }}
                    </p>
                    <div class="mt-auto flex gap-2">
                        <el-button size="small" type="primary" @click="goEdit(item.id)">编辑</el-button>
                        <el-button size="small" @click="openApplications(item.id)">投递记录</el-button>
                        <el-button size="small" @click="handleShare(item)">分享</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(item.id)">删除</el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="text-center py-20">
            <svg class="mx-auto w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-gray-500 text-lg mb-2">暂无简历</p>
            <p class="text-gray-400 text-sm">点击上方"新建简历"按钮创建你的第一份简历</p>
        </div>
    </main>

    <!-- 新建简历弹窗 -->
    <FormDialog ref="formDialogRef" title="新建简历" @submit="handleCreate" destroyOnClose>
        <el-form ref="createFormRef" :model="createForm" :rules="createRules">
            <el-form-item label="简历名称" prop="name" label-width="80px">
                <el-input v-model="createForm.name" placeholder="请输入简历名称" maxlength="30" show-word-limit clearable />
            </el-form-item>
        </el-form>
    </FormDialog>

    <!-- 分享设置弹窗 -->
    <el-dialog v-model="shareDialogVisible" title="分享简历" width="500px" :close-on-click-modal="false">
        <div class="mb-4 flex items-center">
            <span class="mr-3 text-gray-700">开启公开分享</span>
            <el-switch v-model="shareForm.enabled" @change="onShareToggle" :loading="shareLoading" />
        </div>
        <div v-if="shareForm.enabled && shareForm.shareUrl">
            <p class="text-sm text-gray-500 mb-2">任何人都可以通过以下链接查看此简历（无需登录）：</p>
            <el-input v-model="shareForm.shareUrl" readonly>
                <template #append>
                    <el-button @click="copyShareLink">复制链接</el-button>
                </template>
            </el-input>
        </div>
        <div v-if="!shareForm.enabled" class="text-sm text-gray-400">
            开启后将生成公开链接，他人可通过链接直接查看简历内容。关闭后链接立即失效。
        </div>
    </el-dialog>

    <ApplicationDrawer ref="applicationDrawerRef" @close="fetchStatistics" />

    <Footer></Footer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import FormDialog from '@/components/FormDialog.vue'
import ApplicationDrawer from '@/components/resume/ApplicationDrawer.vue'
import ResumeApplicationLineChart from '@/components/resume/ResumeApplicationLineChart.vue'
import ResumeApplicationPieChart from '@/components/resume/ResumeApplicationPieChart.vue'
import { getResumeList, createResume, deleteResume, toggleResumeShare, getResumeShareInfo } from '@/api/admin/resume'
import { getApplicationStatistics } from '@/api/admin/resumeApplication'
import { showMessage, showModel } from '@/composables/util'

const router = useRouter()

const resumeList = ref([])
const formDialogRef = ref(null)
const createFormRef = ref(null)
const applicationDrawerRef = ref(null)

function openApplications(id) {
    applicationDrawerRef.value.open(id)
}

const createForm = reactive({ name: '' })
const createRules = {
    name: [{ required: true, message: '简历名称不能为空', trigger: 'blur' }]
}

const fetchList = () => {
    getResumeList().then(res => {
        if (res.success == true) {
            resumeList.value = res.data || []
        } else {
            showMessage(res.message, 'error')
        }
    })
}

const statisticsData = ref({ totalCount: 0, dates: [], dailyCounts: [], statusDistribution: [] })

const fetchStatistics = () => {
    getApplicationStatistics().then(res => {
        if (res.success === true) {
            statisticsData.value = res.data || { totalCount: 0, dates: [], dailyCounts: [], statusDistribution: [] }
        }
    })
}

onMounted(() => {
    fetchList()
    fetchStatistics()
})

const goEdit = (resumeId) => {
    router.push({ path: '/resume/edit', query: { resumeId } })
}

const handleDelete = (resumeId) => {
    showModel('确定删除该简历？').then(() => {
        deleteResume({ resumeId }).then(res => {
            if (res.success == true) {
                showMessage('删除成功')
                fetchList()
            } else {
                showMessage(res.message, 'error')
            }
        })
    }).catch(() => {})
}

const handleCreate = () => {
    createFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        formDialogRef.value.showBtnLoading()
        createResume({ name: createForm.name }).then(res => {
            if (res.success == true) {
                showMessage('创建成功')
                createForm.name = ''
                formDialogRef.value.close()
                router.push({ path: '/resume/edit', query: { resumeId: res.data.id } })
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => {
            formDialogRef.value.closeBtnLoading()
        })
    })
}

// --- 分享功能 ---

const shareDialogVisible = ref(false)
const shareLoading = ref(false)
const shareForm = reactive({ resumeId: null, enabled: false, shareCode: '', shareUrl: '' })

function buildShareUrl(code) {
    return `${window.location.origin}${window.location.pathname}#/resume/s/${code}`
}

const handleShare = (item) => {
    shareForm.resumeId = item.id
    shareForm.enabled = false
    shareForm.shareCode = ''
    shareForm.shareUrl = ''

    getResumeShareInfo({ resumeId: item.id }).then(res => {
        if (res.success) {
            shareForm.enabled = res.data.shareEnabled
            shareForm.shareCode = res.data.shareCode || ''
            shareForm.shareUrl = shareForm.shareCode ? buildShareUrl(shareForm.shareCode) : ''
            shareDialogVisible.value = true
        } else {
            showMessage(res.message || '获取分享信息失败', 'error')
        }
    })
}

const onShareToggle = (val) => {
    shareLoading.value = true
    toggleResumeShare({ resumeId: shareForm.resumeId, enabled: val }).then(res => {
        if (res.success) {
            shareForm.shareCode = res.data.shareCode || ''
            shareForm.shareUrl = shareForm.shareCode ? buildShareUrl(shareForm.shareCode) : ''
            showMessage(val ? '已开启分享' : '已关闭分享')
        } else {
            shareForm.enabled = !val
            showMessage(res.message || '操作失败', 'error')
        }
    }).catch(() => {
        shareForm.enabled = !val
        showMessage('操作失败', 'error')
    }).finally(() => {
        shareLoading.value = false
    })
}

const copyShareLink = () => {
    navigator.clipboard.writeText(shareForm.shareUrl).then(() => {
        showMessage('链接已复制到剪贴板')
    }).catch(() => {
        showMessage('复制失败，请手动复制', 'warning')
    })
}
</script>
