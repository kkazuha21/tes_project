<template>
  <div class="category-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="32"><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalCategories }}</div>
              <div class="stat-label">分类总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon :size="32"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalDevices }}</div>
              <div class="stat-label">设备总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon :size="32"><CollectionTag /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ avgDevicesPerCategory }}</div>
              <div class="stat-label">平均设备数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <el-form :inline="true">
        <el-form-item label="搜索">
          <el-input
            v-model="searchText"
            placeholder="分类名称"
            clearable
            style="width: 200px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 分类列表 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <span class="card-title">分类列表</span>
      </template>
      
      <el-table 
        :data="filteredCategories" 
        v-loading="loading" 
        stripe 
        style="width: 100%"
      >
        <el-table-column prop="id" label="分类ID" width="100" />
        
        <el-table-column label="分类名称" min-width="180">
          <template #default="{ row }">
            <div class="category-cell">
              <el-icon :size="20" style="color: #409EFF;">
                <CollectionTag />
              </el-icon>
              <span class="category-name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        
        <el-table-column label="设备数量" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.deviceCount || 0 }} 台</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="viewDevices(row)"
              >
                查看设备
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(row)"
                :disabled="row.deviceCount > 0"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="categoryForm" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="分类描述">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 设备列表对话框 -->
    <el-dialog
      v-model="devicesDialogVisible"
      :title="`${currentCategory?.name} - 设备列表`"
      width="800px"
    >
      <el-table :data="categoryDevices" stripe max-height="400">
        <el-table-column prop="id" label="设备ID" width="80" />
        <el-table-column prop="name" label="设备名称" min-width="150" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'AVAILABLE' ? 'success' : 'warning'" size="small">
              {{ row.status === 'AVAILABLE' ? '可用' : row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/services/api'

const loading = ref(false)
const categories = ref([])
const searchText = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const categoryForm = ref({
  id: null,
  name: '',
  description: ''
})

const devicesDialogVisible = ref(false)
const currentCategory = ref(null)
const categoryDevices = ref([])

// 统计数据
const totalCategories = computed(() => categories.value.length)
const totalDevices = computed(() => categories.value.reduce((sum, cat) => sum + (cat.deviceCount || 0), 0))
const avgDevicesPerCategory = computed(() => {
  if (totalCategories.value === 0) return 0
  return Math.round(totalDevices.value / totalCategories.value * 10) / 10
})

// 过滤分类
const filteredCategories = computed(() => {
  if (!searchText.value) return categories.value
  
  const text = searchText.value.toLowerCase()
  return categories.value.filter(cat => 
    cat.name.toLowerCase().includes(text) || 
    (cat.description && cat.description.toLowerCase().includes(text))
  )
})

const loadCategories = async () => {
  loading.value = true
  try {
    const response = await api.get('/categories')
    categories.value = response.data
  } catch (error) {
    ElMessage.error('加载分类列表失败')
    console.error('Load categories error:', error)
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  dialogTitle.value = '添加分类'
  categoryForm.value = {
    id: null,
    name: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (category) => {
  dialogTitle.value = '编辑分类'
  categoryForm.value = { ...category }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!categoryForm.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  try {
    if (categoryForm.value.id) {
      await api.put(`/categories/${categoryForm.value.id}`, categoryForm.value)
      ElMessage.success('更新成功')
    } else {
      await api.post('/categories', categoryForm.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCategories()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('Save category error:', error)
  }
}

const handleDelete = async (category) => {
  if (category.deviceCount > 0) {
    ElMessage.warning('该分类下还有设备，无法删除')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${category.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await api.delete(`/categories/${category.id}`)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('Delete category error:', error)
    }
  }
}

const viewDevices = async (category) => {
  currentCategory.value = category
  try {
    const response = await api.get(`/devices?categoryId=${category.id}`)
    categoryDevices.value = response.data
    devicesDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载设备列表失败')
    console.error('Load devices error:', error)
  }
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-management {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.toolbar-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.category-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-name {
  font-weight: 500;
  color: #303133;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
}
</style>
