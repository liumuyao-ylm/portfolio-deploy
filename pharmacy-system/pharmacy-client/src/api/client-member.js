import request from '@/utils/request'

export function loginClientMember(phone) {
  return request({
    url: '/client/member/login',
    method: 'post',
    data: { phone }
  })
}

export function getClientMemberProfile(customerId) {
  return request({
    url: '/client/member/profile',
    method: 'get',
    params: { customerId }
  })
}

export function getClientMemberPage(params) {
  return request({
    url: '/client/member/page',
    method: 'get',
    params
  })
}

export function getClientMemberOrders(customerId) {
  return request({
    url: '/client/member/orders',
    method: 'get',
    params: { customerId }
  })
}
