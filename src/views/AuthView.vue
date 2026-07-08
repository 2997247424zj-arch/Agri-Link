<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { useSessionStore } from '@/stores/session'
import type { UserRole } from '@/types/domain'

const session = useSessionStore()
const route = useRoute()
const router = useRouter()
const mode = ref<'login' | 'register'>('login')
const loading = ref(false)
const message = ref('')
const error = ref('')
// ?????????????? query ????????????
const routeNotice = computed(() => {
  if (route.query.reason === 'login') return '请先登录后再访问该业务页面。'
  if (route.query.reason === 'role') return '当前角色无权访问目标页面，请切换到匹配角色后登录。'
  return ''
})

const form = reactive({
  userName: '',
  password: '',
  nickName: '',
  realName: '',
  phone: '',
  address: '',
  role: 'FARMER' as UserRole,
})

const roles: Array<{ value: UserRole; label: string }> = [
  { value: 'FARMER', label: '农户' },
  { value: 'BUYER', label: '买家' },
  { value: 'EXPERT', label: '技术专家' },
  { value: 'BANK', label: '银行' },
  { value: 'SYSTEM_ADMIN', label: '系统管理员' },
]

// ??/??????????????????????
async function submit() {
  loading.value = true
  message.value = ''
  error.value = ''

  try {
    if (mode.value === 'login') {
      await session.login({ userName: form.userName, password: form.password, role: form.role })
      message.value = `已登录：${session.displayName}（${session.roleLabel}）`
    } else {
      await session.register({ ...form })
      message.value = `注册成功：${session.displayName}（${session.roleLabel}）`
    }
    await router.push(typeof route.query.redirect === 'string' ? route.query.redirect : '/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '请求失败，请稍后重试。'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-copy">
      <span class="eyebrow"><AppIcon name="shield" />角色化访问控制</span>
      <h1>注册与登录入口</h1>
      <p>
        后端使用轻量角色头 `X-User-Role` 控制权限。登录或注册后，前端会保存当前角色，
        用于融资、交易、专家问答和后台管理接口联调。
      </p>
      <div class="grid grid--two">
        <div class="metric">
          <strong>5 类</strong>
          <span>买家、农户、专家、银行、管理员</span>
        </div>
        <div class="metric">
          <strong>统一</strong>
          <span>账号、角色和业务入口</span>
        </div>
      </div>
    </div>

    <div class="auth-card">
      <div class="tabs" role="tablist" aria-label="登录注册切换">
        <button class="tab" type="button" :aria-selected="mode === 'login'" @click="mode = 'login'">登录</button>
        <button class="tab" type="button" :aria-selected="mode === 'register'" @click="mode = 'register'">注册</button>
      </div>

      <form class="form" @submit.prevent="submit">
        <label class="field">
          <span>账号</span>
          <input v-model.trim="form.userName" autocomplete="username" required placeholder="请输入用户名" />
        </label>
        <label class="field">
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="current-password" required placeholder="请输入密码" />
        </label>
        <label class="field">
          <span>角色</span>
          <select v-model="form.role">
            <option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option>
          </select>
        </label>

        <template v-if="mode === 'register'">
          <label class="field">
            <span>昵称</span>
            <input v-model.trim="form.nickName" placeholder="用于平台展示" />
          </label>
          <label class="field">
            <span>真实姓名</span>
            <input v-model.trim="form.realName" placeholder="融资和专家认证可用" />
          </label>
          <label class="field">
            <span>手机号</span>
            <input v-model.trim="form.phone" type="tel" autocomplete="tel" placeholder="请输入联系方式" />
          </label>
          <label class="field">
            <span>地址</span>
            <input v-model.trim="form.address" placeholder="省市区/经营地址" />
          </label>
        </template>

        <p v-if="message" class="alert">{{ message }}</p>
        <p v-if="routeNotice" class="alert">{{ routeNotice }}</p>
        <p v-if="error" class="alert alert--error">{{ error }}</p>

        <button class="button button--green" type="submit" :disabled="loading">
          <AppIcon name="check" />
          {{ loading ? '提交中' : mode === 'login' ? '登录平台' : '创建账号' }}
        </button>
      </form>
    </div>
  </section>
</template>
