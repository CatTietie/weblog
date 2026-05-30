<template>
    <Header></Header>

    <main class="container max-w-screen-xl mx-auto p-4">
        <div class="mb-6 flex items-center justify-between">
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">我的简历</h1>
            <el-button type="primary" @click="formDialogRef.open()">新建简历</el-button>
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

    <Footer></Footer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import FormDialog from '@/components/FormDialog.vue'
import { getResumeList, createResume, deleteResume } from '@/api/admin/resume'
import { showMessage, showModel } from '@/composables/util'

const router = useRouter()

const resumeList = ref([])
const formDialogRef = ref(null)
const createFormRef = ref(null)

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

onMounted(() => {
    fetchList()
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
</script>
