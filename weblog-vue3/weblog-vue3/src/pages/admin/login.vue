<template>
    <div class="grid grid-cols-2 h-screen">
        <div class="col-span-2 order-2 p-10 md:col-span-1 md:order-1 bg-slate-900">
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInLeft animate__fast">
                <h2 class="font-bold text-4xl mb-7 text-white">{{ t('login.blogTitle') }}</h2>
                <p class="text-white">{{ t('login.blogDesc') }}</p>
                <img src="@/assets/dashboard.png" class="w-1/2">
            </div>
        </div>
        <div class="col-span-2 order-1 md:col-span-1 md:order-2 bg-white">
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInRight animate__fast">
                <h1 class="font-bold text-4xl mb-5">{{ isLogin ? t('login.welcome') : t('login.register') }}</h1>
                <div class="flex items-center justify-center mb-7 text-gray-400 space-x-2">
                    <span class="h-[1px] w-16 bg-gray-200"></span>
                    <span>{{ isLogin ? t('login.accountLogin') : t('login.createAccount') }}</span>
                    <span class="h-[1px] w-16 bg-gray-200"></span>
                </div>
                <el-form class="w-5/6 md:w-2/5" ref="formRef" :rules="rules" :model="form">
                    <el-form-item prop="username">
                        <el-input size="large" v-model="form.username" :placeholder="t('login.usernamePlaceholder')" :prefix-icon="User" clearable />
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input size="large" type="password" v-model="form.password" :placeholder="t('login.passwordPlaceholder')"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item prop="confirmPassword" v-if="!isLogin">
                        <el-input size="large" type="password" v-model="form.confirmPassword" :placeholder="t('login.confirmPasswordPlaceholder')"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item>
                        <el-button class="w-full mt-2" size="large" :loading="loading" type="primary" @click="onSubmit">
                            {{ isLogin ? t('login.loginBtn') : t('login.registerBtn') }}
                        </el-button>
                    </el-form-item>
                </el-form>
                <div class="text-gray-500">
                    <span>{{ isLogin ? t('login.noAccount') : t('login.hasAccount') }}</span>
                    <el-button type="text" @click="toggleMode" class="text-blue-500">
                        {{ isLogin ? t('login.goRegister') : t('login.goLogin') }}
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
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
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
        callback(new Error(t('validation.confirmPassword')))
    } else if (value !== form.password) {
        callback(new Error(t('validation.confirmPasswordMismatch')))
    } else {
        callback()
    }
}

const rules = computed(() => {
    const baseRules = {
        username: [
            {
                required: true,
                message: t('validation.usernameRequired'),
                trigger: 'blur'
            }
        ],
        password: [
            {
                required: true,
                message: t('validation.passwordRequired'),
                trigger: 'blur',
            },
            {
                min: 4,
                message: t('validation.passwordMinLength'),
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
            showMessage(t('login.loginSuccess'))

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
            showMessage(t('login.registerSuccess'))
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