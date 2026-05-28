import request from '@/utils/request'

export function getMedicineList(params) {
  return request({
    url: '/medicine/list',
    method: 'get',
    params
  })
}

export function getMedicineById(id) {
  return request({
    url: `/medicine/${id}`,
    method: 'get'
  })
}

export function addMedicine(data) {
  return request({
    url: '/medicine',
    method: 'post',
    data
  })
}

export function updateMedicine(data) {
  return request({
    url: '/medicine',
    method: 'put',
    data
  })
}

export function deleteMedicine(id) {
  return request({
    url: `/medicine/${id}`,
    method: 'delete'
  })
}

export function exportMedicine(params) {
  return request({
    url: '/medicine/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function downloadMedicineTemplate() {
  return request({
    url: '/medicine/import-template',
    method: 'get',
    responseType: 'blob'
  })
}

export function importMedicine(file) {
  const data = new FormData()
  data.append('file', file)
  return request({
    url: '/medicine/import',
    method: 'post',
    data
  })
}
