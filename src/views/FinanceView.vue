<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import type { Bank, Finance, FinancingIntention } from '@/types/domain'

const session = useSessionStore()
const banks = ref<Bank[]>([])
const matches = ref<Bank[]>([])
const farmerMatches = ref<FinancingIntention[]>([])
const applications = ref<Finance[]>([])
const intentions = ref<FinancingIntention[]>([])
const loading = ref(true)
const submitting = ref(false)
const message = ref('')
const error = ref('')
const applicationStatusFilter = ref<'all' | '0' | '1' | '2'>('all')
const reviewRemarks = reactive<Record<number, string>>({})
const expandedFinanceId = ref<number | null>(null)
const materialInputs = reactive<Record<number, string>>({})

// 表单字段与后端 FinanceApplicationRequest 保持一致。
const applicationForm = reactive({
  bankId: 1001,
  ownName: session.userName || 'farmer-demo',
  realName: '',
  phone: '',
  idNum: '',
  money: 50000,
  rate: 4.2,
  repayment: '按季付息',
  remark: '',
})

const intentionForm = reactive({
  userName: session.userName || 'farmer-demo',
  realName: '',
  address: '湘西州吉首市',
  amount: 80000,
  application: '春耕备货与周转资金',
  item: '高山大米订单',
  repaymentPeriod: '12个月',
  area: '80亩',
  phone: '',
})

const fallbackBanks: Bank[] = [
  {
    bankId: 1001,
    bankName: '乡村振兴信用贷',
    introduce: '适合种养殖经营周转，授信快，材料简化。',
    bankPhone: '0743-000001',
    money: 200000,
    rate: 3.8,
    repayment: '按季付息',
  },
  {
    bankId: 1002,
    bankName: '订单收益贷',
    introduce: '以农产品订单和合作社信用作为辅助依据。',
    bankPhone: '0743-000002',
    money: 500000,
    rate: 4.2,
    repayment: '到期还本',
  },
  {
    bankId: 1003,
    bankName: '农机设备贷',
    introduce: '支持采购农机、冷链与仓储设备。',
    bankPhone: '0743-000003',
    money: 300000,
    rate: 4.6,
    repayment: '等额本息',
  },
]

const fallbackIntentions: FinancingIntention[] = [
  {
    id: 1,
    userName: 'farmer-demo',
    realName: '示范农户',
    address: '湘西州吉首市',
    amount: 80000,
    application: '春耕备货与周转资金',
    item: '高山大米订单',
    repaymentPeriod: '12个月',
    area: '80亩',
    phone: '13800000001',
  },
]

const sortedBanks = computed(() => [...banks.value].sort((a, b) => (a.rate ?? 99) - (b.rate ?? 99)))
const pendingApplications = computed(() => applications.value.filter((item) => (item.status ?? 0) === 0))
const filteredApplications = computed(() => {
  if (applicationStatusFilter.value === 'all') return applications.value
  return applications.value.filter((item) => String(item.status ?? 0) === applicationStatusFilter.value)
})

function financeStatusLabel(status?: number) {
  if (status === 1) return '已通过'
  if (status === 2) return '已拒绝'
  return '待审批'
}

function financeStatusClass(status?: number) {
  if (status === 1) return 'tag tag--green'
  if (status === 2) return 'tag tag--red'
  return 'tag tag--amber'
}

function financeTimeline(status?: number) {
  return [
    { label: '已提交', active: true, done: true },
    { label: '银行受理', active: status === 0 || status === 1 || status === 2, done: status === 1 || status === 2 },
    { label: status === 2 ? '已拒绝' : '审批通过', active: status === 1 || status === 2, done: status === 1 },
  ]
}

// 融资状态在农户、银行和管理员视角中复用。
function materialList(item: Finance) {
  const fromInput = materialInputs[item.financeId]
  if (fromInput) return fromInput.split(/[\n,，]/).map((text) => text.trim()).filter(Boolean)
  return item.materials?.length ? item.materials : ['身份证明', '经营流水或订单凭证', '土地/基地经营证明']
}

// 材料字段以数组编辑，提交前由后端转为换行文本。
function repaymentPlan(item: Finance) {
  if (item.repaymentPlan?.length) return item.repaymentPlan

  const total = item.money ?? 0
  const periods = 3
  const amount = Math.round((total / periods) * 100) / 100
  return Array.from({ length: periods }, (_, index) => ({
    period: `第 ${index + 1} 期`,
    amount,
    dueDate: `放款后 ${index + 1} 个周期`,
    status: index === 0 ? '待还款' : '未开始',
  }))
}

async function saveMaterials(item: Finance) {
  message.value = ''
  error.value = ''
  const materials = materialList(item)
  try {
    const updated = await api.patch<Finance>(
      `/api/finance/applications/${item.financeId}/materials`,
      { materials },
      { role: 'FARMER' },
    )
    item.materials = updated.materials ?? materials
    message.value = `融资申请 #${item.financeId} 材料已更新。`
  } catch {
    item.materials = materials
    message.value = `后端材料接口暂不可用，已在当前页面保存申请 #${item.financeId} 的材料清单。`
  }
}

// 加载银行产品、融资申请和融资意向。
async function loadFinance() {
  loading.value = true
  error.value = ''
  try {
    const [bankData, applicationData, intentionData] = await Promise.all([
      api.get<Bank[]>('/api/finance/banks'),
      api.get<Finance[]>('/api/finance/applications').catch(() => []),
      api.get<FinancingIntention[]>('/api/finance/intentions').catch(() => fallbackIntentions),
    ])
    banks.value = bankData?.length ? bankData : fallbackBanks
    applicationForm.bankId = banks.value[0]?.bankId ?? 1001
    applications.value = applicationData ?? []
    intentions.value = intentionData?.length ? intentionData : fallbackIntentions
  } catch (err) {
    banks.value = fallbackBanks
    intentions.value = fallbackIntentions
    error.value = err instanceof Error ? `后端暂不可用：${err.message}` : '后端暂不可用，已显示演示数据。'
  } finally {
    loading.value = false
  }
}

async function loadMatches() {
  message.value = ''
  error.value = ''
  try {
    const data = await api.get<Bank[]>(`/api/finance/banks/matches?amount=${applicationForm.money}`, {
      role: 'FARMER',
    })
    matches.value = data?.length ? data : sortedBanks.value.slice(0, 2)
    message.value = '已根据申请金额匹配可用银行产品。'
  } catch (err) {
    matches.value = sortedBanks.value.slice(0, 2)
    error.value = err instanceof Error ? `智能匹配接口暂不可用：${err.message}` : '智能匹配接口暂不可用。'
  }
}

async function loadFarmerMatches() {
  message.value = ''
  error.value = ''
  try {
    const data = await api.get<FinancingIntention[]>(
      `/api/finance/matches/farmers/${applicationForm.bankId}`,
      { role: 'BANK' },
    )
    farmerMatches.value = data?.length ? data : intentions.value.slice(0, 3)
    message.value = '已按银行产品匹配潜在融资农户。'
  } catch (err) {
    farmerMatches.value = intentions.value.slice(0, 3)
    error.value = err instanceof Error ? `农户匹配接口暂不可用：${err.message}` : '农户匹配接口暂不可用。'
  }
}

async function submitApplication() {
  submitting.value = true
  message.value = ''
  error.value = ''
  try {
    await api.post<Finance>('/api/finance/applications', applicationForm, { role: 'FARMER' })
    message.value = '融资申请已提交，银行端可在审批管理中处理。'
    await loadFinance()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '融资申请提交失败。'
  } finally {
    submitting.value = false
  }
}

async function submitIntention() {
  submitting.value = true
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<FinancingIntention>('/api/finance/intentions', intentionForm, {
      role: 'FARMER',
    })
    intentions.value = [created, ...intentions.value]
    message.value = '融资意向已登记，银行端可用于农户匹配。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '融资意向登记失败。'
  } finally {
    submitting.value = false
  }
}

async function updateApplicationStatus(item: Finance, status: number) {
  message.value = ''
  error.value = ''
  try {
    const remark =
      reviewRemarks[item.financeId] || item.remark || (status === 1 ? '银行审批通过' : '银行审批拒绝')
    const updated = await api.patch<Finance>(
      `/api/finance/applications/${item.financeId}/status`,
      { status, remark },
      { role: 'BANK' },
    )
    item.status = updated.status
    item.remark = updated.remark
    reviewRemarks[item.financeId] = ''
    message.value = `融资申请 #${item.financeId} 状态已更新。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '融资状态更新失败。'
  }
}

onMounted(loadFinance)
</script>

<template>
  <section class="page">
    <div class="section-title">
      <div>
        <span class="eyebrow"><AppIcon name="bank" />融资服务</span>
        <h2>融资产品、意向匹配与审批</h2>
        <p>对接银行产品、融资申请、融资意向和银行端审批接口。</p>
      </div>
      <div class="toolbar">
        <button class="button" type="button" @click="loadMatches"><AppIcon name="search" />按金额匹配</button>
        <button class="button button--ghost" type="button" @click="loadFarmerMatches">
          <AppIcon name="expert" />匹配农户
        </button>
      </div>
    </div>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <div class="summary-strip">
      <div class="metric">
        <strong>{{ banks.length }}</strong>
        <span>{{ loading ? '正在加载产品' : '银行产品' }}</span>
      </div>
      <div class="metric">
        <strong>{{ intentions.length }}</strong>
        <span>融资意向</span>
      </div>
      <div class="metric">
        <strong>{{ pendingApplications.length }}</strong>
        <span>待审批申请</span>
      </div>
      <div class="metric">
        <strong>{{ session.roleLabel }}</strong>
        <span>当前角色</span>
      </div>
    </div>

    <section class="section">
      <div class="grid">
        <article v-for="bank in sortedBanks" :key="bank.bankId" class="card">
          <h3>{{ bank.bankName }}</h3>
          <p>{{ bank.introduce || '暂无银行产品介绍' }}</p>
          <div class="tag-row">
            <span class="tag tag--green">额度 {{ bank.money ? `${bank.money} 元` : '面议' }}</span>
            <span class="tag">年化 {{ bank.rate ?? '-' }}%</span>
            <span class="tag tag--amber">{{ bank.repayment || '还款方式面议' }}</span>
          </div>
          <div class="card__footer">
            <span>{{ bank.bankPhone || '联系电话待补充' }}</span>
            <button class="button button--ghost" type="button" @click="applicationForm.bankId = bank.bankId">
              <AppIcon name="check" />选择
            </button>
          </div>
        </article>
      </div>
    </section>

    <section class="section grid grid--two">
      <form class="panel form" @submit.prevent="submitApplication">
        <div class="section-title">
          <div>
            <h2>提交融资申请</h2>
            <p>农户侧申请，银行端或后台后续审批。</p>
          </div>
        </div>
        <label class="field">
          <span>银行产品</span>
          <select v-model.number="applicationForm.bankId">
            <option v-for="bank in banks" :key="bank.bankId" :value="bank.bankId">{{ bank.bankName }}</option>
          </select>
        </label>
        <label class="field"><span>申请人账号</span><input v-model.trim="applicationForm.ownName" required /></label>
        <label class="field"><span>真实姓名</span><input v-model.trim="applicationForm.realName" required /></label>
        <label class="field"><span>手机号</span><input v-model.trim="applicationForm.phone" type="tel" required /></label>
        <label class="field"><span>身份证号</span><input v-model.trim="applicationForm.idNum" required /></label>
        <label class="field"><span>申请金额</span><input v-model.number="applicationForm.money" type="number" min="1000" required /></label>
        <label class="field"><span>还款方式</span><input v-model.trim="applicationForm.repayment" required /></label>
        <label class="field"><span>经营说明</span><textarea v-model.trim="applicationForm.remark" placeholder="种植规模、订单情况、资金用途" /></label>
        <button class="button button--green" type="submit" :disabled="submitting">
          <AppIcon name="plus" />{{ submitting ? '提交中' : '提交申请' }}
        </button>
      </form>

      <form class="panel form" @submit.prevent="submitIntention">
        <div class="section-title">
          <div>
            <h2>融资意向登记</h2>
            <p>让银行端基于额度、用途和经营信息主动匹配农户。</p>
          </div>
        </div>
        <label class="field"><span>账号</span><input v-model.trim="intentionForm.userName" required /></label>
        <label class="field"><span>姓名</span><input v-model.trim="intentionForm.realName" required /></label>
        <label class="field"><span>联系电话</span><input v-model.trim="intentionForm.phone" type="tel" /></label>
        <label class="field"><span>经营地址</span><input v-model.trim="intentionForm.address" required /></label>
        <label class="field"><span>意向金额</span><input v-model.number="intentionForm.amount" type="number" min="1" required /></label>
        <label class="field"><span>经营面积</span><input v-model.trim="intentionForm.area" /></label>
        <label class="field"><span>资金用途</span><textarea v-model.trim="intentionForm.application" /></label>
        <button class="button" type="submit" :disabled="submitting">
          <AppIcon name="plus" />登记意向
        </button>
      </form>
    </section>

    <section class="section grid grid--two">
      <div class="panel">
        <div class="section-title">
          <div>
            <h2>匹配结果</h2>
            <p>展示银行产品或银行端农户匹配结果。</p>
          </div>
        </div>
        <div v-if="matches.length || farmerMatches.length" class="mini-list">
          <span v-for="bank in matches" :key="bank.bankId">{{ bank.bankName }} · 年化 {{ bank.rate ?? '-' }}%</span>
          <span v-for="farmer in farmerMatches" :key="farmer.id">{{ farmer.realName }} · {{ farmer.amount }} 元 · {{ farmer.item || farmer.application }}</span>
        </div>
        <div v-else class="empty">点击匹配按钮后展示推荐结果。</div>
      </div>

      <div class="panel">
        <div class="section-title">
          <div>
            <h2>申请审批</h2>
            <p>银行端处理融资申请状态。</p>
          </div>
          <label class="field compact-field">
            <span>状态筛选</span>
            <select v-model="applicationStatusFilter">
              <option value="all">全部申请</option>
              <option value="0">待审批</option>
              <option value="1">已通过</option>
              <option value="2">已拒绝</option>
            </select>
          </label>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>编号</th>
                <th>申请人</th>
                <th>金额</th>
                <th>状态</th>
                <th>审批进度</th>
                <th>审批备注</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="item in filteredApplications" :key="item.financeId">
                <tr>
                  <td>{{ item.financeId }}</td>
                  <td>{{ item.realName || item.ownName }}</td>
                  <td>{{ item.money ?? '-' }}</td>
                  <td><span :class="financeStatusClass(item.status)">{{ financeStatusLabel(item.status) }}</span></td>
                  <td>
                    <div class="compact-timeline">
                      <span
                        v-for="step in financeTimeline(item.status)"
                        :key="step.label"
                        :class="{ 'is-active': step.active, 'is-done': step.done }"
                      >
                        {{ step.label }}
                      </span>
                    </div>
                  </td>
                  <td>
                    <input
                      v-model.trim="reviewRemarks[item.financeId]"
                      class="inline-remark"
                      :placeholder="item.remark || '填写审批意见'"
                    />
                  </td>
                  <td class="toolbar">
                    <button class="button button--small" type="button" @click="expandedFinanceId = expandedFinanceId === item.financeId ? null : item.financeId">详情</button>
                    <button class="button button--small" type="button" @click="updateApplicationStatus(item, 1)">通过</button>
                    <button class="button button--danger button--small" type="button" @click="updateApplicationStatus(item, 2)">拒绝</button>
                  </td>
                </tr>
                <tr v-if="expandedFinanceId === item.financeId">
                  <td colspan="7">
                    <div class="detail-panel">
                      <div class="grid grid--two">
                        <div>
                          <h3>申请详情</h3>
                          <p>申请账号：{{ item.ownName }}</p>
                          <p>联系电话：{{ item.phone || '待补充' }}</p>
                          <p>还款方式：{{ item.repayment || '待确认' }}</p>
                          <p>审批备注：{{ item.remark || '暂无' }}</p>
                        </div>
                        <div>
                          <h3>补充材料</h3>
                          <textarea v-model="materialInputs[item.financeId]" :placeholder="materialList(item).join('，')" />
                          <button class="button button--small" type="button" @click="saveMaterials(item)">保存材料</button>
                        </div>
                      </div>
                      <div class="table-wrap">
                        <table>
                          <thead><tr><th>期次</th><th>金额</th><th>到期说明</th><th>状态</th></tr></thead>
                          <tbody>
                            <tr v-for="plan in repaymentPlan(item)" :key="plan.period">
                              <td>{{ plan.period }}</td>
                              <td>￥{{ plan.amount }}</td>
                              <td>{{ plan.dueDate }}</td>
                              <td>{{ plan.status }}</td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </td>
                </tr>
              </template>
              <tr v-if="!filteredApplications.length">
                <td colspan="7">暂无符合条件的融资申请记录。</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </section>
</template>
