<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { useLocaleStore } from '@/stores/locale'
import { useSessionStore } from '@/stores/session'
import type { UserRole } from '@/types/domain'

const session = useSessionStore()
const locale = useLocaleStore()
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
  if (route.query.reason === 'login') return locale.t('请先登录后再访问该业务页面。', 'Please sign in before opening this page.')
  if (route.query.reason === 'role') return locale.t('当前角色无权访问目标页面，请使用匹配角色登录。', 'This role cannot access the requested page.')
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

const roles: Array<{ value: UserRole; label: string; enLabel: string }> = [
  { value: 'FARMER', label: '农户', enLabel: 'Farmer' },
  { value: 'BUYER', label: '买家', enLabel: 'Buyer' },
  { value: 'EXPERT', label: '技术专家', enLabel: 'Expert' },
  { value: 'BANK', label: '银行', enLabel: 'Bank' },
  { value: 'SYSTEM_ADMIN', label: '系统管理员', enLabel: 'Administrator' },
]

const roleDescriptions: Record<UserRole, { zh: string; en: string }> = {
  FARMER: { zh: '发布货源、申请融资、咨询专家。', en: 'Publish products, apply for finance and consult experts.' },
  BUYER: { zh: '采购农产品、管理购物车和订单。', en: 'Purchase products and manage carts and orders.' },
  EXPERT: { zh: '答复问题、处理预约、发布知识。', en: 'Answer questions, manage bookings and publish guidance.' },
  BANK: { zh: '维护贷款产品，审核融资进度。', en: 'Maintain loan products and review applications.' },
  SYSTEM_ADMIN: { zh: '管理用户、交易、融资和内容。', en: 'Manage users, trade, finance and content.' },
}

const authRoles = computed(() =>
  mode.value === 'register' ? roles.filter((role) => role.value !== 'SYSTEM_ADMIN') : roles,
)
const selectedRoleDescription = computed(() => {
  const description = roleDescriptions[form.role]
  return locale.t(description.zh, description.en)
})
const accountLabel = computed(() => accountMode.value === 'netease' ? locale.t('网易邮箱', 'NetEase email') : locale.t('账号', 'Account'))
const accountPlaceholder = computed(() =>
  accountMode.value === 'netease' ? 'name@163.com' : locale.t('请输入用户名', 'Enter username'),
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
      throw new Error(locale.t('请输入有效的 163 网易邮箱。', 'Enter a valid 163.com email address.'))
    }

    if (mode.value === 'login') {
      // 登录不再要求选择角色：后端按账号自动识别身份并返回角色。
      await session.login({ userName, password: form.password })
      message.value = locale.t(`已登录：${session.displayName}（${session.roleLabel}）`, `Signed in as ${session.displayName}.`)
    } else {
      await session.register({ ...form, userName })
      message.value = locale.t(`已注册并登录：${session.displayName}（${session.roleLabel}）`, `Account created for ${session.displayName}.`)
    }
    await router.push(typeof route.query.redirect === 'string' ? route.query.redirect : '/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : locale.t('请求失败，请稍后重试。', 'Request failed. Please try again.')
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
        {{ locale.t('返回首页', 'Back home') }}
      </RouterLink>
      <span class="eyebrow"><AppIcon name="shield" />{{ locale.t('安全入口', 'Secure access') }}</span>
      <h1>{{ locale.t('进入融销通', 'Welcome to Agri-Link') }}</h1>
      <p>{{ locale.t('一个账号连接农户、买家、专家与银行服务。', 'One account connects farmers, buyers, experts and financial services.') }}</p>

      <ul class="auth-benefits">
        <li><AppIcon name="check" /><span><strong>{{ locale.t('身份自动识别', 'Automatic role detection') }}</strong><small>{{ locale.t('登录后按角色开放对应工作台', 'Open the right workspace after sign-in') }}</small></span></li>
        <li><AppIcon name="check" /><span><strong>{{ locale.t('业务进度留痕', 'Traceable workflow') }}</strong><small>{{ locale.t('交易、融资与咨询状态统一管理', 'Manage trade, finance and consultations') }}</small></span></li>
        <li><AppIcon name="check" /><span><strong>{{ locale.t('注册信息精简', 'Simple registration') }}</strong><small>{{ locale.t('仅收集业务所需的必要信息', 'Only essential information is required') }}</small></span></li>
      </ul>
    </div>

    <div class="auth-card" :class="{ 'auth-card--register': mode === 'register' }">
      <div class="tabs" role="tablist" aria-label="登录注册切换">
        <button class="tab" type="button" :aria-selected="mode === 'login'" @click="switchMode('login')">{{ locale.t('登录', 'Sign in') }}</button>
        <button class="tab" type="button" :aria-selected="mode === 'register'" @click="switchMode('register')">{{ locale.t('注册', 'Register') }}</button>
      </div>

      <Transition name="auth-mode" mode="out-in">
        <div :key="mode" class="auth-card__content">
          <div class="auth-card__heading">
            <h2>{{ mode === 'login' ? locale.t('欢迎回来', 'Welcome back') : locale.t('创建您的账号', 'Create your account') }}</h2>
            <p>{{ mode === 'login' ? locale.t('输入账号和密码进入业务工作台。', 'Sign in to open your workspace.') : locale.t('选择身份并补充必要的账号信息。', 'Choose a role and enter the required details.') }}</p>
          </div>

          <form class="form auth-form" @submit.prevent="submit">
            <div class="auth-provider" aria-label="账号类型">
              <button type="button" :aria-selected="accountMode === 'account'" @click="accountMode = 'account'">
                {{ locale.t('普通账号', 'Account') }}
              </button>
              <button type="button" :aria-selected="accountMode === 'netease'" @click="accountMode = 'netease'">
                {{ locale.t('163 邮箱', '163 email') }}
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
              <span>{{ locale.t('密码', 'Password') }}</span>
              <input v-model="form.password" type="password" :autocomplete="mode === 'login' ? 'current-password' : 'new-password'" required :placeholder="locale.t('请输入密码', 'Enter password')" />
            </label>

            <div v-if="mode === 'register'" class="auth-role-row">
              <label class="field">
                <span>{{ locale.t('角色', 'Role') }}</span>
                <select v-model="form.role">
                  <option v-for="role in authRoles" :key="role.value" :value="role.value">{{ locale.isEnglish ? role.enLabel : role.label }}</option>
                </select>
              </label>
              <p class="form-note">{{ selectedRoleDescription }}</p>
            </div>
            <p v-else class="form-note">{{ locale.t('登录后将自动识别您的账号身份，无需手动选择角色。', 'Your role is detected automatically after sign-in.') }}</p>

            <Transition name="auth-extra">
              <div v-if="mode === 'register'" class="auth-extra-grid">
                <label class="field">
                  <span>{{ locale.t('昵称', 'Display name') }}</span>
                  <input v-model.trim="form.nickName" :placeholder="locale.t('平台展示名', 'Name shown on the platform')" />
                </label>
                <label class="field">
                  <span>{{ locale.t('真实姓名', 'Full name') }}</span>
                  <input v-model.trim="form.realName" :placeholder="locale.t('认证可用', 'Used for verification')" />
                </label>
                <label class="field">
                  <span>{{ locale.t('手机号', 'Phone') }} <small>{{ locale.t('选填', 'Optional') }}</small></span>
                  <input v-model.trim="form.phone" type="tel" autocomplete="tel" :placeholder="locale.t('联系方式', 'Contact number')" />
                </label>
                <label class="field">
                  <span>{{ locale.t('地区', 'Location') }} <small>{{ locale.t('选填', 'Optional') }}</small></span>
                  <input v-model.trim="form.address" :placeholder="locale.t('省市区/经营地', 'Province, city or business area')" />
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
              {{ locale.t('打开网易邮箱设置', 'Open NetEase email settings') }}
            </a>

            <button class="button button--green" type="submit" :disabled="loading">
              <AppIcon name="check" />
              {{ loading ? locale.t('提交中', 'Submitting') : mode === 'login' ? locale.t('登录平台', 'Sign in') : locale.t('创建账号', 'Create account') }}
            </button>
          </form>
        </div>
      </Transition>
    </div>
  </section>
</template>
