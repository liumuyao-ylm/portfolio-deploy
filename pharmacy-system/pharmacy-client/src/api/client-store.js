import request from '@/utils/request'

export function getClientStoreInfo() {
  return request({
    url: '/client/store-info',
    method: 'get'
  })
}
