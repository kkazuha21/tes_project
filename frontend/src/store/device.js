import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'

export const useDeviceStore = defineStore('device', () => {
  // State
  const devices = ref([])
  const currentDevice = ref(null)
  const loading = ref(false)
  
  // Actions
  const fetchDevices = async (filters = {}) => {
    loading.value = true
    try {
      const params = {}
      if (filters.status) params.status = filters.status
      if (filters.categoryId) params.categoryId = filters.categoryId
      
      const response = await api.get('/devices', { params })
      devices.value = response.data
      return { success: true }
    } catch (error) {
      console.error('获取设备列表失败:', error)
      return { success: false, message: '获取设备列表失败' }
    } finally {
      loading.value = false
    }
  }
  
  const fetchDeviceById = async (id) => {
    loading.value = true
    try {
      const response = await api.get(`/devices/${id}`)
      currentDevice.value = response.data
      return { success: true, data: response.data }
    } catch (error) {
      console.error('获取设备详情失败:', error)
      return { success: false, message: '获取设备详情失败' }
    } finally {
      loading.value = false
    }
  }
  
  return {
    devices,
    currentDevice,
    loading,
    fetchDevices,
    fetchDeviceById
  }
})
