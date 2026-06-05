<template>
    <div class="p-5">
        <!-- 页面标题 -->
        <h2 class="text-xl font-bold mb-6">静态站点配置</h2>

        <!-- 进度条 -->
        <div v-if="progress.status === 'RUNNING'" class="mb-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
            <div class="flex items-center justify-between mb-2">
                <span class="text-sm font-medium text-blue-700">正在生成...</span>
                <span class="text-sm text-blue-600">{{ progress.generatedPages }} / {{ progress.totalPages }}</span>
            </div>
            <el-progress :percentage="progressPercent" :stroke-width="8" />
            <div class="flex items-center justify-between mt-2 text-xs text-gray-500">
                <span v-if="estimatedRemaining">预计剩余时间: {{ estimatedRemaining }}</span>
                <span v-else>正在估算...</span>
                <span>已耗时: {{ elapsedTimeText }}</span>
            </div>
        </div>

        <!-- 任务超时提示 -->
        <div v-if="taskTimeout" class="mb-6 p-4 bg-yellow-50 rounded-lg border border-yellow-200">
            <div class="flex items-center gap-2">
                <el-icon class="text-yellow-600"><WarningFilled /></el-icon>
                <span class="text-sm text-yellow-700">任务可能已超时，长时间无进度更新。请刷新日志或重新触发生成。</span>
                <el-button type="warning" size="small" @click="cancelPollingAndReset">关闭</el-button>
            </div>
        </div>

        <el-form :model="form" label-width="160px" class="max-w-3xl">
            <!-- 通用设置 -->
            <el-divider content-position="left">通用设置</el-divider>
            <el-form-item label="站点基础 URL">
                <el-input v-model="form.outputBaseUrl" placeholder="/" />
            </el-form-item>
            <el-form-item label="发布时自动部署">
                <el-switch v-model="form.autoDeploy" />
            </el-form-item>

            <!-- 评论系统 -->
            <el-divider content-position="left">评论系统</el-divider>
            <el-form-item label="评论提供商">
                <el-select v-model="form.commentProvider" placeholder="选择评论系统">
                    <el-option label="无" value="none" />
                    <el-option label="Giscus" value="giscus" />
                    <el-option label="Waline" value="waline" />
                </el-select>
            </el-form-item>

            <!-- Giscus 配置 -->
            <template v-if="form.commentProvider === 'giscus'">
                <el-form-item label="仓库 (owner/repo)">
                    <el-input v-model="commentConfig.repo" placeholder="owner/repo" />
                </el-form-item>
                <el-form-item label="仓库 ID">
                    <el-input v-model="commentConfig.repoId" />
                </el-form-item>
                <el-form-item label="分类名">
                    <el-input v-model="commentConfig.category" />
                </el-form-item>
                <el-form-item label="分类 ID">
                    <el-input v-model="commentConfig.categoryId" />
                </el-form-item>
                <el-form-item label="主题跟随">
                    <el-select v-model="commentConfig.theme" placeholder="选择主题模式">
                        <el-option label="跟随博客主题 (自动)" value="auto" />
                        <el-option label="始终浅色" value="light" />
                        <el-option label="始终深色" value="dark" />
                    </el-select>
                    <div class="text-xs text-gray-400 mt-1">选择"跟随博客主题"时，Giscus 将根据页面主题自动切换明暗模式</div>
                </el-form-item>
            </template>

            <!-- Waline 配置 -->
            <template v-if="form.commentProvider === 'waline'">
                <el-form-item label="服务端 URL">
                    <el-input v-model="commentConfig.serverURL" placeholder="https://your-waline-server.com" />
                </el-form-item>
            </template>

            <!-- GitHub Pages -->
            <el-divider content-position="left">GitHub Pages 部署</el-divider>
            <el-form-item label="启用">
                <el-switch v-model="form.githubEnabled" />
            </el-form-item>
            <template v-if="form.githubEnabled">
                <el-form-item label="Personal Access Token">
                    <el-input v-model="form.githubToken" type="password" show-password placeholder="ghp_..." />
                </el-form-item>
                <el-form-item label="目标仓库">
                    <el-input v-model="form.githubRepo" placeholder="owner/repo" />
                </el-form-item>
                <el-form-item label="分支">
                    <el-input v-model="form.githubBranch" placeholder="gh-pages" />
                </el-form-item>
                <el-form-item label="自定义域名">
                    <el-input v-model="form.githubCname" placeholder="可选，如 blog.example.com" />
                </el-form-item>
            </template>

            <!-- 阿里云 OSS -->
            <el-divider content-position="left">阿里云 OSS 部署</el-divider>
            <el-form-item label="启用">
                <el-switch v-model="form.ossEnabled" />
            </el-form-item>
            <template v-if="form.ossEnabled">
                <el-form-item label="Endpoint">
                    <el-input v-model="form.ossEndpoint" placeholder="oss-cn-beijing.aliyuncs.com" />
                </el-form-item>
                <el-form-item label="AccessKey ID">
                    <el-input v-model="form.ossAccessKeyId" type="password" show-password />
                </el-form-item>
                <el-form-item label="AccessKey Secret">
                    <el-input v-model="form.ossAccessKeySecret" type="password" show-password />
                </el-form-item>
                <el-form-item label="Bucket 名称">
                    <el-input v-model="form.ossBucketName" />
                </el-form-item>
                <el-form-item label="路径前缀">
                    <el-input v-model="form.ossBasePath" placeholder="可选" />
                </el-form-item>
            </template>

            <!-- 操作按钮 -->
            <el-divider content-position="left">操作</el-divider>
            <el-form-item>
                <div class="flex flex-wrap gap-3">
                    <el-button type="primary" @click="saveConfig">保存配置</el-button>
                    <el-button type="success" @click="generateFull" :loading="generating">全量生成</el-button>
                    <el-button type="warning" @click="generateIncremental" :loading="generating">增量生成</el-button>
                    <el-button @click="downloadZip">下载 ZIP</el-button>
                    <el-button type="danger" @click="deployNow" :loading="deploying">立即部署</el-button>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { getStaticSiteConfig, updateStaticSiteConfig, triggerGeneration, triggerDeploy, getTaskProgress } from '@/api/admin/staticsite'

const form = reactive({
    commentProvider: 'none',
    commentConfig: '',
    githubEnabled: false,
    githubToken: '',
    githubRepo: '',
    githubBranch: 'gh-pages',
    githubCname: '',
    ossEnabled: false,
    ossEndpoint: '',
    ossAccessKeyId: '',
    ossAccessKeySecret: '',
    ossBucketName: '',
    ossBasePath: '',
    autoDeploy: false,
    outputBaseUrl: '/'
})

const commentConfig = reactive({
    repo: '',
    repoId: '',
    category: '',
    categoryId: '',
    theme: 'auto',
    serverURL: ''
})

const generating = ref(false)
const deploying = ref(false)
const taskTimeout = ref(false)
const progress = reactive({ status: 'IDLE', generatedPages: 0, totalPages: 0 })
let progressTimer = null
let pollingStartTime = null
let lastProgressUpdate = null
let lastGeneratedPages = 0

const POLL_INTERVAL = 1000
const TIMEOUT_THRESHOLD = 60000

const progressPercent = computed(() => {
    if (progress.totalPages === 0) return 0
    return Math.round((progress.generatedPages / progress.totalPages) * 100)
})

const elapsedTimeText = computed(() => {
    if (!pollingStartTime) return ''
    const elapsed = Date.now() - pollingStartTime
    return formatDuration(elapsed)
})

const estimatedRemaining = computed(() => {
    if (!pollingStartTime || progress.generatedPages === 0 || progress.totalPages === 0) return ''
    const elapsed = Date.now() - pollingStartTime
    const rate = progress.generatedPages / elapsed
    const remaining = (progress.totalPages - progress.generatedPages) / rate
    if (remaining <= 0) return '即将完成'
    return formatDuration(remaining)
})

function formatDuration(ms) {
    const seconds = Math.floor(ms / 1000)
    if (seconds < 60) return `${seconds}秒`
    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${minutes}分${secs}秒`
}

onMounted(() => {
    loadConfig()
})

onUnmounted(() => {
    if (progressTimer) clearInterval(progressTimer)
})

async function loadConfig() {
    const res = await getStaticSiteConfig()
    if (res.data.success && res.data.data) {
        const data = res.data.data
        Object.keys(form).forEach(key => {
            if (data[key] !== undefined && data[key] !== null) {
                form[key] = data[key]
            }
        })
        if (data.commentConfig) {
            try {
                const parsed = JSON.parse(data.commentConfig)
                Object.assign(commentConfig, parsed)
            } catch (e) { /* ignore */ }
        }
    }
}

async function saveConfig() {
    const data = { ...form }
    if (form.commentProvider !== 'none') {
        data.commentConfig = JSON.stringify(commentConfig)
    } else {
        data.commentConfig = ''
    }
    const res = await updateStaticSiteConfig(data)
    if (res.data.success) {
        ElMessage.success('配置已保存')
    } else {
        ElMessage.error(res.data.message || '保存失败')
    }
}

async function generateFull() {
    generating.value = true
    taskTimeout.value = false
    const res = await triggerGeneration({ taskType: 'FULL', autoDeploy: form.autoDeploy })
    if (res.data.success) {
        ElMessage.success('全量生成已触发')
        startProgressPolling()
    } else {
        ElMessage.error(res.data.message || '触发失败')
        generating.value = false
    }
}

async function generateIncremental() {
    generating.value = true
    taskTimeout.value = false
    const res = await triggerGeneration({ taskType: 'INCREMENTAL', autoDeploy: form.autoDeploy })
    if (res.data.success) {
        ElMessage.success('增量生成已触发')
        startProgressPolling()
    } else {
        ElMessage.error(res.data.message || '触发失败')
        generating.value = false
    }
}

async function deployNow() {
    deploying.value = true
    const res = await triggerDeploy()
    if (res.data.success) {
        ElMessage.success('部署已触发')
    } else {
        ElMessage.error(res.data.message || '部署失败')
    }
    deploying.value = false
}

function downloadZip() {
    window.open('/api/admin/static-site/download', '_blank')
}

function startProgressPolling() {
    if (progressTimer) clearInterval(progressTimer)
    pollingStartTime = Date.now()
    lastProgressUpdate = Date.now()
    lastGeneratedPages = 0
    taskTimeout.value = false

    progressTimer = setInterval(async () => {
        const res = await getTaskProgress()
        if (res.data.success && res.data.data) {
            const data = res.data.data
            progress.status = data.status
            progress.generatedPages = data.generatedPages || 0
            progress.totalPages = data.totalPages || 0

            if (progress.generatedPages !== lastGeneratedPages) {
                lastProgressUpdate = Date.now()
                lastGeneratedPages = progress.generatedPages
            }

            if (data.status === 'RUNNING' && Date.now() - lastProgressUpdate > TIMEOUT_THRESHOLD) {
                taskTimeout.value = true
            }

            if (data.status !== 'RUNNING') {
                clearInterval(progressTimer)
                progressTimer = null
                generating.value = false
                pollingStartTime = null
                if (data.status === 'SUCCESS') {
                    ElMessage.success('生成完成')
                } else if (data.status === 'FAILED') {
                    ElMessage.error('生成失败: ' + (data.errorMessage || '未知错误'))
                }
            }
        }
    }, POLL_INTERVAL)
}

function cancelPollingAndReset() {
    if (progressTimer) {
        clearInterval(progressTimer)
        progressTimer = null
    }
    generating.value = false
    taskTimeout.value = false
    progress.status = 'IDLE'
    pollingStartTime = null
}
</script>
