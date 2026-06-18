<template>
    <div class="bg-white py-5 flex items-center justify-center text-sm text-gray-500 shadow-none">
        <!-- Copyright 版权信息 -->
        Copyright © 2024. All rights reserved. Provided by&nbsp;<a class="underline" target="_blank">{{ t('admin.footer.team') }}</a>
    </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>