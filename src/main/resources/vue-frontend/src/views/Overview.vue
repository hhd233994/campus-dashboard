<template>
  <div class="overview-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ overview.totalAmount?.toFixed(2) || '0.00' }}</div>
              <div class="stat-label">总消费金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ overview.todayAmount?.toFixed(2) || '0.00' }}</div>
              <div class="stat-label">今日消费</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon :size="32"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.totalCount || 0 }}</div>
              <div class="stat-label">消费次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>消费分类占比</span>
            </div>
          </template>
          <div ref="categoryChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>每日消费趋势</span>
            </div>
          </template>
          <div ref="dailyChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getCategoryStats } from '@/api/record'
import { ElMessage } from 'element-plus'

const overview = ref({})
const categoryChartRef = ref(null)
const dailyChartRef = ref(null)

let categoryChart = null
let dailyChart = null

// 加载数据
const loadData = async () => {
  try {
    // 获取概览数据
    const overviewRes = await getOverview()
    overview.value = overviewRes.data
    
    // 获取分类统计
    const categoryRes = await getCategoryStats()
    renderCategoryChart(categoryRes.data)
    
    // 渲染每日趋势图（使用模拟数据，实际应该调用后端API）
    renderDailyChart()
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 渲染分类饼图
const renderCategoryChart = (data) => {
  if (!categoryChartRef.value) return
  
  categoryChart = echarts.init(categoryChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
    },
    series: [
      {
        name: '消费分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: true,
          formatter: '{b}: ¥{c}',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold',
          },
        },
        data: data.map(item => ({
          name: item.category,
          value: item.totalAmount,
        })),
      },
    ],
  }
  
  categoryChart.setOption(option)
}

// 渲染每日趋势折线图
const renderDailyChart = () => {
  if (!dailyChartRef.value) return
  
  dailyChart = echarts.init(dailyChartRef.value)
  
  // 这里使用模拟数据，实际应该从后端获取
  const dates = []
  const amounts = []
  for (let i = 6; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    amounts.push(Math.random() * 100 + 50)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>消费: ¥{c}',
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}',
      },
    },
    series: [
      {
        name: '消费金额',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' },
          ]),
        },
        itemStyle: {
          color: '#409EFF',
        },
        data: amounts,
      },
    ],
  }
  
  dailyChart.setOption(option)
}

onMounted(() => {
  loadData()
  
  // 响应式调整图表大小
  window.addEventListener('resize', () => {
    categoryChart?.resize()
    dailyChart?.resize()
  })
})
</script>

<style scoped>
.overview-container {
  padding: 0;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.charts-row {
  margin-top: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
