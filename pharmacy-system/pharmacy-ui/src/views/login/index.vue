<template>
  <div class="login-container">
    <div class="ambient ambient-left"></div>
    <div class="ambient ambient-right"></div>
    <div class="wave-line"></div>

    <section class="login-card">
      <div class="brand-icon">
        <el-icon><DataBoard /></el-icon>
      </div>
      <h1>管理系统</h1>
      <p class="subtitle">高效 · 安全 · 稳定</p>

      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model.trim="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <div class="login-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-button link type="primary" :loading="hintLoading" @click="handleForgotPassword">
            忘记密码
          </el-button>
        </div>

        <el-button class="login-button" type="primary" size="large" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="security-note">
        <el-icon><Lock /></el-icon>
        <span>数据安全保障中</span>
      </div>
    </section>

    <el-dialog v-model="hintDialogVisible" title="密码提示" width="360px" align-center>
      <p class="hint-content">{{ passwordHint }}</p>
      <template #footer>
        <el-button type="primary" @click="hintDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>

    <footer class="site-footer">
      <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">
        冀ICP备2026018313号
      </a>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataBoard, Lock, User } from '@element-plus/icons-vue'
import { getPasswordHint, login } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const hintLoading = ref(false)
const rememberMe = ref(false)
const hintDialogVisible = ref(false)
const passwordHint = ref('')

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    rememberMe.value = true
  }
})

watch(rememberMe, (checked) => {
  if (!checked) {
    localStorage.removeItem('rememberedUsername')
  }
})

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await login(loginForm)
      if (res.code === 200) {
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        if (rememberMe.value) {
          localStorage.setItem('rememberedUsername', loginForm.username)
        }
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error(res.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error('登录失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  })
}

const handleForgotPassword = async () => {
  if (!loginForm.username) {
    ElMessage.warning('请先输入用户名')
    return
  }

  hintLoading.value = true
  try {
    const res = await getPasswordHint(loginForm.username)
    if (res.code === 200) {
      passwordHint.value = res.data || '该账号暂未配置找回密码提示，请联系管理员处理。'
      hintDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取密码提示失败')
    }
  } catch (error) {
    ElMessage.error('获取密码提示失败: ' + (error.message || '未知错误'))
  } finally {
    hintLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100vw;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at 12% 18%, rgba(92, 168, 255, 0.24) 0, rgba(92, 168, 255, 0) 28%),
    radial-gradient(circle at 84% 72%, rgba(45, 126, 246, 0.18) 0, rgba(45, 126, 246, 0) 32%),
    linear-gradient(135deg, #f8fbff 0%, #eef6ff 48%, #dcecff 100%);
  padding: 24px;
}

.ambient {
  position: absolute;
  border-radius: 50%;
  filter: blur(2px);
  pointer-events: none;
}

.ambient-left {
  left: -160px;
  top: -120px;
  width: 430px;
  height: 430px;
  background: rgba(72, 151, 248, 0.14);
}

.ambient-right {
  right: -210px;
  bottom: -160px;
  width: 520px;
  height: 520px;
  background: rgba(48, 127, 237, 0.15);
}

.wave-line {
  position: absolute;
  right: -8%;
  bottom: -10%;
  width: 68%;
  height: 44%;
  border: 1px solid rgba(64, 158, 255, 0.13);
  border-radius: 50%;
  transform: rotate(-8deg);
}

.login-card {
  position: relative;
  z-index: 1;
  width: min(420px, 100%);
  padding: 42px 40px 30px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(214, 231, 255, 0.92);
  border-radius: 8px;
  box-shadow: 0 22px 52px rgba(39, 103, 185, 0.16);
  text-align: center;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  margin-bottom: 20px;
  color: #fff;
  font-size: 36px;
  background: linear-gradient(135deg, #35a7ff 0%, #1f74ed 100%);
  border-radius: 18px;
  box-shadow: 0 14px 28px rgba(31, 116, 237, 0.28);
}

h1 {
  margin: 0;
  color: #17233d;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 0;
}

.subtitle {
  margin: 10px 0 34px;
  color: #7a8aa0;
  font-size: 14px;
}

.login-form {
  text-align: left;
}

.login-form :deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 6px;
  box-shadow: 0 0 0 1px #d9e6f7 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset, 0 6px 18px rgba(64, 158, 255, 0.14);
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -2px 0 22px;
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #1f74ed 100%);
  border: 0;
  box-shadow: 0 12px 22px rgba(31, 116, 237, 0.24);
}

.security-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 28px;
  color: #7b8da8;
  font-size: 13px;
}

.site-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 16px;
  z-index: 1;
  display: flex;
  justify-content: center;
  padding: 0 24px;
  color: #6f8199;
  font-size: 13px;
}

.site-footer a {
  color: inherit;
  text-decoration: none;
}

.site-footer a:hover {
  color: #1f74ed;
}

.hint-content {
  margin: 0;
  color: #303133;
  line-height: 1.7;
  word-break: break-word;
}

@media (max-width: 480px) {
  .login-container {
    padding: 18px;
  }

  .login-card {
    padding: 34px 24px 26px;
  }

  h1 {
    font-size: 25px;
  }
}
</style>
