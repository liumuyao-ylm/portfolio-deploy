import request from '@/utils/request'

export const getSaleItemList = (params) => {
  return request({
    url: '/sale-item/list',
    method: 'get',
    params
  })
}

export const getSaleItemById = (id) => {
  return request({
    url: `/sale-item/${id}`,
    method: 'get'
  })
}

export const addSaleItem = (data) => {
  return request({
    url: '/sale-item',
    method: 'post',
    data
  })
}

export const updateSaleItem = (data) => {
  return request({
    url: '/sale-item',
    method: 'put',
    data
  })
}

export const deleteSaleItem = (id) => {
  return request({
    url: `/sale-item/${id}`,
    method: 'delete'
  })
}
