import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/admin/user'
import { removeToken } from '@/composables/cookie'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({})

  // 设置用户信息（返回 Promise，方便等待）
  function setUserInfo() {
    return new Promise((resolve, reject) => {
      // 调用后端获取用户信息接口
      getUserInfo().then(res => {
        if (res.success == true) {
          userInfo.value = res.data
          resolve(res.data)
        } else {
          reject(new Error(res.message))
        }
      }).catch(err => {
        reject(err)
      })
    })
  }

  // 退出登录
  function logout() {
    // 删除 cookie 中的 token 令牌
    removeToken()
    // 删除登录用户信息
    userInfo.value = {}
  }

  return { userInfo, setUserInfo, logout }
}, 
{
  // 开启持久化
  persist: true,
}
)