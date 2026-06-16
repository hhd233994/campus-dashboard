<template>
  <div class="profile-container">
    <!-- 用户信息卡片 -->
    <el-card shadow="hover" class="info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" :icon="Edit" @click="editMode = !editMode">
            {{ editMode ? '取消编辑' : '编辑' }}
          </el-button>
        </div>
      </template>
      
      <el-form :model="userInfo" label-width="100px" :disabled="!editMode">
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" disabled />
        </el-form-item>
        
        <el-form-item label="真实姓名">
          <el-input v-model="userInfo.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="userInfo.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'success'">
            {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-form-item>
        
        <el-form-item v-if="editMode">
          <el-button type="primary" @click="handleUpdate">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 修改密码卡片 -->
    <el-card shadow="hover" class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form :model="passwordForm" label-width="100px" ref="passwordFormRef">
        <el-form-item 
          label="旧密码" 
          prop="oldPassword"
          :rules="[
            { required: true, message: '请输入旧密码', trigger: 'blur' },
            { min: 6, message: '密码长度至少6位', trigger: 'blur' }
          ]"
        >
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password
            placeholder="请输入旧密码"
          />
        </el-form-item>
        
        <el-form-item 
          label="新密码" 
          prop="newPassword"
          :rules="[
            { required: true, message: '请输入新密码', trigger: 'blur' },
            { min: 6, message: '密码长度至少6位', trigger: 'blur' }
          ]"
        >
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码（至少6位）"
          />
        </el-form-item>
        
        <el-form-item 
          label="确认密码" 
          prop="confirmPassword"
          :rules="[
            { required: true, message: '请再次输入新密码', trigger: 'blur' },
            { validator: validateConfirmPassword, trigger: 'blur' }
          ]"
        >
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const editMode = ref(false)
const passwordFormRef = ref(null)

// 用户信息
const userInfo = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  role: ''
})

// 原始数据备份
let originalUserInfo = {}

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    Object.assign(userInfo, res.data)
    // 备份原始数据
    originalUserInfo = { ...res.data }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 更新用户信息
const handleUpdate = async () => {
  try {
    const data = {
      realName: userInfo.realName,
      email: userInfo.email,
      phone: userInfo.phone
    }
    
    await updateUserInfo(data)
    ElMessage.success('更新成功')
    editMode.value = false
    
    // 更新本地存储的用户信息
    userStore.userInfo = { ...userStore.userInfo, ...data }
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error(error.response?.data?.message || '更新失败')
  }
}

// 取消编辑
const cancelEdit = () => {
  editMode.value = false
  // 恢复原始数据
  Object.assign(userInfo, originalUserInfo)
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    await changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    
    // 退出登录
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 1500)
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      console.error('修改密码失败:', error)
      ElMessage.error(error.response?.data?.message || '修改密码失败')
    }
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

/* 信息卡片 */
.info-card {
  flex: 1;
  min-width: 400px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  background: #FFFFFF;
  overflow: hidden;
}

.info-card :deep(.el-card__header) {
  background: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-light);
  padding: 16px 24px;
}

/* 密码卡片 */
.password-card {
  flex: 1;
  min-width: 400px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  background: #FFFFFF;
  overflow: hidden;
}

.password-card :deep(.el-card__header) {
  background: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-light);
  padding: 16px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

/* 表单样式 */
.info-card :deep(.el-form),
.password-card :deep(.el-form) {
  padding: 24px;
}

.info-card :deep(.el-form-item__label),
.password-card :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 14px;
}

.info-card :deep(.el-input__wrapper),
.password-card :deep(.el-input__wrapper) {
  border-radius: var(--radius-sm);
  box-shadow: 0 0 0 1px var(--border-color) inset;
  transition: all 0.3s ease;
}

.info-card :deep(.el-input__wrapper:hover),
.password-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset;
}

.info-card :deep(.el-input__wrapper.is-focus),
.password-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset;
}

/* 按钮样式 */
.info-card :deep(.el-button--primary),
.password-card :deep(.el-button--primary) {
  background: var(--gradient-primary);
  border: none;
  border-radius: var(--radius-sm);
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(74, 108, 247, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.info-card :deep(.el-button--primary:hover),
.password-card :deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(74, 108, 247, 0.25);
}

.info-card :deep(.el-button),
.password-card :deep(.el-button) {
  border-radius: var(--radius-sm);
  font-weight: 500;
  height: 36px;
  padding: 0 20px;
}

/* 角色标签 - 柔和配色 */
.info-card :deep(.el-tag) {
  border-radius: var(--radius-sm) !important;
  padding: 6px 14px !important;
  font-weight: 500;
  border: none !important;
  font-size: 13px;
}

.info-card :deep(.el-tag--danger) {
  background: rgba(239, 68, 68, 0.1);
  color: #DC4A4A;
}

.info-card :deep(.el-tag--success) {
  background: rgba(123, 198, 126, 0.12);
  color: #5BA85E;
}

@media (max-width: 900px) {
  .profile-container {
    flex-direction: column;
  }
  
  .info-card,
  .password-card {
    min-width: 100%;
  }
}
</style>
