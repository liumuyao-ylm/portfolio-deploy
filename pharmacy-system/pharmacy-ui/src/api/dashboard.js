import request from '@/utils/request'

export function getStatistics() {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

export function getRecentOrders() {
  return request({
    url: '/dashboard/recent-orders',
    method: 'get'
  })
}

export function getExpiredStocks() {
  return request({
    url: '/dashboard/expired-stocks',
    method: 'get'
  })
}
