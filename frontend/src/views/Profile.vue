<template>
  <div class="profile-page">
    <el-main class="main-content">
      <div class="page-header">
        <h2 class="page-title">我的信息</h2>
      </div>

      <el-card shadow="never" class="profile-card" v-loading="loading">
        <el-form :model="form" label-width="90px">
          <el-form-item label="用户名" required>
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>

          <el-form-item label="邮箱" required>
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>

          <el-divider />

          <el-form-item label="旧密码" required>
            <el-input
              v-model="passwordForm.currentPassword"
              type="password"
              show-password
              placeholder="请输入旧密码"
            />
          </el-form-item>

          <el-form-item label="新密码" required>
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              show-password
              placeholder="请输入新密码"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">修改密码</el-button>
          </el-form-item>

          <el-alert
            title="提示"
            type="info"
            :closable="false"
            description="如果你修改了用户名，当前登录状态可能会失效，保存后需要重新登录。"
            show-icon
          />
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const changingPassword = ref(false)

const original = reactive({
  username: authStore.user?.username || '',
  email: authStore.user?.email || ''
})

const form = reactive({
  username: authStore.user?.username || '',
  email: authStore.user?.email || ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: ''
})

const loadProfile = async () => {
  loading.value = true
  try {
    const res = await api.get('/profile/me')
    const data = res.data || {}

    original.username = data.username || ''
    original.email = data.email || ''

    form.username = original.username
    form.email = original.email
  } catch (e) {
    console.error('加载个人信息失败:', e)
    ElMessage.error('加载个人信息失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  form.username = original.username
  form.email = original.email
}

const handleSave = async () => {
  if (!form.username?.trim() || !form.email?.trim()) {
    ElMessage.warning('请填写用户名和邮箱')
    return
  }

  saving.value = true
  try {
    const payload = {
      username: form.username.trim(),
      email: form.email.trim()
    }

    const res = await api.put('/profile/me', payload)
    const updated = res.data || {}

    const oldUsername = authStore.user?.username
    const newUsername = updated.username

    // 更新本地展示
    authStore.updateLocalUser({
      username: updated.username,
      email: updated.email
    })

    ElMessage.success('保存成功')

    // 如果改了用户名：JWT 的 subject 可能失效，强制重新登录
    if (oldUsername && newUsername && oldUsername !== newUsername) {
      ElMessage.info('用户名已修改，请重新登录')
      authStore.logout()
      router.push('/login')
      return
    }

    original.username = updated.username || payload.username
    original.email = updated.email || payload.email
  } catch (e) {
    console.error('保存个人信息失败:', e)
    const msg = e.response?.data?.error || e.response?.data?.message || '保存失败'
    ElMessage.error(msg)
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordForm.currentPassword?.trim() || !passwordForm.newPassword?.trim()) {
    ElMessage.warning('请填写旧密码和新密码')
    return
  }
  if (passwordForm.newPassword.trim().length < 6) {
    ElMessage.warning('新密码长度至少为6个字符')
    return
  }

  changingPassword.value = true
  try {
    await api.put('/profile/me/password', {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功，请重新登录')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    authStore.logout()
    router.push('/login')
  } catch (e) {
    console.error('修改密码失败:', e)
    const msg = e.response?.data?.message || e.response?.data?.error || '修改密码失败'
    ElMessage.error(msg)
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-page {
  width: 100%;
  height: 100%;
}

.main-content {
  padding: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.profile-card {
  max-width: 520px;
}
</style>
