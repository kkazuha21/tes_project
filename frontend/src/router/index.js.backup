import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

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
        component: () => import('@/views/Admin/AdminDashboard.vue')
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
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminOverview',
        component: () => import('@/views/Admin/AdminDashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/Admin/AdminDashboard.vue')
      },
      {
        path: 'devices',
        name: 'AdminDevices',
        component: () => import('@/views/DeviceList.vue')
      },
      {
        path: 'bookings',
        name: 'AdminBookings',
        component: () => import('@/views/Admin/AdminDashboard.vue')
      },
      {
        path: 'tickets',
        name: 'AdminTickets',
        component: () => import('@/views/Admin/TicketManagement.vue')
      },
      {
        path: 'categories',
        name: 'CategoryManagement',
        component: () => import('@/views/Admin/AdminDashboard.vue')
      },
      {
        path: 'logs',
        name: 'SystemLogs',
        component: () => import('@/views/Admin/AdminDashboard.vue')
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
  const authStore = useAuthStore()

  // 需要认证的路由
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // 已登录用户访问登录页，根据角色重定向
  if (to.path === '/login' && authStore.isAuthenticated) {
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

  // 检查路由角色权限（只在父路由检查，不影响子路由）
  if (to.matched.length > 0) {
    const parentRoute = to.matched[0]
    if (parentRoute.meta.role && authStore.user?.role !== parentRoute.meta.role) {
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
  }

  next()
})

export default router