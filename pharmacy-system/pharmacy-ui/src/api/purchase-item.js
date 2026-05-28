import request from '@/utils/request'

export const getPurchaseItemList = (params) => {
  return request({
    url: '/purchase-item/list',
    method: 'get',
    params
  })
}

export const getPurchaseItemById = (id) => {
  return request({
    url: `/purchase-item/${id}`,
    method: 'get'
  })
}

export const addPurchaseItem = (data) => {
  return request({
    url: '/purchase-item',
    method: 'post',
    data
  })
}

export const updatePurchaseItem = (data) => {
  return request({
    url: '/purchase-item',
    method: 'put',
    data
  })
}

export const deletePurchaseItem = (id) => {
  return request({
    url: `/purchase-item/${id}`,
    method: 'delete'
  })
}

export const stockInPurchaseItems = (purchaseId) => {
  return request({
    url: `/purchase-item/stock-in/${purchaseId}`,
    method: 'post'
  })
}
