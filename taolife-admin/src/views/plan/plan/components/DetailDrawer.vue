<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { getUserPlanDetail } from '@/service'
import { useAppStore } from '@/store'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css' // oxlint-disable-line no-unassigned-import

const appStore = useAppStore()
const { bool: visible, setTrue: show } = useBoolean(false)
const loading = ref(false)
const detail = ref<Entity.UserPlanDetail>({})

type PlanTabKey = 'food' | 'exercise' | 'acupoint' | 'meridian' | 'lifestyle'

const planTabs: Array<{ key: PlanTabKey, label: string }> = [
  { key: 'food', label: '饮食方案' },
  { key: 'exercise', label: '运动方案' },
  { key: 'acupoint', label: '穴位方案' },
  { key: 'meridian', label: '经络方案' },
  { key: 'lifestyle', label: '生活方案' },
]

const activeTab = ref<PlanTabKey>('food')

const contentMap = computed(() => ({
  food: { content: detail.value.foodPlanContent, tags: detail.value.foodPlanTags },
  exercise: { content: detail.value.exercisePlanContent, tags: detail.value.exercisePlanTags },
  acupoint: { content: detail.value.acupointPlanContent, tags: detail.value.acupointPlanTags },
  meridian: { content: detail.value.meridianPlanContent, tags: detail.value.meridianPlanTags },
  lifestyle: { content: detail.value.lifestylePlanContent, tags: detail.value.lifestylePlanTags },
}))

function parseTags(tags?: string): string[] {
  if (!tags) return []
  try {
    const parsed = JSON.parse(tags)
    if (Array.isArray(parsed)) return parsed.filter(Boolean)
  } catch {
    // 非JSON格式，按逗号分隔
  }
  return tags.split(/[,，]/).map((t: string) => t.trim()).filter(Boolean)
}

const tagList = computed(() => parseTags(contentMap.value[activeTab.value]?.tags))

const statusMap: Record<number, { label: string, type: 'success' | 'info' | 'warning' | 'error' }> = {
  1: { label: '进行中', type: 'success' },
  2: { label: '已完成', type: 'info' },
  3: { label: '已过期', type: 'warning' },
  4: { label: '已终止', type: 'error' },
}

async function loadDetail(id: string) {
  loading.value = true
  const res = await getUserPlanDetail(id)
  detail.value = res.data
  activeTab.value = 'food'
  loading.value = false
}

function open(planId: string) {
  show()
  loadDetail(planId)
}

defineExpose({ open })
</script>

<template>
  <n-drawer v-model:show="visible" :width="800">
    <n-drawer-content :title="detail.planTitle || '方案详情'">
      <n-spin :show="loading">
        <n-descriptions :column="3" label-placement="left" bordered>
          <n-descriptions-item label="用户ID">{{ detail.accountId }}</n-descriptions-item>
          <n-descriptions-item label="体质">{{ detail.constitutionName }}</n-descriptions-item>
          <n-descriptions-item label="季节">{{ detail.seasonName }}</n-descriptions-item>
          <n-descriptions-item label="状态">
            <n-tag v-if="statusMap[detail.status ?? 0]" :type="statusMap[detail.status ?? 0].type" size="small">
              {{ statusMap[detail.status ?? 0].label }}
            </n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="完成率">{{ ((detail.completionRate ?? 0) * 100).toFixed(0) }}%</n-descriptions-item>
          <n-descriptions-item label="任务">{{ detail.completedTasks ?? 0 }}/{{ detail.totalTasks ?? 0
            }}</n-descriptions-item>
          <n-descriptions-item label="评分">{{ detail.userRating ?? '-' }}</n-descriptions-item>
          <n-descriptions-item label="调整次数">{{ detail.adjustmentCount ?? 0 }}</n-descriptions-item>
          <n-descriptions-item label="AI调整">{{ detail.aiAdjustmentCount ?? 0 }}</n-descriptions-item>
          <n-descriptions-item label="开始日期">{{ detail.startDate ?? '-' }}</n-descriptions-item>
          <n-descriptions-item label="结束日期">{{ detail.endDate ?? '-' }}</n-descriptions-item>
          <n-descriptions-item label="周期">{{ detail.cycleDays ? `${detail.cycleDays}天` : '-' }}</n-descriptions-item>
          <n-descriptions-item label="用户反馈" :span="3">{{ detail.userFeedback || '-' }}</n-descriptions-item>
          <n-descriptions-item label="用户备注" :span="3">{{ detail.userNotes || '-' }}</n-descriptions-item>
        </n-descriptions>

        <n-divider>方案内容</n-divider>

        <n-tabs v-model:value="activeTab" type="line">
          <n-tab v-for="tab in planTabs" :key="tab.key" :name="tab.key">
            {{ tab.label }}
          </n-tab>
        </n-tabs>
        <div class="mt-12px">
          <template v-if="contentMap[activeTab]?.content">
            <n-space v-if="tagList.length" size="small" class="mb-8px">
              <n-tag v-for="tag in tagList" :key="tag" size="small" type="primary" round>
                {{ tag }}
              </n-tag>
            </n-space>
            <MdPreview :model-value="contentMap[activeTab]!.content!" :theme="appStore.colorMode" />
          </template>
          <n-empty v-else description="暂无该类方案内容" />
        </div>
      </n-spin>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>
:deep(.md-editor-previewOnly) {
  padding: 16px;
  border-radius: 4px;
  background-color: var(--n-border-color);
  border: 1px solid var(--n-border-color)
}

:deep(.md-editor-preview-wrapper) {
  padding: 16px;
}

:deep(.md-editor-preview) {
  font-size: 14px;
  line-height: 1.6;
}

:deep(.md-editor-preview h1),
:deep(.md-editor-preview h2),
:deep(.md-editor-preview h3),
:deep(.md-editor-preview h4) {
  margin-top: 12px;
  margin-bottom: 8px;
  font-weight: 600;
}

:deep(.md-editor-preview p) {
  margin-bottom: 8px;
}

:deep(.md-editor-preview ul),
:deep(.md-editor-preview ol) {
  padding-left: 20px;
  margin-bottom: 8px;
}

:deep(.md-editor-preview blockquote) {
  margin: 8px 0;
  padding: 8px 16px;
  border-radius: 3px;
}
</style>
