<template>
    <div>
        <el-card shadow="never">
            <template #header>
                <span class="text-sm font-bold">{{ t('notification.sendGlobal') }}</span>
            </template>

            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width: 600px;">
                <el-form-item :label="t('notification.titleLabel')" prop="title">
                    <el-input v-model="form.title" :placeholder="t('notification.titlePlaceholder')" maxlength="100" show-word-limit />
                </el-form-item>

                <el-form-item :label="t('notification.contentLabel')" prop="content">
                    <el-input v-model="form.content" type="textarea" :rows="6" :placeholder="t('notification.contentPlaceholder')"
                        maxlength="500" show-word-limit />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" :loading="loading" @click="handleSend">
                        {{ t('notification.sendBtn') }}
                    </el-button>
                    <el-button @click="handleReset">{{ t('common.reset') }}</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sendGlobalNotification } from '@/api/admin/notification'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
    title: '',
    content: ''
})

const rules = {
    title: [
        { required: true, message: t('validation.notificationTitleRequired'), trigger: 'blur' },
        { max: 100, message: t('validation.notificationTitleMaxLength'), trigger: 'blur' }
    ],
    content: [
        { required: true, message: t('validation.notificationContentRequired'), trigger: 'blur' },
        { max: 500, message: t('validation.notificationContentMaxLength'), trigger: 'blur' }
    ]
}

const handleSend = () => {
    formRef.value.validate((valid) => {
        if (!valid) return

        ElMessageBox.confirm(t('confirm.sendNotification'), t('confirm.sendNotificationTitle'), {
            confirmButtonText: t('common.confirm'),
            cancelButtonText: t('common.cancel'),
            type: 'warning'
        }).then(() => {
            loading.value = true
            sendGlobalNotification(form).then(res => {
                if (res.success) {
                    ElMessage.success(t('message.notificationSent'))
                    handleReset()
                } else {
                    ElMessage.error(res.message || t('message.notificationSendFail'))
                }
            }).finally(() => {
                loading.value = false
            })
        }).catch(() => {})
    })
}

const handleReset = () => {
    form.title = ''
    form.content = ''
    formRef.value?.resetFields()
}
</script>