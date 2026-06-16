<template>
  <div class="logs-container">
    <!-- 筛选表单 -->
    <el-card shadow="hover" class="filter-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="操作人">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        
        <el-form-item label="操作类型">
          <el-select
            v-model="searchForm.operationType"
            placeholder="全部类型"
            clearable
            style="width: 150px"
          >
            <el-option label="登录" value="LOGIN" />
            <el-option label="查询" value="QUERY" />
            <el-option label="新增" value="INSERT" />
            <el-option label="修改" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="导出" value="EXPORT" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 日志表格 -->
    <el-card shadow="hover" class="table-card">
      <el-table
        v-loading="loading"
        :data="logList"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ getOperationTypeName(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="150" />
        <el-table-column prop="description" label="操作描述" min-width="200" />
        <el-table-column prop="ipAddress" label="IP地址" width="150" />
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const logList = ref([])

const searchForm = reactive({
  username: '',
  operationType: '',
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm,
    }
    
    const response = await axios.get('/api/log/list', {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    
    const res = response.data
    if (res.code === 200) {
      logList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.operationType = ''
  handleSearch()
}

// 分页大小改变
const handleSizeChange = () => {
  loadData()
}

// 页码改变
const handlePageChange = () => {
  loadData()
}

// 获取操作类型标签
const getOperationTypeTag = (type) => {
  const tags = {
    'LOGIN': 'success',
    'QUERY': 'info',
    'INSERT': 'warning',
    'UPDATE': 'primary',
    'DELETE': 'danger',
    'EXPORT': '',
  }
  return tags[type] || ''
}

// 获取操作类型名称
const getOperationTypeName = (type) => {
  const names = {
    'LOGIN': '登录',
    'QUERY': '查询',
    'INSERT': '新增',
    'UPDATE': '修改',
    'DELETE': '删除',
    'EXPORT': '导出',
  }
  return names[type] || type
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.logs-container {
  padding: 0;
}

/* 筛选卡片 - 清新典雅版 */
.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  background: #FFFFFF;
  padding: 20px 24px;
}

.filter-card :deep(.el-form) {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.filter-card :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 14px;
}

.filter-card :deep(.el-button) {
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 36px;
}

.filter-card :deep(.el-button--primary) {
  background: var(--gradient-primary);
  border: none;
  box-shadow: 0 2px 8px rgba(74, 108, 247, 0.15);
}

.filter-card :deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(74, 108, 247, 0.25);
}

.filter-card :deep(.el-input__wrapper) {
  border-radius: var(--radius-sm);
  box-shadow: 0 0 0 1px var(--border-color) inset;
  transition: all 0.3s ease;
}

.filter-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset;
}

.filter-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset;
}

/* 表格卡片 */
.table-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  background: #FFFFFF;
  overflow: hidden;
}

/* 表格样式 - 清新典雅版 */
.table-card :deep(.el-table) {
  border-radius: 0;
  box-shadow: none;
  background: transparent;
}

.table-card :deep(.el-table__header-wrapper th) {
  background: var(--bg-tertiary) !important;
  color: var(--text-secondary);
  font-weight: 600;
  padding: 16px 0;
  border-bottom: 2px solid var(--border-color) !important;
  letter-spacing: 0.5px;
}

.table-card :deep(.el-table__body-wrapper td) {
  padding: 14px 0;
  border-bottom: 1px solid var(--border-light) !important;
  color: var(--text-primary);
}

/* 去除斑马纹 */
.table-card :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: transparent !important;
}

/* 行悬停效果 - 左侧主色竖条指示器 */
.table-card :deep(.el-table__body-wrapper tr:hover) {
  background: rgba(74, 108, 247, 0.03) !important;
  position: relative;
}

.table-card :deep(.el-table__body-wrapper tr:hover td:first-child) {
  position: relative;
}

.table-card :deep(.el-table__body-wrapper tr:hover td:first-child::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: var(--gradient-primary);
  border-radius: 0 2px 2px 0;
}

/* 操作类型标签 - 柔和配色 */
.table-card :deep(.el-tag) {
  border-radius: var(--radius-sm) !important;
  padding: 6px 14px !important;
  font-weight: 500;
  border: none !important;
  font-size: 13px;
}

.table-card :deep(.el-tag--success) {
  background: rgba(123, 198, 126, 0.12);
  color: #5BA85E;
}

.table-card :deep(.el-tag--info) {
  background: rgba(107, 141, 214, 0.12);
  color: #4A6CB5;
}

.table-card :deep(.el-tag--warning) {
  background: rgba(245, 201, 122, 0.15);
  color: #D4A85C;
}

.table-card :deep(.el-tag--primary) {
  background: rgba(74, 108, 247, 0.1);
  color: #4A6CF7;
}

.table-card :deep(.el-tag--danger) {
  background: rgba(239, 68, 68, 0.1);
  color: #DC4A4A;
}

/* 分页器美化 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 0 4px;
}

.pagination-container :deep(.el-pagination) {
  font-weight: 500;
}

.pagination-container :deep(.el-pagination button) {
  border-radius: var(--radius-sm);
  background: #FFFFFF;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  transition: all 0.3s ease;
  min-width: 32px;
  height: 32px;
}

.pagination-container :deep(.el-pagination button:hover:not(:disabled)) {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(74, 108, 247, 0.05);
  transform: translateY(-1px);
}

.pagination-container :deep(.el-pager li) {
  border-radius: var(--radius-sm);
  background: #FFFFFF;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  margin: 0 4px;
  transition: all 0.3s ease;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
}

.pagination-container :deep(.el-pager li:hover) {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(74, 108, 247, 0.05);
  transform: translateY(-1px);
}

.pagination-container :deep(.el-pager li.is-active) {
  background: var(--gradient-primary);
  border-color: var(--primary-color);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(74, 108, 247, 0.2);
}

.pagination-container :deep(.el-pagination__total) {
  color: var(--text-secondary);
  font-weight: 500;
}

.pagination-container :deep(.el-pagination__sizes) {
  margin-left: 16px;
}

.pagination-container :deep(.el-select .el-input__wrapper) {
  border-radius: var(--radius-sm);
}
</style>
