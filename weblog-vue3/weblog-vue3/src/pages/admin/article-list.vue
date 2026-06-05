<template>
    <div>
        <!-- 表头分页查询条件， shadow="never" 指定 card 卡片组件没有阴影 -->
        <el-card shadow="never" class="mb-5">
            <!-- flex 布局，内容垂直居中 -->
            <div class="flex items-center">
                <el-text>文章标题</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchArticleTitle" placeholder="请输入（模糊查询）" /></div>

                <el-text>创建日期</el-text>
                <div class="ml-3 w-30 mr-5">
                    <!-- 日期选择组件（区间选择） -->
                    <el-date-picker v-model="pickDate" type="daterange" :range-separator="t('datepicker.rangeSeparator')" :start-placeholder="t('datepicker.startPlaceholder')"
                        :end-placeholder="t('datepicker.endPlaceholder')" size="default" :shortcuts="shortcuts" @change="datepickerChange" />
                </div>

                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">查询</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- 写文章按钮 -->
            <div class="mb-5">
                <el-button type="primary" v-if="userStore.hasPermission('article:publish')" @click="isArticlePublishEditorShow = true">
                    <el-icon class="mr-1">
                        <EditPen />
                    </el-icon>
                    写文章</el-button>
            </div>

            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="id" label="ID" width="50" />
                <el-table-column prop="title" label="标题" width="350" />
                <el-table-column prop="cover" label="封面" width="180">
                    <template #default="scope">
                        <el-image style="width: 100px;" :src="scope.row.cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                            {{ scope.row.status === 1 ? '已发布' : '草稿' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button v-if="userStore.hasPermission('article:update')" size="small" @click="showArticleUpdateEditor(scope.row)">
                            <el-icon class="mr-1">
                                <Edit />
                            </el-icon>
                            编辑</el-button>
                        <el-button v-if="scope.row.status === 0 && userStore.hasPermission('article:publish')" type="success" size="small" @click="publishArticleAction(scope.row)">
                            <el-icon class="mr-1">
                                <Promotion />
                            </el-icon>
                            发布</el-button>
                        <el-button size="small" @click="goArticleDetailPage(scope.row.id)">
                            <el-icon class="mr-1">
                                <View />
                            </el-icon>
                            预览</el-button>
                        <el-button v-if="userStore.hasPermission('article:delete')" type="danger" size="small" @click="deleteArticleSubmit(scope.row)">
                            <el-icon class="mr-1">
                                <Delete />
                            </el-icon>
                            删除
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

        <!-- 写博客 -->
        <el-dialog v-model="isArticlePublishEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false">
            <template #header="{ close, titleId, titleClass }">
                <!-- 固钉组件，固钉到顶部 -->
                <el-affix :offset="20" style="width: 100%;">
                    <!-- 指定 flex 布局， 高度为 10， 背景色为白色 -->
                    <div class="flex h-10 bg-white">
                        <!-- 字体加粗 -->
                        <h4 class="font-bold">写文章</h4>
                        <el-tag class="ml-3" type="info" size="small">草稿</el-tag>
                        <!-- 靠右对齐 -->
                        <div class="ml-auto flex">
                            <el-button @click="isArticlePublishEditorShow = false">取消</el-button>
                            <el-button @click="saveAsDraft">
                                <el-icon class="mr-1">
                                    <Document />
                                </el-icon>
                                保存草稿
                            </el-button>
                            <el-button type="primary" @click="publishArticleSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                发布
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <!-- label-position="top" 用于指定 label 元素在上面 -->
            <el-form :model="form" ref="publishArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit
                        clearable />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <!-- Markdown 编辑器 -->
                    <MdEditor v-model="form.content" @onUploadImg="onUploadImg" editorId="publishArticleEditor" />
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.cover" :src="form.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="摘要" prop="summary">
                    <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
                    <el-input v-model="form.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" />
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <span class="w-60">
                        <!-- 标签选择 -->
                        <el-select v-model="form.tags" multiple filterable remote reserve-keyword placeholder="请输入文章标签"
                            remote-show-suffix allow-create default-first-option :remote-method="remoteMethod"
                            :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>

        <!-- 编辑博客 -->
        <el-dialog v-model="isArticleUpdateEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false">
            <template #header="{ close, titleId, titleClass }">
                <!-- 固钉组件，固钉到顶部 -->
                <el-affix :offset="20" style="width: 100%;">
                    <!-- 指定 flex 布局， 高度为 10， 背景色为白色 -->
                    <div class="flex h-10 bg-white">
                        <!-- 字体加粗 -->
                        <h4 class="font-bold">编辑文章</h4>
                        <el-tag class="ml-3" :type="updateArticleForm.status === 1 ? 'success' : 'info'" size="small">
                            {{ updateArticleForm.status === 1 ? '已发布' : '草稿' }}
                        </el-tag>
                        <!-- 靠右对齐 -->
                        <div class="ml-auto flex">
                            <el-button @click="isArticleUpdateEditorShow = false">
                                {{ updateArticleForm.status === 1 ? '完成' : '取消' }}
                            </el-button>
                            <el-button v-if="updateArticleForm.status === 0" type="success" @click="publishExistingArticle">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                发布
                            </el-button>
                            <el-button type="primary" @click="updateSubmit">
                                <el-icon class="mr-1">
                                    <Edit />
                                </el-icon>
                                {{ updateArticleForm.status === 1 ? '保存并生成快照' : '保存草稿' }}
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <!-- label-position="top" 用于指定 label 元素在上面 -->
            <el-form :model="updateArticleForm" ref="updateArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="updateArticleForm.title" autocomplete="off" size="large" maxlength="40"
                        show-word-limit clearable />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <!-- Markdown 编辑器 -->
                    <MdEditor v-model="updateArticleForm.content" @onUploadImg="onUploadImg"
                        editorId="updateArticleEditor" />
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleUpdateCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="updateArticleForm.cover" :src="updateArticleForm.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="摘要" prop="summary">
                    <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
                    <el-input v-model="updateArticleForm.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" />
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="updateArticleForm.categoryId" clearable placeholder="---请选择---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <span class="w-60">
                        <!-- 标签选择 -->
                        <el-select v-model="updateArticleForm.tags" multiple filterable remote reserve-keyword
                            placeholder="请输入文章标签" remote-show-suffix allow-create default-first-option
                            :remote-method="remoteMethod" :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>

            <!-- 版本历史 -->
            <el-divider content-position="left">版本历史</el-divider>
            <el-timeline v-if="articleVersions.length > 0">
                <el-timeline-item
                    v-for="version in articleVersions"
                    :key="version.id"
                    :timestamp="version.createTime"
                    placement="top"
                >
                    <el-card shadow="hover" class="cursor-pointer" @click="showVersionDetail(version.id)">
                        <p>{{ version.title }}</p>
                    </el-card>
                </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无版本历史" />
        </el-dialog>

        <!-- 版本预览弹窗 -->
        <el-dialog v-model="isVersionPreviewShow" title="版本预览" width="70%">
            <h3 class="text-xl font-bold mb-4">{{ versionDetail.title }}</h3>
            <p class="text-sm text-gray-500 mb-4">保存时间：{{ versionDetail.createTime }}</p>
            <MdEditor v-model="versionDetail.content" :preview-only="true" editorId="versionPreviewEditor" />
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { getArticlePageList, deleteArticle, publishArticle, getArticleDetail, updateArticle, changeArticleStatus, getArticleVersionList, getArticleVersionDetail } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import { getCategorySelectList } from '@/api/admin/category'
import { searchTags, getTagSelectList } from '@/api/admin/tag'
import moment from 'moment'
import { showMessage, showModel } from '@/composables/util'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const router = useRouter()

// 模糊搜索的文章标题
const searchArticleTitle = ref('')
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

// 重置
const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchArticleTitle.value = ''
}

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
    getArticlePageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, title: searchArticleTitle.value })
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
    size.value = chooseSize
    getTableData()
}

// 删除文章
const deleteArticleSubmit = (row) => {
    showModel('是否确定要删除该文章？').then(() => {
        deleteArticle(row.id).then((res) => {
            if (res.success == false) {
                let message = res.message
                showMessage(message, 'error')
                return
            }

            showMessage('删除成功')
            getTableData()
        })
    }).catch(() => {})
}

// 列表中发布文章
const publishArticleAction = (row) => {
    showModel('是否确定要发布该文章？').then(() => {
        changeArticleStatus({ id: row.id, status: 1 }).then((res) => {
            if (res.success == false) {
                showMessage(res.message, 'error')
                return
            }
            showMessage('发布成功')
            getTableData()
        })
    }).catch(() => {})
}

// 是否显示文章发布对话框
const isArticlePublishEditorShow = ref(false)
// 发布文章表单引用
const publishArticleFormRef = ref(null)

// 表单对象
const form = reactive({
    id: null,
    title: '',
    content: '请输入内容',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// 修改文章表单对象
const updateArticleForm = reactive({
    id: null,
    title: '',
    content: '请输入内容',
    cover: '',
    categoryId: null,
    tags: [],
    summary: "",
    status: 0
})

// 表单校验规则
const rules = {
    title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' },
        { min: 1, max: 40, message: '文章标题要求大于1个字符，小于40个字符', trigger: 'blur' },
    ],
    content: [{ required: true }],
    cover: [{ required: true }],
    categoryId: [{ required: true, message: '请选择文章分类', trigger: 'blur' }],
    tags: [{ required: true, message: '请选择文章标签', trigger: 'blur' }],
}

// 重置发布表单
const resetForm = () => {
    form.title = ''
    form.content = ''
    form.cover = ''
    form.summary = ''
    form.categoryId = null
    form.tags = []
}

// 上传文章封面图片
const handleCoverChange = (file) => {
    let formData = new FormData()
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }
        form.cover = e.data.url
        showMessage('上传成功')
    })
}

// 编辑文章：上传文章封面图片
const handleUpdateCoverChange = (file) => {
    let formData = new FormData()
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }
        updateArticleForm.cover = e.data.url
        showMessage('上传成功')
    })
}

// 编辑器图片上传
const onUploadImg = async (files, callback) => {
    const res = await Promise.all(
        files.map((file) => {
            return new Promise((rev, rej) => {
                let formData = new FormData()
                formData.append("file", file);
                uploadFile(formData).then((res) => {
                    callback([res.data.url]);
                })
            });
        })
    );
}

// 文章分类
const categories = ref([])
getCategorySelectList().then((e) => {
    categories.value = e.data
})

// 标签 select Loading 状态，默认不显示
const tagSelectLoading = ref(false)
// 文章标签
const tags = ref([])
// 渲染标签数据
getTagSelectList().then(res => {
    tags.value = res.data
})


// 根据用户输入的标签名称，远程模糊查询
const remoteMethod = (query) => {
    if (query) {
        tagSelectLoading.value = true
        searchTags(query).then((e) => {
            if (e.success) {
                tags.value = e.data
            }
        }).finally(() => tagSelectLoading.value = false)
    }
}

// 保存草稿
const saveAsDraft = () => {
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        publishArticle(form).then((res) => {
            if (res.success == false) {
                showMessage(res.message, 'error')
                return
            }
            showMessage('草稿保存成功')
            isArticlePublishEditorShow.value = false
            resetForm()
            getTableData()
        })
    })
}

// 发布文章（创建后立即发布）
const publishArticleSubmit = () => {
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        publishArticle(form).then((res) => {
            if (res.success == false) {
                showMessage(res.message, 'error')
                return
            }
            // 创建成功后立即发布
            changeArticleStatus({ id: res.data, status: 1 }).then((statusRes) => {
                if (statusRes.success == false) {
                    showMessage(statusRes.message, 'error')
                    return
                }
                showMessage('发布成功')
                isArticlePublishEditorShow.value = false
                resetForm()
                getTableData()
            })
        })
    })
}


// 是否显示编辑文章对话框
const isArticleUpdateEditorShow = ref(false)
// 编辑文章表单引用
const updateArticleFormRef = ref(null)
// 版本历史
const articleVersions = ref([])

// 刷新版本列表
const refreshVersionList = () => {
    if (!updateArticleForm.id) return
    getArticleVersionList(updateArticleForm.id).then((res) => {
        if (res.success) {
            articleVersions.value = res.data || []
        }
    })
}

// 编辑文章按钮点击事件
const showArticleUpdateEditor = (row) => {
    isArticleUpdateEditorShow.value = true
    let articleId = row.id
    getArticleDetail(articleId).then((res) => {
        if (res.success) {
            updateArticleForm.id = res.data.id
            updateArticleForm.title = res.data.title
            updateArticleForm.cover = res.data.cover
            updateArticleForm.content = res.data.content
            updateArticleForm.categoryId = res.data.categoryId
            updateArticleForm.tags = res.data.tagIds
            updateArticleForm.summary = res.data.summary
            updateArticleForm.status = res.data.status
        }
    })

    // 加载版本历史
    getArticleVersionList(articleId).then((res) => {
        if (res.success) {
            articleVersions.value = res.data || []
        }
    })
}

// 发布已有草稿文章
const publishExistingArticle = () => {
    changeArticleStatus({ id: updateArticleForm.id, status: 1 }).then((res) => {
        if (res.success == false) {
            showMessage(res.message, 'error')
            return
        }
        showMessage('发布成功')
        // 立即更新本地状态，避免旧值导致逻辑错乱
        updateArticleForm.status = 1
        // 刷新版本列表（发布后后续保存会生成快照）
        refreshVersionList()
        // 同步刷新列表页状态
        getTableData()
    })
}

// 保存文章 — 根据文章状态区分处理逻辑
const updateSubmit = () => {
    updateArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        updateArticle(updateArticleForm).then((res) => {
            if (res.success == false) {
                showMessage(res.message, 'error')
                return
            }

            if (updateArticleForm.status === 1) {
                // 已发布文章：保存会触发版本快照，不关闭对话框，刷新版本列表让用户看到新快照
                showMessage('保存成功，已生成版本快照')
                refreshVersionList()
                getTableData()
            } else {
                // 草稿文章：保存后关闭对话框
                showMessage('草稿保存成功')
                isArticleUpdateEditorShow.value = false
                getTableData()
            }
        })
    })
}

// 版本预览
const isVersionPreviewShow = ref(false)
const versionDetail = reactive({
    title: '',
    content: '',
    createTime: ''
})

const showVersionDetail = (versionId) => {
    getArticleVersionDetail(versionId).then((res) => {
        if (res.success) {
            versionDetail.title = res.data.title
            versionDetail.content = res.data.content
            versionDetail.createTime = res.data.createTime
            isVersionPreviewShow.value = true
        }
    })
}

// 跳转文章详情页
const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}
</script>

<style scoped>
/* 封面图片样式 */
.avatar-uploader .avatar {
    width: 200px;
    height: 100px;
    display: block;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 100px;
    text-align: center;
}

/* 指定 select 下拉框宽度 */
.el-select--large {
    width: 600px;
}
</style>

<style>
.md-editor-footer {
    height: 40px;
}
</style>
