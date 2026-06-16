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
      
      <el-table :data="categoryStats" stripe style="width: 100%">
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
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.totalAmount.toFixed(2) }}</span>
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

.chart-card,
.table-card {
  border-radius: 8px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
