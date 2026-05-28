import request from '@/utils/request'

export const getSaleOrderList = (params) => {
  return request({
    url: '/sale-order/list',
    method: 'get',
    params
  })
}

export const getSaleOrderById = (id) => {
  return request({
    url: `/sale-order/${id}`,
    method: 'get'
  })
}

export const addSaleOrder = (data) => {
  return request({
    url: '/sale-order',
    method: 'post',
    data
  })
}

export const updateSaleOrder = (data) => {
  return request({
    url: '/sale-order',
    method: 'put',
    data
  })
}

export const deleteSaleOrder = (id) => {
  return request({
    url: `/sale-order/${id}`,
    method: 'delete'
  })
}

export const batchDeleteSaleOrder = (ids) => {
  return request({
    url: '/sale-order/batch',
    method: 'delete',
    data: ids
  })
}

export const createSaleOrder = (data) => {
  return request({
    url: '/sale-order/create',
    method: 'post',
    data
  })
}

export const outboundSaleOrder = (id) => {
  return request({
    url: `/sale-order/${id}/outbound`,
    method: 'post'
  })
}

export const exportSaleOrder = () => {
  return request({
    url: '/sale-order/export',
    method: 'get',
    responseType: 'blob'
  })
}
