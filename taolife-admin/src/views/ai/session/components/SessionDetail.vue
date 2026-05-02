<script setup lang="tsx">
import { useAppStore } from '@/store'
import { useBoolean } from '@/hooks'
import { getSessionDetail } from '@/service/api/ai'
import { NBadge, NButton, NCard, NDescriptions, NDescriptionsItem, NDivider, NEmpty, NGrid, NGi, NScrollbar, NSpace, NSpin, NTag, NText } from 'naive-ui'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'

const appStore = useAppStore()

const props = defineProps<{
  sessionId: string
}>()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)
const { bool: visible, setTrue: open, setFalse: close } = useBoolean(false)

const detailData = ref<Entity.SessionDetailAdminVO | null>(null)

async function loadDetail() {
  if (!props.sessionId) return
  startLoading()
  await getSessionDetail(props.sessionId).then((res) => {
    detailData.value = res.data
    endLoading()
  })
}

watch(() => props.sessionId, () => {
  if (visible.value) {
    loadDetail()
  }
})

defineExpose({
  open: () => {
    open()
    loadDetail()
  },
  close
})

function getRoleTagType(role: number) {
  switch (role) {
    case 1: return 'info'
    case 2: return 'success'
    case 3: return 'warning'
    default: return 'default'
  }
}
</script>

<template>
  <n-drawer v-model:show="visible" :width="1000" placement="right">
    <n-drawer-content title="会话详情" closable @update:show="(val: boolean) => !val && close()">
      <template #header>
        <n-space align="center">
          <span>会话详情</span>
        </n-space>
      </template>

      <n-spin :show="loading" class="drawer-spin">
        <div v-if="detailData" class="drawer-content">
          <n-descriptions :column="2" label-placement="left" bordered>
            <n-descriptions-item label="会话ID" :span="2">
              {{ detailData.session.sessionId }}
            </n-descriptions-item>
            <n-descriptions-item label="用户ID">
              {{ detailData.session.accountId }}
            </n-descriptions-item>
            <n-descriptions-item label="体质">
              {{ detailData.session.constitutionName || '-' }}
            </n-descriptions-item>
            <n-descriptions-item label="模型">
              {{ detailData.session.chatModel || '-' }}
            </n-descriptions-item>
            <n-descriptions-item label="消息数">
              {{ detailData.session.messageCount }}
            </n-descriptions-item>
            <n-descriptions-item label="创建时间">
              {{ detailData.session.createTime }}
            </n-descriptions-item>
            <n-descriptions-item label="最后消息时间">
              {{ detailData.session.lastMessageTime || '-' }}
            </n-descriptions-item>
          </n-descriptions>

          <n-divider>对话内容</n-divider>

          <div v-if="detailData.messages.length === 0">
            <n-empty description="暂无消息" />
          </div>

          <n-scrollbar v-else class="message-scrollbar">
            <n-space vertical :size="16">
              <template v-for="msg in detailData.messages" :key="msg.messageId">
                <!-- AI助手消息：左右对比布局 -->
                <n-grid v-if="msg.role === 2" :cols="2" :x-gap="12" responsive="screen">
                  <n-gi>
                    <n-card :class="`role-card role-${msg.role}`" size="small" :content-style="{ padding: '12px' }">
                      <template #header>
                        <n-space align="center" :size="12">
                          <n-tag :type="getRoleTagType(msg.role)" size="small">
                            {{ msg.roleName }}
                          </n-tag>
                          <n-text depth="3" style="font-size: 12px">
                            {{ msg.createTime }}
                          </n-text>
                        </n-space>
                      </template>
                      <pre style="margin: 0 0 8px 0; white-space: pre-wrap; word-break: break-word; font-size: 14px">{{ msg.content }}</pre>
                      <n-space :size="16" class="msg-meta">
                        <n-text v-if="msg.modelUsed" depth="3" style="font-size: 12px">
                          模型: {{ msg.modelUsed }}
                        </n-text>
                        <n-text v-if="msg.tokensUsed" depth="3" style="font-size: 12px">
                          Tokens: {{ msg.tokensUsed }}
                        </n-text>
                        <n-text v-if="msg.responseTime" depth="3" style="font-size: 12px">
                          耗时: {{ msg.responseTime }}ms
                        </n-text>
                      </n-space>

                    </n-card>
                  </n-gi>
                  <n-gi>
                    <n-card class="role-card role-2-markdown" size="small">
                      <template #header>
                        <n-space align="center" :size="12">
                          <n-tag type="success" size="small">
                            Markdown
                          </n-tag>
                        </n-space>
                      </template>
                      <MdPreview :model-value="msg.content" :theme="appStore.colorMode" />
                    </n-card>
                  </n-gi>
                </n-grid>

                <!-- 用户/系统消息：普通布局 -->
                <n-card v-else :class="`role-card role-${msg.role}`" size="small">
                  <template #header>
                    <n-space align="center" :size="12">
                      <n-tag :type="getRoleTagType(msg.role)" size="small">
                        {{ msg.roleName }}
                      </n-tag>
                      <n-text depth="3" style="font-size: 12px">
                        {{ msg.createTime }}
                      </n-text>
                    </n-space>
                  </template>

                  <template #header-extra>
                    <n-space v-if="msg.role === 1" :size="8">
                      <n-tag v-if="msg.questionTypeName" size="small" type="info">
                        {{ msg.questionTypeName }}
                      </n-tag>
                      <n-badge v-if="msg.isSafe === 0" :value="'风险'" type="error" />
                    </n-space>
                  </template>

                  <pre
                    style="margin: 0; white-space: pre-wrap; word-break: break-word; font-size: 14px">{{ msg.content }}</pre>

                  <template v-if="msg.riskReason" #footer>
                    <n-tag type="error" size="small">
                      风险原因: {{ msg.riskReason }}
                    </n-tag>
                  </template>
                </n-card>
              </template>
            </n-space>
          </n-scrollbar>
        </div>
      </n-spin>

      <template #footer>
        <n-button size="small" @click="close()">关闭</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>
.role-card.role-1 {
  --n-color: var(--n-color-info);
}

.role-card.role-2 {
  --n-color: var(--n-color-success);
}

.role-card.role-3 {
  --n-color: var(--n-color-warning);
}

.role-card.role-2-markdown {
  --n-color: var(--n-color-success);
}

.msg-meta {
  margin: 8px 0;
}

.drawer-spin {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.drawer-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.message-scrollbar {
  flex: 1;
  min-height: 200px;
}

:deep(.md-editor-previewOnly) {
  padding: 12px;
  border-radius: 4px;
  background-color: var(--n-border-color);
  border: 1px solid var(--n-border-color);
}

:deep(.md-editor-preview-wrapper) {
  padding: 12px;
}

:deep(.md-editor-preview) {
  font-size: 14px;
  line-height: 1.6;
}

:deep(.md-editor-preview h1),
:deep(.md-editor-preview h2),
:deep(.md-editor-preview h3),
:deep(.md-editor-preview h4) {
  margin-top: 8px;
  margin-bottom: 4px;
  font-weight: 600;
}

:deep(.md-editor-preview p) {
  margin-bottom: 6px;
}

:deep(.md-editor-preview ul),
:deep(.md-editor-preview ol) {
  padding-left: 20px;
  margin-bottom: 6px;
}

:deep(.md-editor-preview blockquote) {
  margin: 6px 0;
  padding: 6px 12px;
  border-radius: 3px;
}
</style>