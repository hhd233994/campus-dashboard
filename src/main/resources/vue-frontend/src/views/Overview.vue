<template>
  <div class="overview-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">校园消费数据看板</h2>
        <p class="page-subtitle">实时掌握校园消费动态</p>
      </div>
      <el-date-picker
        v-model="selectedDate"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        class="date-picker"
        @change="handleDateChange"
      />
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-primary">
          <div class="stat-content">
            <div class="stat-indicator"></div>
            <div class="stat-body">
              <div class="stat-icon">
                <el-icon :size="24"><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">总消费金额</div>
                <div class="stat-value">¥{{ overview.totalAmount?.toFixed(2) || '0.00' }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-success">
          <div class="stat-content">
            <div class="stat-indicator"></div>
            <div class="stat-body">
              <div class="stat-icon">
                <el-icon :size="24"><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">今日消费</div>
                <div class="stat-value">¥{{ overview.todayAmount?.toFixed(2) || '0.00' }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-warning">
          <div class="stat-content">
            <div class="stat-indicator"></div>
            <div class="stat-body">
              <div class="stat-icon">
                <el-icon :size="24"><List /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">本月订单数</div>
                <div class="stat-value">{{ overview.monthOrders || 0 }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-info">
          <div class="stat-content">
            <div class="stat-indicator"></div>
            <div class="stat-body">
              <div class="stat-icon">
                <el-icon :size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">活跃用户</div>
                <div class="stat-value">{{ overview.activeUsers || 0 }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="24" class="charts-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">消费趋势</span>
              <el-radio-group v-model="trendPeriod" size="small" @change="handleTrendChange">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="dailyChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">消费分类占比</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container chart-container-pie"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getCategoryStats } from '@/api/record'
import { ElMessage } from 'element-plus'

const overview = ref({})
const categoryChartRef = ref(null)
const dailyChartRef = ref(null)
const selectedDate = ref([])
const trendPeriod = ref('week')

let categoryChart = null
let dailyChart = null

// 处理日期范围变化
const handleDateChange = (value) => {
  console.log('选择的日期范围:', value)
  // TODO: 根据日期范围重新加载数据
  loadData()
}

// 处理趋势周期切换
const handleTrendChange = (value) => {
  renderDailyChart(value)
}

// 加载数据
const loadData = async () => {
  try {
    // 获取概览数据
    const overviewRes = await getOverview()
    overview.value = overviewRes.data
    
    // 获取分类统计
    const categoryRes = await getCategoryStats()
    renderCategoryChart(categoryRes.data)
    
    // 渲染每日趋势图
    renderDailyChart(trendPeriod.value)
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 渲染分类饼图
const renderCategoryChart = (data) => {
  if (!categoryChartRef.value) return
  
  if (!categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: '#FFFFFF',
      borderColor: '#E8EAF0',
      borderWidth: 1,
      textStyle: {
        color: '#2C3E50',
      },
      formatter: '{b}: ¥{c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      textStyle: {
        color: '#8B9DC3',
        fontSize: 13,
      },
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 12,
    },
    series: [
      {
        name: '消费分类',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#F7F8FC',
          borderWidth: 3,
        },
        label: {
          show: false,
        },
        emphasis: {
          scale: true,
          scaleSize: 8,
          label: {
            show: true,
            fontSize: 14,
            fontWeight: '500',
            color: '#2C3E50',
          },
        },
        data: data.map((item, index) => ({
          name: item.category,
          value: item.totalAmount,
          itemStyle: {
            color: ['#4A6CF7', '#6B8DD6', '#7BC67E', '#F5C97A'][index % 4],
          },
        })),
      },
    ],
  }
  
  categoryChart.setOption(option)
}

// 渲染每日趋势折线图
const renderDailyChart = (period) => {
  if (!dailyChartRef.value) return
  
  if (!dailyChart) {
    dailyChart = echarts.init(dailyChartRef.value)
  }
  
  // 模拟数据，实际应该从后端获取
  const days = period === 'week' ? 7 : 30
  const dates = []
  const amounts = []
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    amounts.push(Math.random() * 200 + 100)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#FFFFFF',
      borderColor: '#E8EAF0',
      borderWidth: 1,
      padding: [12, 16],
      textStyle: {
        color: '#2C3E50',
        fontSize: 13,
      },
      formatter: (params) => {
        return `
          <div style="font-weight: 600; margin-bottom: 6px;">${params[0].name}</div>
          <div style="color: #4A6CF7;">消费金额: <strong>¥${params[0].value.toFixed(2)}</strong></div>
        `
      },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '8%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#E8EAF0',
        },
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#8B9DC3',
        fontSize: 12,
        margin: 12,
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: '#F0F1F5',
          type: 'solid',
        },
      },
      axisLabel: {
        formatter: '¥{value}',
        color: '#8B9DC3',
        fontSize: 12,
      },
    },
    series: [
      {
        name: '消费金额',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        sampling: 'average',
        lineStyle: {
          width: 3,
          color: '#4A6CF7',
        },
        itemStyle: {
          color: '#4A6CF7',
          borderWidth: 2,
          borderColor: '#FFFFFF',
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(74, 108, 247, 0.15)' },
            { offset: 1, color: 'rgba(74, 108, 247, 0.02)' },
          ]),
        },
        data: amounts,
      },
    ],
  }
  
  dailyChart.setOption(option)
}

// 响应式调整图表大小
const handleResize = () => {
  categoryChart?.resize()
  dailyChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  categoryChart?.dispose()
  dailyChart?.dispose()
})
</script>

<style scoped>
.overview-container {
  padding: 0;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 28px 32px;
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(74, 108, 247, 0.06);
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #2C3E50;
  margin: 0 0 8px 0;
  letter-spacing: 0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #8B9DC3;
  margin: 0;
  font-weight: 400;
}

.date-picker {
  width: 280px;
}

.date-picker :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #E8EAF0;
  box-shadow: none;
  transition: all 0.3s ease;
}

.date-picker :deep(.el-input__wrapper:hover) {
  border-color: #6B8DD6;
}

.date-picker :deep(.el-input__wrapper.is-focus) {
  border-color: #4A6CF7;
  box-shadow: 0 0 0 2px rgba(74, 108, 247, 0.1);
}

/* 统计卡片 */
.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  border: none;
  border-radius: 12px;
  background: #FFFFFF;
  box-shadow: 0 2px 12px rgba(74, 108, 247, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(74, 108, 247, 0.12);
}

.stat-content {
  padding: 24px;
  position: relative;
}

.stat-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.stat-card-primary .stat-indicator {
  background: #4A6CF7;
}

.stat-card-success .stat-indicator {
  background: #7BC67E;
}

.stat-card-warning .stat-indicator {
  background: #F5C97A;
}

.stat-card-info .stat-indicator {
  background: #6B8DD6;
}

.stat-body {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-left: 8px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-card-primary .stat-icon {
  background: rgba(74, 108, 247, 0.1);
  color: #4A6CF7;
}

.stat-card-success .stat-icon {
  background: rgba(123, 198, 126, 0.1);
  color: #7BC67E;
}

.stat-card-warning .stat-icon {
  background: rgba(245, 201, 122, 0.1);
  color: #F5C97A;
}

.stat-card-info .stat-icon {
  background: rgba(107, 141, 214, 0.1);
  color: #6B8DD6;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #8B9DC3;
  margin-bottom: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.stat-value {
  font-size: 26px;
  font-weight: 600;
  color: #2C3E50;
  line-height: 1;
  letter-spacing: -0.5px;
}

/* 图表区域 */
.charts-row {
  margin-top: 0;
}

.chart-card {
  border: none;
  border-radius: 12px;
  background: #FFFFFF;
  box-shadow: 0 2px 12px rgba(74, 108, 247, 0.06);
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(74, 108, 247, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #2C3E50;
  letter-spacing: 0.3px;
}

.chart-container {
  height: 380px;
  width: 100%;
}

.chart-container-pie {
  height: 380px;
}

/* 单选按钮组样式 */
:deep(.el-radio-group) {
  border-radius: 8px;
}

:deep(.el-radio-button__inner) {
  border: 1px solid #E8EAF0;
  background: #FFFFFF;
  color: #8B9DC3;
  font-size: 13px;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

:deep(.el-radio-button__inner:hover) {
  border-color: #6B8DD6;
  color: #4A6CF7;
}

:deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 8px 0 0 8px;
}

:deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0 8px 8px 0;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #4A6CF7 0%, #6B8DD6 100%);
  border-color: #4A6CF7;
  color: #FFFFFF;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .date-picker {
    width: 100%;
  }
  
  .chart-container {
    height: 320px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px 24px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .stats-cards {
    margin-bottom: 16px;
  }
  
  .stat-content {
    padding: 20px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
  }
  
  .stat-value {
    font-size: 22px;
  }
  
  .chart-container {
    height: 280px;
  }
}
</style>
