<template>
  <div class="statistics-container">
    <!-- 分类统计柱状图 -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="card-header">
          <span>各类别消费统计</span>
        </div>
      </template>
      <div ref="barChartRef" style="height: 400px"></div>
    </el-card>
    
    <!-- 详细数据表格 -->
    <el-card shadow="hover" class="table-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>详细统计数据</span>
        </div>
      </template>
      
      <el-table :data="categoryStats" style="width: 100%">
        <el-table-column prop="category" label="类别" width="150">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="消费次数" width="150" />
        <el-table-column prop="totalAmount" label="总金额" width="150">
          <template #default="{ row }">
            <span class="amount-highlight">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="avgAmount" label="平均金额" width="150">
          <template #default="{ row }">
            ¥{{ row.avgAmount?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="maxAmount" label="最高金额" width="150">
          <template #default="{ row }">
            ¥{{ row.maxAmount?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="最低金额" width="150">
          <template #default="{ row }">
            ¥{{ row.minAmount?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getCategoryStats } from '@/api/record'
import { ElMessage } from 'element-plus'

const categoryStats = ref([])
const barChartRef = ref(null)

let barChart = null

// 加载数据
const loadData = async () => {
  try {
    const res = await getCategoryStats()
    categoryStats.value = res.data
    
    // 渲染柱状图
    renderBarChart(res.data)
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 渲染柱状图
const renderBarChart = (data) => {
  if (!barChartRef.value) return
  
  barChart = echarts.init(barChartRef.value)
  
  const categories = data.map(item => item.category)
  const amounts = data.map(item => item.totalAmount)
  const counts = data.map(item => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
    },
    legend: {
      data: ['总金额', '消费次数'],
      top: 10,
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        interval: 0,
        rotate: 30,
      },
    },
    yAxis: [
      {
        type: 'value',
        name: '金额（元）',
        position: 'left',
        axisLabel: {
          formatter: '¥{value}',
        },
      },
      {
        type: 'value',
        name: '次数',
        position: 'right',
      },
    ],
    series: [
      {
        name: '总金额',
        type: 'bar',
        data: amounts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' },
          ]),
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' },
            ]),
          },
        },
      },
      {
        name: '消费次数',
        type: 'line',
        yAxisIndex: 1,
        data: counts,
        smooth: true,
        itemStyle: {
          color: '#f56c6c',
        },
        lineStyle: {
          color: '#f56c6c',
          width: 2,
        },
      },
    ],
  }
  
  barChart.setOption(option)
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

onMounted(() => {
  loadData()
  
  // 响应式调整图表大小
  window.addEventListener('resize', () => {
    barChart?.resize()
  })
})
</script>

<style scoped>
.statistics-container {
  padding: 0;
}

/* 图表卡片 */
.chart-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  background: #FFFFFF;
  overflow: hidden;
}

.chart-card :deep(.el-card__header) {
  background: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-light);
  padding: 16px 24px;
}

/* 表格卡片 */
.table-card {
  margin-top: 20px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  background: #FFFFFF;
  overflow: hidden;
}

.table-card :deep(.el-card__header) {
  background: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-light);
  padding: 16px 24px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
  color: var(--text-primary);
  letter-spacing: 0.5px;
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

/* 类别标签 - 柔和配色 */
.table-card :deep(.el-tag) {
  border-radius: var(--radius-sm) !important;
  padding: 6px 14px !important;
  font-weight: 500;
  border: none !important;
  font-size: 13px;
}

.table-card :deep(.el-tag--warning) {
  background: rgba(245, 201, 122, 0.15);
  color: #D4A85C;
}

.table-card :deep(.el-tag--success) {
  background: rgba(123, 198, 126, 0.12);
  color: #5BA85E;
}

.table-card :deep(.el-tag--info) {
  background: rgba(107, 141, 214, 0.12);
  color: #4A6CB5;
}

.table-card :deep(.el-tag--danger) {
  background: rgba(239, 68, 68, 0.1);
  color: #DC4A4A;
}

/* 金额列特殊样式 */
.amount-highlight {
  color: var(--primary-color);
  font-weight: 700;
  font-size: 15px;
}
</style>
