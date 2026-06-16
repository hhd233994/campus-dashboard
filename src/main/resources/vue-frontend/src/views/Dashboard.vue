<template>
  <el-container class="dashboard-container">
    <!-- 侧边栏 -->
    <el-aside width="240px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="28"><DataAnalysis /></el-icon>
        </div>
        <span class="logo-text">校园消费平台</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/overview">
          <el-icon><Odometer /></el-icon>
          <span>首页看板</span>
        </el-menu-item>
        
        <el-menu-item index="/records">
          <el-icon><List /></el-icon>
          <span>消费记录</span>
        </el-menu-item>
        
        <el-menu-item index="/statistics">
          <el-icon><TrendCharts /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        
        <el-menu-item v-if="userStore.isAdmin" index="/logs">
          <el-icon><Document /></el-icon>
          <span>系统管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-wrapper">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/overview' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="notification-badge">
            <el-button circle size="large">
              <el-icon :size="20"><Bell /></el-icon>
            </el-button>
          </el-badge>
          
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <div class="user-avatar">
              <el-avatar :size="40" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                {{ userStore.userInfo.username?.charAt(0).toUpperCase() || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo.username || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前页面标题
const currentTitle = computed(() => {
  const titles = {
    '/overview': '首页看板',
    '/records': '消费记录',
    '/statistics': '数据统计',
    '/logs': '系统管理',
    '/profile': '个人中心',
  }
  return titles[route.path] || '首页'
})

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      userStore.logout()
      router.push('/login')
    } catch {
      // 用户取消
    }
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  background: #F7F8FC;
}

/* 侧边栏样式 */
.sidebar {
  background: #FFFFFF;
  box-shadow: 2px 0 12px rgba(74, 108, 247, 0.04);
  display: flex;
  flex-direction: column;
  z-index: 10;
  border-right: 1px solid #E8EAF0;
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 12px;
  border-bottom: 1px solid #E8EAF0;
  background: linear-gradient(135deg, #4A6CF7 0%, #6B8DD6 100%);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-text {
  color: white;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  padding: 12px 0;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  color: #8B9DC3;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(74, 108, 247, 0.06);
  color: #4A6CF7;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #4A6CF7 0%, #6B8DD6 100%);
  color: white;
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: white;
}

/* 主容器 */
.main-wrapper {
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.header {
  background: #FFFFFF;
  border-bottom: 1px solid #E8EAF0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  height: 70px;
  box-shadow: 0 2px 12px rgba(74, 108, 247, 0.04);
}

.header-left {
  flex: 1;
}

.header-left :deep(.el-breadcrumb) {
  font-size: 14px;
}

.header-left :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #4A6CF7;
  font-weight: 500;
}

.header-left :deep(.el-breadcrumb__inner) {
  color: #8B9DC3;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge :deep(.el-button) {
  border: 1px solid #E8EAF0;
  transition: all 0.3s;
  background: #FFFFFF;
  color: #8B9DC3;
}

.notification-badge :deep(.el-button:hover) {
  border-color: #4A6CF7;
  color: #4A6CF7;
  background: rgba(74, 108, 247, 0.06);
}

.user-dropdown {
  cursor: pointer;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  border-radius: 10px;
  transition: all 0.3s;
}

.user-avatar:hover {
  background: rgba(74, 108, 247, 0.06);
}

.username {
  font-size: 14px;
  color: #2C3E50;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: #8B9DC3;
}

/* 主内容区 */
.main-content {
  background: #F7F8FC;
  padding: 24px 32px;
  overflow-y: auto;
  min-height: calc(100vh - 70px);
}

@media (max-width: 768px) {
  .sidebar {
    width: 64px !important;
  }
  
  .logo-text {
    display: none;
  }
  
  .header {
    padding: 0 16px;
  }
  
  .main-content {
    padding: 16px;
  }
}
</style>
