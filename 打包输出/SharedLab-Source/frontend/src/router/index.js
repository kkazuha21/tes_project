import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { h } from 'vue'

// 强制重新加载 - 2025-11-09 02:43

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  // 学生路由
  {
    path: '/student',
    component: () => import('@/views/StudentDashboard.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
    redirect: '/student/devices',
    children: [
      {
        path: 'devices',
        name: 'StudentDevices',
        component: () => import('@/views/DeviceList.vue')
      },
      {
        path: 'devices/:id',
        name: 'StudentDeviceDetail',
        component: () => import('@/views/DeviceDetail.vue')
      },
      {
        path: 'bookings',
        name: 'StudentBookings',
        component: () => import('@/views/MyBookings.vue')
      },
      {
        path: 'tickets',
        name: 'StudentTickets',
        component: () => import('@/views/MyTickets.vue')
      }
    ]
  },
  // 教师路由
  {
    path: '/teacher',
    component: () => import('@/views/TeacherDashboard.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
    redirect: '/teacher/devices',
    children: [
      {
        path: 'devices',
        name: 'TeacherDevices',
        component: () => import('@/views/DeviceList.vue')
      },
      {
        path: 'devices/:id',
        name: 'TeacherDeviceDetail',
        component: () => import('@/views/DeviceDetail.vue')
      },
      {
        path: 'bookings',
        name: 'TeacherBookingManagement',
        component: () => import('@/views/Admin/BookingManagement.vue')
      },
      {
        path: 'tickets',
        name: 'TeacherTicketManagement',
        component: () => import('@/views/Admin/TicketManagement.vue')
      },
      {
        path: 'my-bookings',
        name: 'TeacherMyBookings',
        component: () => import('@/views/MyBookings.vue')
      }
    ]
  },
  // 管理员路由
  {
    path: '/admin',
    component: () => import('@/views/AdminDashboard.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminOverview',
        component: () => import('@/views/Admin/AdminDashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/Admin/UserManagement.vue')
      },
      {
        path: 'devices',
        name: 'AdminDevices',
        component: () => import('@/views/DeviceList.vue')
      },
      {
        path: 'devices/:id',
        name: 'AdminDeviceDetail',
        component: () => import('@/views/DeviceDetail.vue')
      },
      {
        path: 'bookings',
        name: 'AdminBookings',
        component: () => import('@/views/Admin/BookingManagement.vue')
      },
      {
        path: 'tickets',
        name: 'AdminTickets',
        component: () => import('@/views/Admin/TicketManagement.vue')
      },
      {
        path: 'categories',
        name: 'CategoryManagement',
        component: () => import('@/views/Admin/CategoryManagement.vue')
      },
      {
        path: 'logs',
        name: 'SystemLogs',
        component: () => import('@/views/Admin/SystemLogs.vue')
      }
    ]
  },
  // 旧路由重定向（兼容性）
  {
    path: '/',
    redirect: to => {
      const authStore = useAuthStore()
      if (!authStore.isAuthenticated) return '/login'
      const role = authStore.user?.role
      if (role === 'ADMIN') return '/admin/dashboard'
      if (role === 'TEACHER') return '/teacher/devices'
      return '/student/devices'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log('🔍 路由守卫:', {
    to: to.path,
    from: from.path,
    toName: to.name
  })
  
  const authStore = useAuthStore()

  // 需要认证的路由
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    console.log('❌ 未认证，跳转登录')
    next('/login')
    return
  }

  // 已登录用户访问登录页，根据角色重定向
  if (to.path === '/login' && authStore.isAuthenticated) {
    console.log('✅ 已登录访问登录页，重定向')
    const role = authStore.user?.role
    if (role === 'ADMIN') {
      next('/admin/dashboard')
    } else if (role === 'TEACHER') {
      next('/teacher/devices')
    } else {
      next('/student/devices')
    }
    return
  }

  // 检查路由角色权限 - 修复:检查整个路由链中的 role
  const requiredRole = to.matched.find(record => record.meta.role)?.meta.role
  console.log('🔐 角色检查:', {
    requiredRole,
    userRole: authStore.user?.role,
    matched: to.matched.map(r => ({ path: r.path, role: r.meta.role }))
  })
  
  if (requiredRole && authStore.user?.role !== requiredRole) {
    console.log('❌ 角色不匹配，重定向到 dashboard')
    // 如果角色不匹配，重定向到对应角色的首页
    const role = authStore.user?.role
    if (role === 'ADMIN') {
      next('/admin/dashboard')
    } else if (role === 'TEACHER') {
      next('/teacher/devices')
    } else {
      next('/student/devices')
    }
    return
  }

  console.log('✅ 守卫通过，允许导航到:', to.path)
  next()
})

export default router