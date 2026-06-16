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
            <span class="amount-column">¥{{ row.amount.toFixed(2) }}</span>
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
  startDate: dayjs().subtract(6, 'day').format('YYYY-MM-DD'), // 默认最近7天
  endDate: dayjs().format('YYYY-MM-DD'),
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

.filter-card :deep(.el-button--success) {
  background: var(--gradient-success);
  border: none;
  box-shadow: 0 2px 8px rgba(123, 198, 126, 0.15);
}

.filter-card :deep(.el-button--success:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(123, 198, 126, 0.25);
}

/* 表格卡片 - 清新典雅版 */
.table-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  background: #FFFFFF;
}

/* 表格样式优化 */
.table-card :deep(.el-table) {
  font-size: 14px;
  border-radius: var(--radius-md);
}

/* 表头样式 - 极淡背景 + 柔和文字 */
.table-card :deep(.el-table__header-wrapper th) {
  background: var(--bg-tertiary) !important;
  color: var(--text-secondary);
  font-weight: 600;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color) !important;
  letter-spacing: 0.3px;
  font-size: 13px;
}

/* 表格行基础样式 */
.table-card :deep(.el-table__body-wrapper tr) {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

/* 行悬停效果 - 轻微背景变化 + 左侧主色指示器 */
.table-card :deep(.el-table__body-wrapper tr:hover) {
  background: rgba(74, 108, 247, 0.03) !important;
}

.table-card :deep(.el-table__body-wrapper tr:hover::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--gradient-primary);
  border-radius: 0 2px 2px 0;
}

/* 单元格样式 */
.table-card :deep(.el-table__body-wrapper td) {
  padding: 14px 0;
  border-bottom: 1px solid var(--border-light) !important;
  color: var(--text-primary);
  font-size: 14px;
}

/* 去除斑马纹，保持通透感 */
.table-card :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: transparent;
}

/* 金额列样式 - 品牌色突出 */
.amount-column {
  font-weight: 700;
  color: var(--primary-color);
  font-size: 15px;
  letter-spacing: -0.2px;
}

/* 类别标签美化 - 柔和配色 */
.table-card :deep(.el-tag) {
  border-radius: var(--radius-sm);
  padding: 5px 12px;
  font-weight: 500;
  border: none;
  font-size: 13px;
}

.table-card :deep(.el-tag--success) {
  background: rgba(123, 198, 126, 0.12);
  color: #5BA85E;
}

.table-card :deep(.el-tag--warning) {
  background: rgba(245, 201, 122, 0.15);
  color: #D4A84B;
}

.table-card :deep(.el-tag--danger) {
  background: rgba(239, 68, 68, 0.1);
  color: #DC5252;
}

.table-card :deep(.el-tag--info) {
  background: rgba(107, 141, 214, 0.12);
  color: #5A7BC4;
}

/* 分页器美化 - 清新典雅版 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.pagination-container :deep(.el-pagination) {
  font-weight: 500;
}

/* 分页按钮基础样式 */
.pagination-container :deep(.el-pagination button) {
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-color);
  background: #FFFFFF;
  color: var(--text-secondary);
  min-width: 32px;
  height: 32px;
  line-height: 32px;
}

.pagination-container :deep(.el-pagination button:hover:not(:disabled)) {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(74, 108, 247, 0.06);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(74, 108, 247, 0.1);
}

/* 页码数字样式 */
.pagination-container :deep(.el-pager li) {
  border-radius: var(--radius-sm);
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 0 4px;
  background: #FFFFFF;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  font-size: 14px;
}

.pagination-container :deep(.el-pager li:hover) {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(74, 108, 247, 0.06);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(74, 108, 247, 0.1);
}

/* 当前激活页码 */
.pagination-container :deep(.el-pager li.is-active) {
  background: var(--gradient-primary);
  color: #FFFFFF;
  border: none;
  box-shadow: 0 2px 8px rgba(74, 108, 247, 0.2);
  font-weight: 600;
}

/* 总条数和每页显示 */
.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__sizes) {
  color: var(--text-secondary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .filter-card,
  .table-card {
    border-radius: var(--radius-md);
  }
  
  .pagination-container {
    justify-content: center;
  }
}
</style>
