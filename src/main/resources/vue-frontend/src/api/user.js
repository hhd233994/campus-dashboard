import request from './request'

// 获取当前用户信息
export const getUserInfo = () => {
  return request.get('/user/info')
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request.put('/user/update', data)
}

// 修改密码
export const changePassword = (oldPassword, newPassword) => {
  return request.post('/user/change-password', null, {
    params: { oldPassword, newPassword }
  })
}
