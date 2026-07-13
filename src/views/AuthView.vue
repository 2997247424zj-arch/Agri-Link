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
const accountMode = ref<'account' | 'netease'>('account')
const loading = ref(false)
const message = ref('')
const error = ref('')
const neteaseMailUrl =
  'https://mail.163.com/js6/main.jsp?sid=PLCySYVsjxoKLpZwYzwJRwupsYmdGZMV&df=mail163_letter#module=options.LinkModule%7C%7B%22link%22%3A%22option_pop3%22%7D'
// 登录成功后回到路由守卫记录的目标页面。
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

const roleDescriptions: Record<UserRole, string> = {
  FARMER: '发布货源、申请融资、咨询专家。',
  BUYER: '采购农产品、管理购物车和订单。',
  EXPERT: '答复问题、处理预约、发布知识。',
  BANK: '维护贷款产品，审核融资进度。',
  SYSTEM_ADMIN: '管理用户、交易、融资和内容。',
}

const authRoles = computed(() =>
  mode.value === 'register' ? roles.filter((role) => role.value !== 'SYSTEM_ADMIN') : roles,
)
const selectedRoleDescription = computed(() => roleDescriptions[form.role])
const accountLabel = computed(() => (accountMode.value === 'netease' ? '网易邮箱' : '账号'))
const accountPlaceholder = computed(() =>
  accountMode.value === 'netease' ? 'name@163.com' : '请输入用户名',
)
const accountAutocomplete = computed(() => (accountMode.value === 'netease' ? 'email' : 'username'))
const accountType = computed(() => (accountMode.value === 'netease' ? 'email' : 'text'))

function switchMode(nextMode: 'login' | 'register') {
  mode.value = nextMode
  if (nextMode === 'register' && form.role === 'SYSTEM_ADMIN') {
    form.role = 'FARMER'
  }
}

function normalizedUserName() {
  const value = form.userName.trim()
  return accountMode.value === 'netease' ? value.toLowerCase() : value
}

// 登录/注册共用表单，提交时根据模式切换接口。
async function submit() {
  loading.value = true
  message.value = ''
  error.value = ''

  try {
    const userName = normalizedUserName()
    if (accountMode.value === 'netease' && !/^[^\s@]+@163\.com$/i.test(userName)) {
      throw new Error('请输入有效的 163 网易邮箱。')
    }

    if (mode.value === 'login') {
      // 登录不再要求选择角色：后端按账号自动识别身份并返回角色。
      await session.login({ userName, password: form.password })
      message.value = `已登录：${session.displayName}（${session.roleLabel}）`
    } else {
      await session.register({ ...form, userName })
      message.value = `已注册并登录：${session.displayName}（${session.roleLabel}）`
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
      <RouterLink class="auth-back" to="/">
        <AppIcon name="arrow" />
        返回首页
      </RouterLink>
      <span class="eyebrow"><AppIcon name="shield" />安全入口</span>
      <h1>进入融销通</h1>
      <p>一个账号连接农户、买家、专家与银行服务。</p>
      <div class="grid grid--two">
        <div class="metric">
          <strong>4 类</strong>
          <span>业务角色快速进入</span>
        </div>
        <div class="metric">
          <strong>更轻</strong>
          <span>注册只填必要信息</span>
        </div>
      </div>
    </div>

    <div class="auth-card" :class="{ 'auth-card--register': mode === 'register' }">
      <div class="tabs" role="tablist" aria-label="登录注册切换">
        <button class="tab" type="button" :aria-selected="mode === 'login'" @click="switchMode('login')">登录</button>
        <button class="tab" type="button" :aria-selected="mode === 'register'" @click="switchMode('register')">注册</button>
      </div>

      <form class="form auth-form" @submit.prevent="submit">
        <div class="auth-provider" aria-label="账号类型">
          <button type="button" :aria-selected="accountMode === 'account'" @click="accountMode = 'account'">
            普通账号
          </button>
          <button type="button" :aria-selected="accountMode === 'netease'" @click="accountMode = 'netease'">
            163 邮箱
          </button>
        </div>

        <label class="field">
          <span>{{ accountLabel }}</span>
          <input
            v-model.trim="form.userName"
            :autocomplete="accountAutocomplete"
            :placeholder="accountPlaceholder"
            :type="accountType"
            required
          />
        </label>
        <label class="field">
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="current-password" required placeholder="请输入密码" />
        </label>
        <label v-if="mode === 'register'" class="field">
          <span>角色</span>
          <select v-model="form.role">
            <option v-for="role in authRoles" :key="role.value" :value="role.value">{{ role.label }}</option>
          </select>
        </label>
        <p v-if="mode === 'register'" class="form-note">{{ selectedRoleDescription }}</p>
        <p v-else class="form-note">登录后将自动识别您的账号身份，无需手动选择角色。</p>

        <Transition name="auth-extra">
          <div v-if="mode === 'register'" class="auth-extra-grid">
            <label class="field">
              <span>昵称</span>
              <input v-model.trim="form.nickName" placeholder="平台展示名" />
            </label>
            <label class="field">
              <span>真实姓名</span>
              <input v-model.trim="form.realName" placeholder="认证可用" />
            </label>
            <label class="field">
              <span>手机号 <small>选填</small></span>
              <input v-model.trim="form.phone" type="tel" autocomplete="tel" placeholder="联系方式" />
            </label>
            <label class="field">
              <span>地区 <small>选填</small></span>
              <input v-model.trim="form.address" placeholder="省市区/经营地" />
            </label>
          </div>
        </Transition>

        <p v-if="message" class="alert">{{ message }}</p>
        <p v-if="routeNotice" class="alert">{{ routeNotice }}</p>
        <p v-if="error" class="alert alert--error">{{ error }}</p>

        <a
          v-if="accountMode === 'netease'"
          class="auth-mail-link"
          :href="neteaseMailUrl"
          rel="noopener noreferrer"
          target="_blank"
        >
          <AppIcon name="arrow" />
          打开网易邮箱设置
        </a>

        <button class="button button--green" type="submit" :disabled="loading">
          <AppIcon name="check" />
          {{ loading ? '提交中' : mode === 'login' ? '登录平台' : '创建账号' }}
        </button>
      </form>
    </div>
  </section>
</template>
