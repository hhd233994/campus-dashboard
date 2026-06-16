import request from './request'

// 获取概览数据
export const getOverview = () => {
  return request.get('/record/statistics/overview')
}

// 获取分类统计
export const getCategoryStats = () => {
  return request.get('/record/statistics/category')
}

// 获取消费记录列表（管理员）
export const getRecordList = (params) => {
  return request.get('/record/list', { params })
}

// 获取我的消费记录
export const getMyRecords = (params) => {
  return request.get('/record/my', { params })
}

// 导出 Excel
export const exportExcel = () => {
  return request.get('/record/export/excel', {
    responseType: 'blob'
  })
}
