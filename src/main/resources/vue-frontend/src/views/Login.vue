<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="32"><DataAnalysis /></el-icon>
        </div>
        <h1>校园消费数据管理平台</h1>
        <p class="subtitle">Campus Consumption Data Management Platform</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
            />
          </div>
        </el-form-item>
        
        <el-form-item prop="password">
          <div class="input-wrapper">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            <span>登录系统</span>
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <el-icon><InfoFilled /></el-icon>
        <span>测试账号：admin / 123456（管理员）或 zhangsan / 123456（普通用户）</span>
      </div>
    </div>
    
    <!-- 底部特性卡片 -->
    <div class="features">
      <div class="feature-card">
        <div class="feature-icon security">
          <el-icon><Shield /></el-icon>
        </div>
        <h3>企业级安全</h3>
        <p>JWT 认证 + BCrypt 加密</p>
      </div>
      <div class="feature-card">
        <div class="feature-icon performance">
          <el-icon><Lightning /></el-icon>
        </div>
        <h3>高性能缓存</h3>
        <p>Caffeine 本地缓存优化</p>
      </div>
      <div class="feature-card">
        <div class="feature-icon analytics">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <h3>可视化分析</h3>
        <p>ECharts 动态图表展示</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    console.log('开始登录...', loginForm)
    await userStore.login(loginForm)
    console.log('登录成功')
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败详情:', error)
    console.error('错误响应:', error.response)
    const errorMsg = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: var(--bg-secondary);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

/* 登录卡片 - 清新典雅版 */
.login-box {
  width: 100%;
  max-width: 440px;
  padding: 48px 40px;
  background: #FFFFFF;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  animation: slideUp 0.6s ease-out;
  position: relative;
  z-index: 1;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo 区域 */
.logo {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 24px rgba(74, 108, 247, 0.25);
}

.logo h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  letter-spacing: 0.5px;
}

.subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  letter-spacing: 1px;
}

/* 表单样式 */
.login-form {
  margin-top: 32px;
}

.input-wrapper {
  margin-bottom: 20px;
}

.input-wrapper :deep(.el-input__wrapper) {
  background: #FFFFFF !important;
  border: 1px solid var(--border-color) !important;
  border-radius: var(--radius-sm) !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  padding: 12px 16px !important;
}

.input-wrapper :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light) !important;
  box-shadow: 0 0 0 1px var(--primary-light) inset !important;
}

.input-wrapper :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px var(--primary-color) inset !important;
}

.input-wrapper :deep(.el-input__inner) {
  color: var(--text-primary) !important;
  font-size: 15px !important;
}

.input-wrapper :deep(.el-input__inner::placeholder) {
  color: var(--text-tertiary) !important;
}

.input-wrapper :deep(.el-input__prefix) {
  color: var(--text-secondary) !important;
}

/* 登录按钮 - 清新典雅版 */
.login-btn {
  height: 52px !important;
  border-radius: var(--radius-sm) !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 1px !important;
  background: var(--gradient-primary) !important;
  border: none !important;
  box-shadow: 0 2px 12px rgba(74, 108, 247, 0.2) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
}

.login-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 16px rgba(74, 108, 247, 0.3) !important;
}

.login-btn:active {
  transform: translateY(0) !important;
}

/* 提示信息 - 清新典雅版 */
.tips {
  margin-top: 24px;
  padding: 16px;
  background: rgba(74, 108, 247, 0.05);
  border: 1px solid rgba(74, 108, 247, 0.1);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: var(--text-secondary);
}

.tips .el-icon {
  color: var(--primary-color);
  font-size: 18px;
}

/* 特性卡片 - 清新典雅版 */
.features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  max-width: 1000px;
  width: 100%;
  margin-top: 60px;
  position: relative;
  z-index: 1;
}

.feature-card {
  padding: 24px;
  background: #FFFFFF;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  text-align: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  box-shadow: var(--shadow-sm);
}

.feature-card:hover {
  border-color: var(--primary-light);
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

.feature-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.feature-icon.security {
  background: rgba(74, 108, 247, 0.1);
  color: var(--primary-color);
}

.feature-icon.performance {
  background: rgba(123, 198, 126, 0.12);
  color: var(--success-color);
}

.feature-icon.analytics {
  background: rgba(107, 141, 214, 0.12);
  color: var(--info-color);
}

.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.feature-card p {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    padding: 32px 24px;
  }
  
  .logo h1 {
    font-size: 24px;
  }
  
  .features {
    grid-template-columns: 1fr;
    gap: 16px;
    margin-top: 40px;
  }
  
  .feature-card {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 20px 16px;
  }
  
  .login-box {
    padding: 24px 20px;
  }
  
  .logo h1 {
    font-size: 22px;
  }
  
  .subtitle {
    font-size: 12px;
  }
}
</style>
