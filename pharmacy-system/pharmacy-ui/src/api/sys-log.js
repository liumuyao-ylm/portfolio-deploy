import request from '@/utils/request'

export function getSysLogList(params) {
  return request({
    url: '/sys-log/list',
    method: 'get',
    params
  })
}

export function getSysLogById(id) {
  return request({
    url: `/sys-log/${id}`,
    method: 'get'
  })
}
