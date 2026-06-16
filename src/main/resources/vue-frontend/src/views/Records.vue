<template>
  <div class="records-container">
    <!-- 筛选表单 -->
    <el-card shadow="hover" class="filter-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="searchForm.startDate"
            type="date"
            placeholder="选择日期"
            style="width: 180px"
          />
        </el-form-item>
        
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="searchForm.endDate"
            type="date"
            placeholder="选择日期"
            style="width: 180px"
          />
        </el-form-item>
        
        <el-form-item label="类别">
          <el-select
            v-model="searchForm.category"
            placeholder="全部类别"
            clearable
            style="width: 150px"
          >
            <el-option label="餐饮" value="餐饮" />
            <el-option label="购物" value="购物" />
            <el-option label="交通" value="交通" />
            <el-option label="娱乐" value="娱乐" />
            <el-option label="其他" value="其他" />
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
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出 Excel
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 数据表格 -->
    <el-card shadow="hover" class="table-card">
      <el-table
        v-loading="loading"
        :data="recordList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="createTime" label="消费时间" width="180">
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
import { getRecordList, exportExcel } from '@/api/record'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const recordList = ref([])

const searchForm = reactive({
  startDate: '',
  endDate: '',
  category: '',
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
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm,
    }
    
    const res = await getRecordList(params)
    recordList.value = res.data.records || []
    pagination.total = res.data.total || 0
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
  searchForm.startDate = ''
  searchForm.endDate = ''
  searchForm.category = ''
  handleSearch()
}

// 导出 Excel
const handleExport = async () => {
  try {
    const response = await exportExcel()
    
    // 创建下载链接
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `消费记录_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 分页大小改变
const handleSizeChange = () => {
  loadData()
}

// 页码改变
const handlePageChange = () => {
  loadData()
}

// 获取类别标签类型
const getCategoryType = (category) => {
  const types = {
    '餐饮': 'warning',
    '购物': 'success',
    '交通': 'info',
    '娱乐': 'danger',
    '其他': '',
  }
  return types[category] || ''
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
.records-container {
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
