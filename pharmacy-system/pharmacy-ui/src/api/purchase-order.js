import request from '@/utils/request'

export const getPurchaseOrderList = (params) => {
  return request({
    url: '/purchase-order/list',
    method: 'get',
    params
  })
}

export const getPurchaseOrderById = (id) => {
  return request({
    url: `/purchase-order/${id}`,
    method: 'get'
  })
}

export const addPurchaseOrder = (data) => {
  return request({
    url: '/purchase-order',
    method: 'post',
    data
  })
}

export const updatePurchaseOrder = (data) => {
  return request({
    url: '/purchase-order',
    method: 'put',
    data
  })
}

export const deletePurchaseOrder = (id) => {
  return request({
    url: `/purchase-order/${id}`,
    method: 'delete'
  })
}

export const batchDeletePurchaseOrder = (ids) => {
  return request({
    url: '/purchase-order/batch',
    method: 'delete',
    data: ids
  })
}

export const createPurchaseOrder = (data) => {
  return request({
    url: '/purchase-order/create',
    method: 'post',
    data
  })
}

export const inboundPurchaseOrder = (id) => {
  return request({
    url: `/purchase-order/${id}/inbound`,
    method: 'post'
  })
}

export const exportPurchaseOrder = () => {
  return request({
    url: '/purchase-order/export',
    method: 'get',
    responseType: 'blob'
  })
}
