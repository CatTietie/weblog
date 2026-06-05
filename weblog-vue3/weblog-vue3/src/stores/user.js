import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/admin/user'
import { removeToken, getToken } from '@/composables/cookie'
import { ElMessageBox } from 'element-plus'
import i18n from '@/i18n'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({})

  // 权限刷新定时器
  let refreshTimer = null

  // 设置用户信息（返回 Promise，方便等待）
  function setUserInfo() {
    return new Promise((resolve, reject) => {
      getUserInfo().then(res => {
        if (res.success == true) {
          userInfo.value = res.data
          startPermissionRefresh()
          resolve(res.data)
        } else {
          reject(new Error(res.message))
        }
      }).catch(err => {
        reject(err)
      })
    })
  }

  // 权限检查
  function hasPermission(code) {
    const permissions = userInfo.value?.permissions || []
    return permissions.includes(code)
  }

  // 检测权限是否变更
  function checkPermissionChange() {
    if (!getToken()) return

    getUserInfo().then(res => {
      if (res.success != true) return

      const oldPermissions = (userInfo.value?.permissions || []).slice().sort()
      const newPermissions = (res.data?.permissions || []).slice().sort()

      const changed = oldPermissions.length !== newPermissions.length ||
          oldPermissions.some((p, i) => p !== newPermissions[i])

      if (changed) {
        userInfo.value = res.data
        const { t } = i18n.global
        ElMessageBox.alert(
          t('message.permissionChanged'),
          t('message.permissionChangedTitle'),
          {
            confirmButtonText: t('common.confirm'),
            type: 'warning',
            callback: () => {
              location.reload()
            }
          }
        )
      }
    }).catch(() => {
      // 静默失败，不影响用户操作
    })
  }

  // 启动定时权限刷新（每 3 分钟）
  function startPermissionRefresh() {
    stopPermissionRefresh()
    refreshTimer = setInterval(checkPermissionChange, 3 * 60 * 1000)
  }

  // 停止定时刷新
  function stopPermissionRefresh() {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  }

  // 退出登录
  function logout() {
    stopPermissionRefresh()
    removeToken()
    userInfo.value = {}
  }

  return {
    userInfo,
    setUserInfo,
    hasPermission,
    checkPermissionChange,
    startPermissionRefresh,
    stopPermissionRefresh,
    logout
  }
},
{
  // 开启持久化
  persist: true,
}
)