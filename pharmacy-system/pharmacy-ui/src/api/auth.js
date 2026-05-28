import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function getPasswordHint(username) {
  return request({
    url: '/user/password-hint',
    method: 'get',
    params: { username }
  })
}
