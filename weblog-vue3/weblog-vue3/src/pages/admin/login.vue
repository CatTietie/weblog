<template>
    <div class="grid grid-cols-2 h-screen">
        <div class="col-span-2 order-2 p-10 md:col-span-1 md:order-1 bg-slate-900">
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInLeft animate__fast">
                <h2 class="font-bold text-4xl mb-7 text-white">Weblog 博客</h2>
                <p class="text-white">一款由 Spring Boot + Mybaits Plus + Vue 3.2 + Vite 4 开发的前后端分离博客。</p>
                <img src="@/assets/dashboard.png" class="w-1/2">
            </div>
        </div>
        <div class="col-span-2 order-1 md:col-span-1 md:order-2 bg-white">
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInRight animate__fast">
                <h1 class="font-bold text-4xl mb-5">{{ isLogin ? '欢迎回来' : '用户注册' }}</h1>
                <div class="flex items-center justify-center mb-7 text-gray-400 space-x-2">
                    <span class="h-[1px] w-16 bg-gray-200"></span>
                    <span>{{ isLogin ? '账号密码登录' : '创建新账号' }}</span>
                    <span class="h-[1px] w-16 bg-gray-200"></span>
                </div>
                <el-form class="w-5/6 md:w-2/5" ref="formRef" :rules="rules" :model="form">
                    <el-form-item prop="username">
                        <el-input size="large" v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item prop="confirmPassword" v-if="!isLogin">
                        <el-input size="large" type="password" v-model="form.confirmPassword" placeholder="请确认密码"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item>
                        <el-button class="w-full mt-2" size="large" :loading="loading" type="primary" @click="onSubmit">
                            {{ isLogin ? '登录' : '注册' }}
                        </el-button>
                    </el-form-item>
                </el-form>
                <div class="text-gray-500">
                    <span>{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
                    <el-button type="text" @click="toggleMode" class="text-blue-500">
                        {{ isLogin ? '立即注册' : '立即登录' }}
                    </el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { login, register } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showMessage} from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const isLogin = ref(true)

const form = reactive({
    username: '',
    password: '',
    confirmPassword: ''
})

const router = useRouter()
const loading = ref(false)

const formRef = ref(null)

const validateConfirmPassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请确认密码'))
    } else if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const rules = computed(() => {
    const baseRules = {
        username: [
            {
                required: true,
                message: '用户名不能为空',
                trigger: 'blur'
            }
        ],
        password: [
            {
                required: true,
                message: '密码不能为空',
                trigger: 'blur',
            },
            {
                min: 4,
                message: '密码长度不能少于4位',
                trigger: 'blur'
            }
        ]
    }

    if (!isLogin.value) {
        baseRules.confirmPassword = [
            {
                required: true,
                validator: validateConfirmPassword,
                trigger: 'blur'
            }
        ]
    }

    return baseRules
})

const toggleMode = () => {
    isLogin.value = !isLogin.value
    form.username = ''
    form.password = ''
    form.confirmPassword = ''
    formRef.value?.clearValidate()
}

const handleLogin = () => {
    loading.value = true

    login(form.username, form.password).then((res) => {
        if (res.success == true) {
            showMessage('登录成功')

            let token = res.data.token
            setToken(token)

            // 等待获取用户信息完成后再跳转
            userStore.setUserInfo().then(() => {
                router.push('/admin/index/article-stats')
            }).catch(() => {
                // 即使获取用户信息失败，也跳转（防止卡死）
                router.push('/admin/index/article-stats')
            })
        } else {
            let message = res.message
            showMessage(message, 'error')
        }
    })
    .finally(() => {
        loading.value = false
    })
}

const handleRegister = () => {
    loading.value = true

    register(form.username, form.password).then((res) => {
        if (res.success == true) {
            showMessage('注册成功，请登录')
            toggleMode()
        } else {
            let message = res.message
            showMessage(message, 'error')
        }
    })
    .finally(() => {
        loading.value = false
    })
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        if (isLogin.value) {
            handleLogin()
        } else {
            handleRegister()
        }
    })
}

function onKeyUp(e) {
    if (e.key == 'Enter') {
        onSubmit()
    }
}

onMounted(() => {
    document.addEventListener('keyup', onKeyUp)
})

onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
})
</script>