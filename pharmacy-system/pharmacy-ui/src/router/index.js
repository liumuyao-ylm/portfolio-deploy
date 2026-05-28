import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '控制台', icon: 'Monitor' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: () => import('@/views/customer/index.vue'),
        meta: { title: '顾客管理', icon: 'User' }
      },
      {
        path: 'medicine',
        name: 'Medicine',
        component: () => import('@/views/medicine/index.vue'),
        meta: { title: '药品管理', icon: 'Box' }
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'Avatar', roles: ['admin'] }
      },
      {
        path: 'purchase-order',
        name: 'PurchaseOrder',
        component: () => import('@/views/purchase-order/index.vue'),
        meta: { title: '采购订单', icon: 'Document' }
      },
      {
        path: 'purchase-order/form',
        name: 'PurchaseOrderForm',
        component: () => import('@/views/purchase-order/form.vue'),
        meta: { title: '采购单编辑', hidden: true }
      },
      {
        path: 'purchase-item',
        name: 'PurchaseItem',
        component: () => import('@/views/purchase-item/index.vue'),
        meta: { title: '采购明细', icon: 'Tickets' }
      },
      {
        path: 'sale-order',
        name: 'SaleOrder',
        component: () => import('@/views/sale-order/index.vue'),
        meta: { title: '销售订单', icon: 'ShoppingCart' }
      },
      {
        path: 'sale-order/form',
        name: 'SaleOrderForm',
        component: () => import('@/views/sale-order/form.vue'),
        meta: { title: '销售单编辑', hidden: true }
      },
      {
        path: 'sale-item',
        name: 'SaleItem',
        component: () => import('@/views/sale-item/index.vue'),
        meta: { title: '销售明细', icon: 'List' }
      },
      {
        path: 'stock',
        name: 'Stock',
        component: () => import('@/views/stock/index.vue'),
        meta: { title: '库存管理', icon: 'Goods' }
      },
      {
        path: 'stock-check',
        name: 'StockCheck',
        component: () => import('@/views/stock-check/index.vue'),
        meta: { title: '盘点管理', icon: 'DataAnalysis' }
      },
      {
        path: 'sys-log',
        name: 'SysLog',
        component: () => import('@/views/sys-log/index.vue'),
        meta: { title: '系统日志', icon: 'Document', roles: ['admin', 'demo'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('userInfo')
  
  if (to.path === '/login') {
    if (userInfo) {
      next('/')
    } else {
      next()
    }
  } else {
    if (userInfo) {
      let user = {}
      try {
        user = JSON.parse(userInfo)
      } catch (error) {
        localStorage.removeItem('userInfo')
        next('/login')
        return
      }
      if (to.meta.roles && !to.meta.roles.includes(user.role)) {
        next('/')
        return
      }
      next()
    } else {
      next('/login')
    }
  }
})

export default router
