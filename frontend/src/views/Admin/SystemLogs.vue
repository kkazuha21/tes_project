<template>
  <div class="system-logs">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalLogs }}</div>
              <div class="stat-label">日志总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ errorCount }}</div>
              <div class="stat-label">错误日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);">
              <el-icon :size="32"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ warningCount }}</div>
              <div class="stat-label">警告日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);">
              <el-icon :size="32"><InfoFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ infoCount }}</div>
              <div class="stat-label">信息日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选工具栏 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="日志级别">
          <el-select v-model="filters.level" placeholder="全部" clearable style="width: 120px" @change="loadLogs">
            <el-option label="错误" value="ERROR" />
            <el-option label="警告" value="WARNING" />
            <el-option label="信息" value="INFO" />
            <el-option label="调试" value="DEBUG" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="日志类型">
          <el-select v-model="filters.type" placeholder="全部" clearable style="width: 150px" @change="loadLogs">
            <el-option label="用户操作" value="USER_ACTION" />
            <el-option label="系统事件" value="SYSTEM_EVENT" />
            <el-option label="API调用" value="API_CALL" />
            <el-option label="数据库操作" value="DATABASE" />
            <el-option label="认证授权" value="AUTH" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 380px"
            @change="loadLogs"
          />
        </el-form-item>
        
        <el-form-item label="搜索">
          <el-input
            v-model="searchText"
            placeholder="日志内容"
            clearable
            style="width: 200px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="loadLogs">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilters">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">系统日志</span>
          <el-button size="small" @click="exportLogs">
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="filteredLogs" 
        v-loading="loading" 
        stripe 
        style="width: 100%"
        :default-sort="{ prop: 'timestamp', order: 'descending' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">
              {{ row.level }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)" size="small" effect="plain">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="message" label="日志内容" min-width="300" show-overflow-tooltip />
        
        <el-table-column prop="user" label="用户" width="120" />
        
        <el-table-column prop="ip" label="IP地址" width="140" />
        
        <el-table-column label="时间" width="160" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.timestamp) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[20, 50, 100]"
        :total="totalLogs"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center;"
        @current-change="loadLogs"
        @size-change="loadLogs"
      />
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentLog">
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="日志级别">
          <el-tag :type="getLevelType(currentLog.level)">{{ currentLog.level }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="日志类型">
          <el-tag :type="getTypeColor(currentLog.type)" effect="plain">{{ getTypeText(currentLog.type) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作用户">{{ currentLog.user }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ formatDateTime(currentLog.timestamp) }}</el-descriptions-item>
        <el-descriptions-item label="日志内容" :span="2">{{ currentLog.message }}</el-descriptions-item>
        <el-descriptions-item label="详细信息" :span="2">
          <pre class="log-detail">{{ currentLog.detail || '无详细信息' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const logs = ref([])
const filters = ref({
  level: null,
  type: null
})
const dateRange = ref(null)
const searchText = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const detailDialogVisible = ref(false)
const currentLog = ref(null)

// 统计数据
const totalLogs = computed(() => logs.value.length)
const errorCount = computed(() => logs.value.filter(log => log.level === 'ERROR').length)
const warningCount = computed(() => logs.value.filter(log => log.level === 'WARNING').length)
const infoCount = computed(() => logs.value.filter(log => log.level === 'INFO').length)

// 过滤日志
const filteredLogs = computed(() => {
  let result = logs.value
  
  if (searchText.value) {
    const text = searchText.value.toLowerCase()
    result = result.filter(log => 
      log.message.toLowerCase().includes(text) ||
      (log.user && log.user.toLowerCase().includes(text))
    )
  }
  
  return result
})

const loadLogs = async () => {
  loading.value = true
  try {
    // TODO: 调用实际API
    // 模拟日志数据
    logs.value = generateMockLogs(100)
  } catch (error) {
    ElMessage.error('加载日志失败')
    console.error('Load logs error:', error)
  } finally {
    loading.value = false
  }
}

const generateMockLogs = (count) => {
  const levels = ['ERROR', 'WARNING', 'INFO', 'DEBUG']
  const types = ['USER_ACTION', 'SYSTEM_EVENT', 'API_CALL', 'DATABASE', 'AUTH']
  const users = ['admin', 'teacher1', 'student1', 'student2', 'system']
  const messages = [
    '用户登录成功',
    '设备预约提交',
    '预约审核通过',
    '设备状态更新',
    '数据库连接异常',
    'API调用超时',
    '权限验证失败',
    '用户注销',
    '设备维修工单创建'
  ]
  
  const mockLogs = []
  for (let i = 1; i <= count; i++) {
    const level = levels[Math.floor(Math.random() * levels.length)]
    mockLogs.push({
      id: i,
      level: level,
      type: types[Math.floor(Math.random() * types.length)],
      message: messages[Math.floor(Math.random() * messages.length)],
      user: users[Math.floor(Math.random() * users.length)],
      ip: `192.168.1.${Math.floor(Math.random() * 255)}`,
      timestamp: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
      detail: level === 'ERROR' ? 'Error stack trace:\n  at ...\n  at ...' : null
    })
  }
  return mockLogs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
}

const resetFilters = () => {
  filters.value = {
    level: null,
    type: null
  }
  dateRange.value = null
  searchText.value = ''
  currentPage.value = 1
  loadLogs()
}

const viewDetail = (log) => {
  currentLog.value = log
  detailDialogVisible.value = true
}

const exportLogs = () => {
  ElMessage.success('正在导出日志...')
  // TODO: 实现日志导出功能
}

const getLevelType = (level) => {
  const typeMap = {
    'ERROR': 'danger',
    'WARNING': 'warning',
    'INFO': 'success',
    'DEBUG': 'info'
  }
  return typeMap[level] || 'info'
}

const getTypeColor = (type) => {
  const colorMap = {
    'USER_ACTION': 'primary',
    'SYSTEM_EVENT': 'success',
    'API_CALL': 'warning',
    'DATABASE': 'danger',
    'AUTH': 'info'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type) => {
  const textMap = {
    'USER_ACTION': '用户操作',
    'SYSTEM_EVENT': '系统事件',
    'API_CALL': 'API调用',
    'DATABASE': '数据库',
    'AUTH': '认证授权'
  }
  return textMap[type] || type
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.system-logs {
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

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.log-detail {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>