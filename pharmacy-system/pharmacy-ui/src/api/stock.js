import request from '@/utils/request'

export function getStockList(params) {
  return request({
    url: '/stock/list',
    method: 'get',
    params
  })
}

export function getStockListWithMedicine(params) {
  return request({
    url: '/stock/list/with-medicine',
    method: 'get',
    params
  })
}

export function getStockById(id) {
  return request({
    url: `/stock/${id}`,
    method: 'get'
  })
}

export function addStock(data) {
  return request({
    url: '/stock',
    method: 'post',
    data
  })
}

export function updateStock(data) {
  return request({
    url: '/stock',
    method: 'put',
    data
  })
}

export function deleteStock(id) {
  return request({
    url: `/stock/${id}`,
    method: 'delete'
  })
}

export function exportStock() {
  return request({
    url: '/stock/export',
    method: 'get',
    responseType: 'blob'
  })
}
