import request from '@/utils/request'

export function sendClientChat(message) {
  return request({
    url: '/client/chat',
    method: 'post',
    data: {
      message
    }
  })
}
