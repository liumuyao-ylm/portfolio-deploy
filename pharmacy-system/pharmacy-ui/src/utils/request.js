import axios from 'axios'
import { ElMessage } from 'element-plus'

const DEMO_READONLY_MESSAGE = '演示账号仅支持查看，不能修改数据。'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const userInfo = getUserInfo()
    if (userInfo?.token) {
      config.headers.Authorization = `Bearer ${userInfo.token}`
    }
    if (isDemoUser(userInfo) && isRestrictedDemoRequest(config)) {
      ElMessage.warning(DEMO_READONLY_MESSAGE)
      return Promise.reject(new Error(DEMO_READONLY_MESSAGE))
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    const status = error?.response?.status
    const message = error?.response?.data?.message
    if (status === 403) {
      ElMessage.warning(message || DEMO_READONLY_MESSAGE)
    } else if (status === 401) {
      ElMessage.error(message || '登录已失效，请重新登录。')
      localStorage.removeItem('userInfo')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

function getUserInfo() {
  try {
    const info = localStorage.getItem('userInfo')
    return info ? JSON.parse(info) : null
  } catch {
    return null
  }
}

function isDemoUser(userInfo) {
  return String(userInfo?.role || '').toUpperCase() === 'DEMO'
}

function isRestrictedDemoRequest(config) {
  const method = String(config.method || 'get').toLowerCase()
  const url = String(config.url || '').toLowerCase()
  if (url === '/user/login' || url === '/user/password-hint') {
    return false
  }
  return ['post', 'put', 'patch', 'delete'].includes(method) || url.includes('/export') || url.includes('/import')
}

export default request
