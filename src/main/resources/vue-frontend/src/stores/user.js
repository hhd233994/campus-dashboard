import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'ADMIN')

  // 登录
  async function login(loginForm) {
    try {
      const res = await loginApi(loginForm)
      token.value = res.data.token
      userInfo.value = JSON.parse(atob(res.data.token.split('.')[1]))
      
      localStorage.setItem('token', token.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      
      return true
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    logout,
  }
})
