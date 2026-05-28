import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/medicines',
    name: 'Medicines',
    component: () => import('@/views/medicines/index.vue'),
    meta: { title: '药品查询' }
  },
  {
    path: '/medicines/:id',
    name: 'MedicineDetail',
    component: () => import('@/views/medicine-detail/index.vue'),
    meta: { title: '药品详情' }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/views/chat/index.vue'),
    meta: { title: 'AI客服' }
  },
  {
    path: '/member',
    name: 'Member',
    component: () => import('@/views/member/index.vue'),
    meta: { title: '会员中心' }
  },
  {
    path: '/store',
    name: 'Store',
    component: () => import('@/views/store/index.vue'),
    meta: { title: '药店信息' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
