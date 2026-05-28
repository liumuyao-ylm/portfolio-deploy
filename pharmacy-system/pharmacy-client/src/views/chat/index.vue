<template>
  <div class="page chat-page">
    <aside class="panel quick-panel">
      <h1 class="panel-title">AI 客服</h1>
      <p class="muted">常见问题可直接点击咨询，具体病情和用药剂量请咨询药师或医生。</p>
      <div class="question-list">
        <el-button
          v-for="question in quickQuestions"
          :key="question"
          plain
          @click="sendQuestion(question)"
        >
          {{ question }}
        </el-button>
      </div>
    </aside>

    <section class="panel chat-panel">
      <div ref="messageListRef" class="message-list">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-row"
          :class="message.role"
        >
          <div class="avatar">
            <el-icon v-if="message.role === 'assistant'"><Service /></el-icon>
            <el-icon v-else><User /></el-icon>
          </div>
          <div class="bubble">{{ message.content }}</div>
        </div>
      </div>

      <div class="input-bar">
        <el-input
          v-model="inputMessage"
          :rows="2"
          maxlength="300"
          show-word-limit
          type="textarea"
          placeholder="请输入想咨询的问题"
          @keydown.enter.exact.prevent="sendQuestion()"
        />
        <el-button type="primary" :icon="Promotion" :loading="sending" @click="sendQuestion()">
          发送
        </el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Promotion, Service, User } from '@element-plus/icons-vue'
import { sendClientChat } from '@/api/client-chat'

const quickQuestions = [
  '药店营业时间是几点？',
  '药店地址在哪里？',
  '联系电话是多少？',
  '处方药怎么买？',
  '会员消费记录在哪里看？',
  '布洛芬还有货吗？'
]

const inputMessage = ref('')
const sending = ref(false)
const messageListRef = ref(null)
const messages = ref([
  {
    id: Date.now(),
    role: 'assistant',
    content: '您好，我是安心乡镇药房 AI 客服。可以帮您查询营业时间、地址电话、处方药购买、会员记录和药品库存入口。'
  }
])

const scrollToBottom = async () => {
  await nextTick()
  const el = messageListRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

const appendMessage = (role, content) => {
  messages.value.push({
    id: `${Date.now()}-${messages.value.length}`,
    role,
    content
  })
}

const sendQuestion = async (question) => {
  const content = (question || inputMessage.value).trim()
  if (!content || sending.value) return

  inputMessage.value = ''
  appendMessage('user', content)
  sending.value = true
  await scrollToBottom()

  try {
    const { data } = await sendClientChat(content)
    if (data.code !== 200) {
      throw new Error(data.message || 'AI 客服暂时不可用')
    }
    appendMessage('assistant', data.data?.reply || '已收到您的问题，请稍后再试。')
  } catch (error) {
    appendMessage('assistant', 'AI 客服暂时不可用，请稍后再试。')
    ElMessage.error('AI 客服暂时不可用，请稍后再试')
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}
</script>

<style scoped>
.chat-page {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 20px;
  min-height: calc(100vh - 164px);
}

.quick-panel,
.chat-panel {
  min-height: 620px;
}

.quick-panel {
  align-self: start;
}

.quick-panel .muted {
  margin: 0 0 18px;
  line-height: 1.7;
}

.question-list {
  display: grid;
  gap: 12px;
}

.question-list .el-button {
  justify-content: flex-start;
  width: 100%;
  height: 44px;
  margin: 0;
  overflow: hidden;
  text-align: left;
}

.chat-panel {
  display: grid;
  grid-template-rows: minmax(0, 1fr) auto;
  gap: 18px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  max-height: 560px;
  overflow-y: auto;
  padding: 4px 6px 4px 0;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar {
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  color: var(--client-primary);
  background: #eef5ff;
  font-size: 20px;
}

.message-row.user .avatar {
  color: #ffffff;
  background: var(--client-primary);
}

.bubble {
  max-width: min(680px, calc(100% - 64px));
  padding: 13px 15px;
  border: 1px solid var(--client-border);
  border-radius: 8px;
  color: var(--client-text);
  background: var(--client-surface-soft);
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-row.user .bubble {
  color: #ffffff;
  border-color: var(--client-primary);
  background: var(--client-primary);
}

.input-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 108px;
  gap: 12px;
  align-items: stretch;
  padding-top: 16px;
  border-top: 1px solid var(--client-border);
}

.input-bar .el-button {
  height: 64px;
}

@media (max-width: 960px) {
  .chat-page {
    grid-template-columns: 1fr;
  }

  .quick-panel,
  .chat-panel {
    min-height: auto;
  }

  .question-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .question-list,
  .input-bar {
    grid-template-columns: 1fr;
  }

  .input-bar .el-button {
    height: 44px;
  }
}
</style>
