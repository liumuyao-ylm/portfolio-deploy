import request from '@/utils/request'

export function getStockCheckList(params) {
  return request({
    url: '/stock-check/list',
    method: 'get',
    params
  })
}

export function getStockCheckById(id) {
  return request({
    url: `/stock-check/${id}`,
    method: 'get'
  })
}

export function addStockCheck(data) {
  return request({
    url: '/stock-check',
    method: 'post',
    data
  })
}

export function createStockCheck(data) {
  return request({
    url: '/stock-check/create',
    method: 'post',
    data
  })
}

export function updateStockCheck(data) {
  return request({
    url: '/stock-check',
    method: 'put',
    data
  })
}

export function deleteStockCheck(id) {
  return request({
    url: `/stock-check/${id}`,
    method: 'delete'
  })
}
