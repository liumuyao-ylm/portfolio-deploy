import request from '@/utils/request'

export function getClientMedicines(params) {
  return request({
    url: '/client/medicines',
    method: 'get',
    params
  })
}

export function getClientMedicineDetail(id) {
  return request({
    url: `/client/medicines/${id}`,
    method: 'get'
  })
}
