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
        stripe
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

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
