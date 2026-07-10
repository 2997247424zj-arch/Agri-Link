<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import type { Expert, Question, Reserve } from '@/types/domain'

const session = useSessionStore()
const route = useRoute()
const router = useRouter()
const experts = ref<Expert[]>([])
const questions = ref<Question[]>([])
const reserves = ref<Reserve[]>([])
const message = ref('')
const error = ref('')
const submitting = ref(false)
const questionAnswers = reactive<Record<number, string>>({})
const reserveAnswers = reactive<Record<number, string>>({})
const expertKeyword = ref('')
const plantFilter = ref('')
const selectedExpert = ref<Expert | null>(null)
const consultModalOpen = ref(false)
type ExpertTab = 'experts' | 'records'
const expertTabs: Array<{ value: ExpertTab; label: string }> = [
  { value: 'experts', label: '专家列表' },
  { value: 'records', label: '我的咨询' },
]

// 专家页同时覆盖专家列表、在线问答和预约服务。
const reserveForm = reactive({
  expertName: '',
  questioner: session.userName || 'farmer-demo',
  area: '20亩',
  address: '湘西州吉首市',
  plantName: '水稻',
  soilCondition: '酸性土壤，排水一般',
  plantCondition: '叶片发黄，局部倒伏',
  plantDetail: '近一周降雨较多，已喷施一次叶面肥。',
  phone: '',
  message: '希望专家线上初诊后安排线下指导。',
  appointmentTime: '',
  serviceMode: '线上诊断',
})

const questionForm = reactive({
  expertName: '',
  questioner: session.userName || 'farmer-demo',
  phone: '',
  plantName: '水稻',
  title: '',
  question: '',
  attachments: [] as string[],
})

const fallbackExperts: Expert[] = [
  {
    userName: 'expert-rice',
    realName: '周明',
    phone: '13800000001',
    profession: '水稻病虫害防治',
    position: '高级农艺师',
    belong: '湘西农业技术站',
  },
  {
    userName: 'expert-fruit',
    realName: '陈岚',
    phone: '13800000002',
    profession: '果树栽培与冷链采后',
    position: '研究员',
    belong: '农业科技推广中心',
  },
  {
    userName: 'expert-brand',
    realName: '李清',
    phone: '13800000003',
    profession: '农产品品牌运营',
    position: '产业顾问',
    belong: '数字农业服务中心',
  },
]

const fallbackQuestions: Question[] = [
  {
    id: 1,
    expertName: 'expert-rice',
    questioner: 'farmer-demo',
    phone: '13800000001',
    plantName: '水稻',
    title: '秧苗叶片发黄如何处理？',
    question: '低洼田块连续降雨后叶片发黄，根系偏弱。',
    answer: '先排水增氧，补充磷钾肥，观察3天后再决定是否用药。',
    attachments: [],
    status: 1,
  },
]

const fallbackReserves: Reserve[] = [
  {
    id: 1,
    expertName: 'expert-rice',
    questioner: 'farmer-demo',
    area: '20亩',
    address: '湘西州吉首市',
    plantName: '水稻',
    soilCondition: '黏土，排水一般',
    plantCondition: '叶片发黄',
    plantDetail: '雨后出现症状，已喷施叶面肥。',
    phone: '13800000001',
    message: '希望安排线上诊断。',
    appointmentTime: '2026-07-09T09:00',
    serviceMode: '线上诊断',
    answer: '',
    status: 0,
  },
]

// 本地兜底数据用于接口不可用时展示完整页面。
const pendingQuestions = computed(() => questions.value.filter((question) => (question.status ?? 0) === 0))
const pendingReserves = computed(() => reserves.value.filter((reserve) => (reserve.status ?? 0) === 0))
const filteredExperts = computed(() => {
  const text = expertKeyword.value.trim().toLowerCase()
  if (!text) return experts.value
  return experts.value.filter((expert) =>
    [expert.realName, expert.userName, expert.profession, expert.position, expert.belong].some((value) =>
      String(value ?? '').toLowerCase().includes(text),
    ),
  )
})
const filteredQuestions = computed(() => {
  const text = plantFilter.value.trim().toLowerCase()
  if (!text) return questions.value
  return questions.value.filter((question) => String(question.plantName ?? '').toLowerCase().includes(text))
})
const filteredReserves = computed(() => {
  const text = plantFilter.value.trim().toLowerCase()
  if (!text) return reserves.value
  return reserves.value.filter((reserve) => String(reserve.plantName ?? '').toLowerCase().includes(text))
})
const activeTab = computed<ExpertTab>(() => (route.query.tab === 'records' ? 'records' : 'experts'))
const isFarmerRole = computed(() => session.role === 'FARMER')
const isExpertRole = computed(() => session.role === 'EXPERT')

function setExpertTab(tab: ExpertTab) {
  router.replace({ query: { ...route.query, tab } })
}

function selectExpert(expert: Expert) {
  reserveForm.expertName = expert.userName
  questionForm.expertName = expert.userName
  selectedExpert.value = expert
  if (isFarmerRole.value) consultModalOpen.value = true
}

function statusLabel(status?: number) {
  if (status === 1) return '已处理'
  if (status === 2) return '已驳回'
  if (status === 3) return '已取消'
  return '待处理'
}

function statusTagClass(status?: number) {
  if (status === 1) return 'tag tag--green'
  if (status === 2 || status === 3) return 'tag tag--red'
  return 'tag tag--amber'
}

// 附件转为 base64，便于演示环境直接提交。
function handleQuestionAttachment(event: Event) {
  const files = [...((event.target as HTMLInputElement).files ?? [])].slice(0, 3)
  questionForm.attachments = []

  for (const file of files) {
    const reader = new FileReader()
    reader.onload = () => {
      questionForm.attachments.push(String(reader.result || ''))
    }
    reader.readAsDataURL(file)
  }
}

// 并行拉取专家、问答和预约记录。
async function loadExperts() {
  error.value = ''
  try {
    const [expertData, questionData, reserveData] = await Promise.all([
      api.get<Expert[]>('/api/experts'),
      api.get<Question[]>('/api/consultation/questions').catch(() => fallbackQuestions),
      api.get<Reserve[]>('/api/consultation/reserves').catch(() => fallbackReserves),
    ])
    experts.value = expertData?.length ? expertData : fallbackExperts
    questions.value = questionData?.length ? questionData : fallbackQuestions
    reserves.value = reserveData?.length ? reserveData : fallbackReserves
    reserveForm.expertName = experts.value[0]?.userName ?? ''
    questionForm.expertName = experts.value[0]?.userName ?? ''
  } catch (err) {
    experts.value = fallbackExperts
    questions.value = fallbackQuestions
    reserves.value = fallbackReserves
    reserveForm.expertName = fallbackExperts[0]?.userName ?? ''
    questionForm.expertName = fallbackExperts[0]?.userName ?? ''
    error.value = err instanceof Error ? `后端暂不可用：${err.message}` : '后端暂不可用，已显示演示专家。'
  }
}

async function submitReserve() {
  submitting.value = true
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<Reserve>('/api/consultation/reserves', reserveForm, { role: 'FARMER' })
    reserves.value = [created, ...reserves.value]
    message.value = '咨询预约已提交，专家端可查看并处理。'
    consultModalOpen.value = false
  } catch (err) {
    error.value = err instanceof Error ? err.message : '预约提交失败。'
  } finally {
    submitting.value = false
  }
}

async function submitQuestion() {
  submitting.value = true
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<Question>('/api/consultation/questions', questionForm, { role: 'FARMER' })
    questions.value = [created, ...questions.value]
    message.value = '问题已发布，等待专家答复。'
    consultModalOpen.value = false
  } catch (err) {
    error.value = err instanceof Error ? err.message : '问题提交失败。'
  } finally {
    submitting.value = false
  }
}

async function answerQuestion(question: Question, status = 1) {
  const id = question.id ?? question.questionId
  if (!id) return
  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<Question>(
      `/api/consultation/questions/${id}/answer`,
      {
        answer: questionAnswers[id] || question.answer || (status === 2 ? '问题信息不足，已驳回补充。' : '已处理，请按专家建议执行。'),
        status,
      },
      { role: 'EXPERT' },
    )
    Object.assign(question, updated)
    message.value = `问答 #${id} 已处理。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '问答处理失败。'
  }
}

async function answerReserve(reserve: Reserve, status = 1) {
  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<Reserve>(
      `/api/consultation/reserves/${reserve.id}/answer`,
      {
        answer: reserveAnswers[reserve.id] || reserve.answer || (status === 2 ? '预约信息不足，已驳回补充。' : '已收到预约，将按电话沟通时间。'),
        status,
      },
      { role: 'EXPERT' },
    )
    Object.assign(reserve, updated)
    message.value = `预约 #${reserve.id} 已处理。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '预约处理失败。'
  }
}

async function cancelReserve(reserve: Reserve) {
  if (typeof window !== 'undefined' && !window.confirm(`确认取消预约 #${reserve.id}？`)) return

  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<Reserve>(
      `/api/consultation/reserves/${reserve.id}/status`,
      { status: 3, answer: reserve.answer || '用户已取消预约。' },
      { role: 'FARMER' },
    )
    Object.assign(reserve, updated)
    message.value = `预约 #${reserve.id} 已取消。`
  } catch {
    reserve.status = 3
    reserve.answer = reserve.answer || '用户已取消预约。'
    message.value = `后端取消预约接口暂不可用，已在当前页面取消预约 #${reserve.id}。`
  }
}

onMounted(loadExperts)
</script>

<template>
  <section class="page">
    <div class="section-title">
      <div>
        <span class="eyebrow"><AppIcon name="expert" />专家助力</span>
        <h2>专家展示、在线问答与咨询预约</h2>
        <p>对接专家资料、问答发布、专家答复和预约处理接口。</p>
      </div>
      <button class="button button--ghost" type="button" @click="loadExperts"><AppIcon name="search" />刷新</button>
    </div>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <div v-if="isFarmerRole" class="tabs module-switcher" role="tablist" aria-label="专家助力操作">
      <button
        v-for="tab in expertTabs"
        :key="tab.value"
        class="tab"
        type="button"
        role="tab"
        :aria-selected="activeTab === tab.value"
        @click="setExpertTab(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="summary-strip">
      <div class="metric">
        <strong>{{ experts.length }}</strong>
        <span>认证专家</span>
      </div>
      <div class="metric">
        <strong>{{ pendingQuestions.length }}</strong>
        <span>待答复问答</span>
      </div>
      <div class="metric">
        <strong>{{ pendingReserves.length }}</strong>
        <span>待处理预约</span>
      </div>
      <div class="metric">
        <strong>{{ session.roleLabel }}</strong>
        <span>当前角色</span>
      </div>
    </div>

    <section class="section">
      <div class="section-title">
        <div>
          <h2>专家筛选</h2>
          <p>按作物、专业方向、机构或姓名快速定位服务对象。</p>
        </div>
        <div class="toolbar">
          <label class="field compact-field">
            <span>专家关键词</span>
            <input v-model.trim="expertKeyword" placeholder="水稻/果树/品牌" />
          </label>
          <label class="field compact-field">
            <span>记录作物</span>
            <input v-model.trim="plantFilter" placeholder="水稻/猕猴桃" />
          </label>
        </div>
      </div>
    </section>

    <section v-if="activeTab === 'experts' || isExpertRole" class="section grid">
      <article
        v-for="expert in filteredExperts"
        :key="expert.userName"
        class="card clickable-card"
        tabindex="0"
        @click="selectExpert(expert)"
        @keydown.enter.prevent="selectExpert(expert)"
      >
        <h3>{{ expert.realName || expert.userName }}</h3>
        <p>{{ expert.profession || '农业综合服务' }}</p>
        <div class="tag-row">
          <span class="tag tag--green">{{ expert.position || '平台专家' }}</span>
          <span class="tag">{{ expert.belong || '认证机构待补充' }}</span>
        </div>
        <div class="card__footer">
          <span>{{ expert.phone || '联系方式待补充' }}</span>
          <button class="button button--ghost" type="button" @click.stop="selectExpert(expert)">
            <AppIcon name="check" />{{ isFarmerRole ? '咨询' : '选择' }}
          </button>
        </div>
      </article>
    </section>

    <section v-if="!isFarmerRole && activeTab === 'experts'" class="section grid grid--two">
      <form class="panel form" @submit.prevent="submitReserve">
        <div class="section-title">
          <div>
            <h2>专家咨询预约</h2>
            <p>填写作物、土壤、症状和联系方式。</p>
          </div>
        </div>
        <label class="field">
          <span>预约专家</span>
          <select v-model="reserveForm.expertName" required>
            <option v-for="expert in experts" :key="expert.userName" :value="expert.userName">
              {{ expert.realName || expert.userName }} - {{ expert.profession || '专家' }}
            </option>
          </select>
        </label>
        <label class="field"><span>农户账号</span><input v-model.trim="reserveForm.questioner" required /></label>
        <label class="field"><span>联系电话</span><input v-model.trim="reserveForm.phone" type="tel" required /></label>
        <label class="field"><span>作物名称</span><input v-model.trim="reserveForm.plantName" required /></label>
        <label class="field"><span>预约时间</span><input v-model="reserveForm.appointmentTime" type="datetime-local" required /></label>
        <label class="field">
          <span>服务方式</span>
          <select v-model="reserveForm.serviceMode">
            <option>线上诊断</option>
            <option>线下指导</option>
            <option>电话沟通</option>
          </select>
        </label>
        <label class="field"><span>种植面积</span><input v-model.trim="reserveForm.area" required /></label>
        <label class="field"><span>地块地址</span><input v-model.trim="reserveForm.address" required /></label>
        <label class="field"><span>土壤情况</span><textarea v-model.trim="reserveForm.soilCondition" required /></label>
        <label class="field"><span>作物情况</span><textarea v-model.trim="reserveForm.plantCondition" required /></label>
        <label class="field"><span>详细描述</span><textarea v-model.trim="reserveForm.plantDetail" required /></label>
        <button class="button button--green" type="submit" :disabled="submitting">
          <AppIcon name="plus" />提交预约
        </button>
      </form>

      <form class="panel form" @submit.prevent="submitQuestion">
        <div class="section-title">
          <div>
            <h2>问答发布</h2>
            <p>用于问答详情和专家答复流程。</p>
          </div>
        </div>
        <label class="field">
          <span>指定专家</span>
          <select v-model="questionForm.expertName" required>
            <option v-for="expert in experts" :key="expert.userName" :value="expert.userName">
              {{ expert.realName || expert.userName }}
            </option>
          </select>
        </label>
        <label class="field"><span>提问人</span><input v-model.trim="questionForm.questioner" required /></label>
        <label class="field"><span>联系电话</span><input v-model.trim="questionForm.phone" type="tel" required /></label>
        <label class="field"><span>作物名称</span><input v-model.trim="questionForm.plantName" required /></label>
        <label class="field"><span>标题</span><input v-model.trim="questionForm.title" required /></label>
        <label class="field"><span>症状图片</span><input type="file" accept="image/*" multiple @change="handleQuestionAttachment" /></label>
        <div v-if="questionForm.attachments.length" class="attachment-grid">
          <img v-for="(image, index) in questionForm.attachments" :key="index" :src="image" alt="症状图片预览" />
        </div>
        <label class="field"><span>问题描述</span><textarea v-model.trim="questionForm.question" required /></label>
        <button class="button" type="submit" :disabled="submitting">
          <AppIcon name="plus" />发布问题
        </button>
      </form>
    </section>

    <section v-if="activeTab === 'records' || isExpertRole" class="section grid grid--two">
      <div class="panel">
        <div class="section-title">
          <div>
            <h2>{{ isExpertRole ? '问答处理' : '我的问答' }}</h2>
            <p>{{ isExpertRole ? '专家可直接答复农户问题。' : '查看已提交问题和专家回复。' }}</p>
          </div>
        </div>
        <div v-if="filteredQuestions.length" class="mini-list">
          <span v-for="question in filteredQuestions" :key="question.id || question.questionId || question.title" class="stack-row">
            <strong>{{ question.title || '农业咨询问题' }}</strong>
            <small>
              {{ question.questioner || question.ownName || '农户' }} · {{ question.plantName || '作物待补充' }}
              <span :class="statusTagClass(question.status)">{{ statusLabel(question.status) }}</span>
            </small>
            <em>{{ question.question || question.content || '暂无详细内容' }}</em>
            <div v-if="question.attachments?.length" class="attachment-grid">
              <img v-for="(image, index) in question.attachments" :key="index" :src="image" alt="症状图片" />
            </div>
            <textarea v-if="isExpertRole && (question.status ?? 0) === 0" v-model="questionAnswers[question.id || question.questionId || 0]" placeholder="填写专家答复" />
            <div v-if="isExpertRole && (question.status ?? 0) === 0" class="toolbar">
              <button class="button button--small" type="button" @click="answerQuestion(question)">答复</button>
              <button class="button button--danger button--small" type="button" @click="answerQuestion(question, 2)">驳回</button>
            </div>
            <em v-else>{{ question.answer || '已处理' }}</em>
          </span>
        </div>
        <div v-else class="empty">暂无符合条件的问答记录。</div>
      </div>

      <div class="panel">
        <div class="section-title">
          <div>
            <h2>{{ isExpertRole ? '预约处理' : '我的预约' }}</h2>
            <p>{{ isExpertRole ? '专家处理线下或线上咨询预约。' : '查看预约状态和专家回复。' }}</p>
          </div>
        </div>
        <div v-if="filteredReserves.length" class="mini-list">
          <span v-for="reserve in filteredReserves" :key="reserve.id" class="stack-row">
            <strong>{{ reserve.plantName }} · {{ reserve.area }}</strong>
            <small>
              {{ reserve.questioner }} · {{ reserve.address }}
              <span :class="statusTagClass(reserve.status)">{{ statusLabel(reserve.status) }}</span>
            </small>
            <small>{{ reserve.serviceMode || '线上诊断' }} · {{ reserve.appointmentTime || '时间待确认' }}</small>
            <em>{{ reserve.plantCondition }}；{{ reserve.plantDetail }}</em>
            <textarea v-if="isExpertRole && (reserve.status ?? 0) === 0" v-model="reserveAnswers[reserve.id]" placeholder="填写预约处理意见" />
            <div v-if="isExpertRole && (reserve.status ?? 0) === 0" class="toolbar">
              <button class="button button--small" type="button" @click="answerReserve(reserve)">处理</button>
              <button class="button button--danger button--small" type="button" @click="answerReserve(reserve, 2)">驳回</button>
            </div>
            <em v-else>{{ reserve.answer || '已处理' }}</em>
          </span>
        </div>
        <div v-else class="empty">暂无符合条件的预约记录。</div>
      </div>
    </section>

    <Transition name="modal-spring">
      <div v-if="consultModalOpen" class="modal-overlay" role="presentation" @click.self="consultModalOpen = false">
        <div class="modal modal--wide" role="dialog" aria-modal="true" aria-label="专家咨询">
          <div class="section-title">
            <div>
              <h2>咨询 {{ selectedExpert?.realName || selectedExpert?.userName || '专家' }}</h2>
              <p>提交后等待专家回复即可。</p>
            </div>
            <button class="button button--ghost button--small" type="button" @click="consultModalOpen = false">关闭</button>
          </div>
          <div class="grid grid--two">
            <form class="form" @submit.prevent="submitReserve">
              <h3>咨询预约</h3>
              <label class="field"><span>农户账号</span><input v-model.trim="reserveForm.questioner" required /></label>
              <label class="field"><span>联系电话</span><input v-model.trim="reserveForm.phone" type="tel" required /></label>
              <label class="field"><span>作物名称</span><input v-model.trim="reserveForm.plantName" required /></label>
              <label class="field"><span>预约时间</span><input v-model="reserveForm.appointmentTime" type="datetime-local" required /></label>
              <label class="field">
                <span>服务方式</span>
                <select v-model="reserveForm.serviceMode">
                  <option>线上诊断</option>
                  <option>线下指导</option>
                  <option>电话沟通</option>
                </select>
              </label>
              <label class="field"><span>地块地址</span><input v-model.trim="reserveForm.address" required /></label>
              <label class="field"><span>咨询信息</span><textarea v-model.trim="reserveForm.plantDetail" required /></label>
              <button class="button button--green" type="submit" :disabled="submitting">
                <AppIcon name="plus" />提交预约
              </button>
            </form>
            <form class="form" @submit.prevent="submitQuestion">
              <h3>在线问答</h3>
              <label class="field"><span>提问人</span><input v-model.trim="questionForm.questioner" required /></label>
              <label class="field"><span>联系电话</span><input v-model.trim="questionForm.phone" type="tel" required /></label>
              <label class="field"><span>作物名称</span><input v-model.trim="questionForm.plantName" required /></label>
              <label class="field"><span>标题</span><input v-model.trim="questionForm.title" required /></label>
              <label class="field"><span>症状图片</span><input type="file" accept="image/*" multiple @change="handleQuestionAttachment" /></label>
              <div v-if="questionForm.attachments.length" class="attachment-grid">
                <img v-for="(image, index) in questionForm.attachments" :key="index" :src="image" alt="症状图片预览" />
              </div>
              <label class="field"><span>问题描述</span><textarea v-model.trim="questionForm.question" required /></label>
              <button class="button" type="submit" :disabled="submitting">
                <AppIcon name="plus" />发布问题
              </button>
            </form>
          </div>
        </div>
      </div>
    </Transition>
  </section>
</template>
