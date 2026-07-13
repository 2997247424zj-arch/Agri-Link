<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import AppImage from '@/components/AppImage.vue'
import PageHeader from '@/components/ui/PageHeader.vue'
import ModuleTabs from '@/components/ui/ModuleTabs.vue'
import SummaryStrip from '@/components/ui/SummaryStrip.vue'
import { api, resolveAssetUrl, uploadImage } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import type { Expert, Knowledge, Question, Reserve } from '@/types/domain'

const session = useSessionStore()
const route = useRoute()
const router = useRouter()
const experts = ref<Expert[]>([])
const questions = ref<Question[]>([])
const reserves = ref<Reserve[]>([])
const knowledgeList = ref<Knowledge[]>([])
const message = ref('')
const error = ref('')
const submitting = ref(false)
const uploadingImages = ref(false)
const questionAnswers = reactive<Record<number, string>>({})
const reserveAnswers = reactive<Record<number, string>>({})
const expertKeyword = ref('')
const plantFilter = ref('')
const selectedExpert = ref<Expert | null>(null)
const consultModalOpen = ref(false)
type ExpertTab = 'experts' | 'records' | 'knowledge'
const expertTabs = computed<Array<{ value: ExpertTab; label: string }>>(() =>
  isExpertRole.value
    ? [
        { value: 'records', label: '咨询工单' },
        { value: 'knowledge', label: '知识发布' },
      ]
    : [
        { value: 'experts', label: '专家列表' },
        { value: 'records', label: '我的咨询' },
      ],
)

const knowledgeForm = reactive({
  title: '',
  content: '',
  picPath: '',
  ownName: session.userName || 'expert-demo',
})

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
  // 专家端只展示本人资料，不能浏览其他专家信息。
  if (isExpertRole.value) return experts.value.filter((expert) => expert.userName === session.userName)
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
  const me = session.userName
  return questions.value.filter((question) => {
    // 专家只看指派给自己的工单；农户只看自己提交的问答。
    if (isExpertRole.value && question.expertName !== me) return false
    if (isFarmerRole.value && (question.questioner ?? question.ownName) !== me) return false
    if (!text) return true
    return String(question.plantName ?? '').toLowerCase().includes(text)
  })
})
const filteredReserves = computed(() => {
  const text = plantFilter.value.trim().toLowerCase()
  const me = session.userName
  return reserves.value.filter((reserve) => {
    if (isExpertRole.value && reserve.expertName !== me) return false
    if (isFarmerRole.value && reserve.questioner !== me) return false
    if (!text) return true
    return String(reserve.plantName ?? '').toLowerCase().includes(text)
  })
})
const myKnowledge = computed(() =>
  knowledgeList.value.filter((item) => (item.ownName || item.userName) === (session.userName || 'expert-demo')),
)
const activeTab = computed<ExpertTab>(() => {
  if (isExpertRole.value) return route.query.tab === 'knowledge' ? 'knowledge' : 'records'
  return route.query.tab === 'records' ? 'records' : 'experts'
})
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

function legacyOrderImageSrc(src?: string) {
  if (!src) return ''
  return resolveAssetUrl(src.startsWith('/') || src.startsWith('http') || src.startsWith('data:') ? src : `/file/order/${src}`)
}

async function handleQuestionAttachment(event: Event) {
  const input = event.target as HTMLInputElement
  const files = [...(input.files ?? [])].slice(0, 3)
  if (!files.length) return

  uploadingImages.value = true
  message.value = ''
  error.value = ''
  try {
    const uploaded = await Promise.all(files.map((file) => uploadImage(file, 'FARMER')))
    questionForm.attachments = uploaded.map((item) => item.url)
    message.value = `已上传 ${uploaded.length} 张症状图片，可提交咨询。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '症状图片上传失败。'
  } finally {
    uploadingImages.value = false
    input.value = ''
  }
}

async function handleKnowledgeImage(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  uploadingImages.value = true
  message.value = ''
  error.value = ''
  try {
    const uploaded = await uploadImage(file, 'EXPERT')
    knowledgeForm.picPath = uploaded.url
    message.value = `配图「${uploaded.originalName}」已上传，可发布知识。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '知识配图上传失败。'
  } finally {
    uploadingImages.value = false
    input.value = ''
  }
}

// 并行拉取专家、问答和预约记录。
async function loadExperts() {
  error.value = ''
  try {
    const [expertData, questionData, reserveData, knowledgeData] = await Promise.all([
      api.get<Expert[]>('/api/experts'),
      api.get<Question[]>('/api/consultation/questions').catch(() => fallbackQuestions),
      api.get<Reserve[]>('/api/consultation/reserves').catch(() => fallbackReserves),
      api.get<Knowledge[]>('/api/knowledge').catch(() => []),
    ])
    experts.value = expertData?.length ? expertData : fallbackExperts
    questions.value = questionData?.length ? questionData : fallbackQuestions
    reserves.value = reserveData?.length ? reserveData : fallbackReserves
    knowledgeList.value = knowledgeData ?? []
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

async function publishKnowledge() {
  submitting.value = true
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<Knowledge>('/api/knowledge', knowledgeForm, { role: 'EXPERT' })
    knowledgeList.value = [created, ...knowledgeList.value]
    knowledgeForm.title = ''
    knowledgeForm.content = ''
    knowledgeForm.picPath = ''
    message.value = '农业知识已发布。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '农业知识发布失败。'
  } finally {
    submitting.value = false
  }
}

async function deleteKnowledge(item: Knowledge) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除知识「${item.title}」？`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/knowledge/${item.knowledgeId}`, { role: 'EXPERT' })
    knowledgeList.value = knowledgeList.value.filter((knowledge) => knowledge.knowledgeId !== item.knowledgeId)
    message.value = '农业知识已删除。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '农业知识删除失败。'
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
    <PageHeader eyebrow="专家助力" icon="expert" title="专家展示、在线问答与咨询预约" desc="对接专家资料、问答发布、专家答复和预约处理接口。">
      <template #actions>
        <button class="button button--ghost" type="button" @click="loadExperts"><AppIcon name="search" />刷新</button>
      </template>
    </PageHeader>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <ModuleTabs
      :model-value="activeTab"
      :options="expertTabs"
      aria-label="专家助力操作"
      @update:model-value="setExpertTab"
    />

    <SummaryStrip
      :items="[
        { value: experts.length, label: '认证专家' },
        { value: pendingQuestions.length, label: '待答复问答' },
        { value: pendingReserves.length, label: '待处理预约' },
        { value: session.roleLabel, label: '当前角色' },
      ]"
    />

    <section class="section">
      <div class="section-title">
        <div>
          <h2>{{ isExpertRole ? '我的服务工单' : '专家筛选' }}</h2>
          <p>{{ isExpertRole ? '下方直接呈现农户向您提交的问答与预约，可按作物快速定位。' : '按作物、专业方向、机构或姓名快速定位服务对象。' }}</p>
        </div>
        <div class="toolbar">
          <label v-if="!isExpertRole" class="field compact-field">
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

    <section v-if="activeTab === 'experts'" class="section grid">
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
          <button v-if="isFarmerRole" class="button button--ghost" type="button" @click.stop="selectExpert(expert)">
            <AppIcon name="check" />咨询
          </button>
        </div>
      </article>
    </section>

    <section v-if="activeTab === 'records'" class="section grid grid--two">
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
              <AppImage v-for="image in question.attachments" :key="image" :src="legacyOrderImageSrc(image)" fallback-src="/file/order/W020230811400645740814_ORIGIN.jpg" alt="症状图片" ratio="4 / 3" icon="leaf" />
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

    <section v-if="activeTab === 'knowledge'" class="section grid grid--two">
      <form class="panel form" @submit.prevent="publishKnowledge">
        <div class="section-title">
          <div><h2>发布农业知识</h2><p>将咨询经验沉淀为农技文章，发布后同步展示在平台资讯。</p></div>
        </div>
        <label class="field"><span>标题</span><input v-model.trim="knowledgeForm.title" required /></label>
        <label class="field"><span>配图路径</span><input v-model.trim="knowledgeForm.picPath" placeholder="可选，例如 /file/info/example.jpg" /></label>
        <label class="field"><span>{{ uploadingImages ? '图片上传中' : '从电脑上传配图' }}</span><input type="file" accept="image/*" :disabled="uploadingImages" @change="handleKnowledgeImage" /></label>
        <div v-if="knowledgeForm.picPath" class="image-preview">
          <img :src="resolveAssetUrl(knowledgeForm.picPath)" alt="知识配图预览" />
        </div>
        <label class="field"><span>正文</span><textarea v-model.trim="knowledgeForm.content" required /></label>
        <button class="button button--green" type="submit" :disabled="submitting || uploadingImages">
          <AppIcon name="plus" />{{ submitting ? '发布中' : '发布知识' }}
        </button>
      </form>
      <div class="panel">
        <div class="section-title"><div><h2>我的知识</h2><p>共 {{ myKnowledge.length }} 篇。</p></div></div>
        <div v-if="myKnowledge.length" class="mini-list">
          <span v-for="item in myKnowledge" :key="item.knowledgeId" class="stack-row">
            <AppImage v-if="item.picPath || item.picture" :src="legacyOrderImageSrc(item.picPath || item.picture)" fallback-src="/file/order/12be19984e374bcfbf06561571365d07.jpg" :alt="item.title" ratio="16 / 8" icon="leaf" />
            <strong>{{ item.title }}</strong>
            <small><span :class="item.status === 2 ? 'tag tag--red' : 'tag tag--green'">{{ item.status === 2 ? '已下架' : '已发布' }}</span></small>
            <em>{{ item.content || '暂无正文' }}</em>
            <div class="toolbar"><button class="button button--danger button--small" type="button" @click="deleteKnowledge(item)">删除</button></div>
          </span>
        </div>
        <div v-else class="empty">暂未发布农业知识。</div>
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
              <label class="field"><span>农户账号</span><input v-model.trim="reserveForm.questioner" disabled /></label>
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
              <label class="field"><span>提问人</span><input v-model.trim="questionForm.questioner" disabled /></label>
              <label class="field"><span>联系电话</span><input v-model.trim="questionForm.phone" type="tel" required /></label>
              <label class="field"><span>作物名称</span><input v-model.trim="questionForm.plantName" required /></label>
              <label class="field"><span>标题</span><input v-model.trim="questionForm.title" required /></label>
              <label class="field"><span>{{ uploadingImages ? '图片上传中' : '症状图片（最多 3 张）' }}</span><input type="file" accept="image/*" multiple :disabled="uploadingImages" @change="handleQuestionAttachment" /></label>
              <div v-if="questionForm.attachments.length" class="attachment-grid">
                <AppImage v-for="image in questionForm.attachments" :key="image" :src="legacyOrderImageSrc(image)" fallback-src="/file/order/W020230811400645740814_ORIGIN.jpg" alt="症状图片预览" ratio="4 / 3" icon="leaf" />
              </div>
              <label class="field"><span>问题描述</span><textarea v-model.trim="questionForm.question" required /></label>
              <button class="button" type="submit" :disabled="submitting || uploadingImages">
                <AppIcon name="plus" />发布问题
              </button>
            </form>
          </div>
        </div>
      </div>
    </Transition>
  </section>
</template>
