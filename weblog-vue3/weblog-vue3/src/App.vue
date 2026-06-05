
<template>
   <el-config-provider :locale="elementLocale">
      <router-view></router-view>
   </el-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'
import { useLocaleStore } from '@/stores/locale'

const localeStore = useLocaleStore()

const elementLocale = computed(() => {
  return localeStore.currentLocale === 'zh' ? zhCn : en
})
</script>

<style>
/* 自定义顶部加载 Loading 颜色 */
#nprogress .bar {
   background: #409eff!important;
}
</style>
