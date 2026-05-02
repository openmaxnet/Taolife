<script setup lang="tsx">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getMemberPlanPage, createMemberPlan, modifyMemberPlan, removeMemberPlan } from '@/service'
import { NButton, NSpace, NTag, NInput, NInputNumber, NSelect, NSwitch } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)
const { bool: showModal, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: saving, setTrue: startSaving, setFalse: endSaving } = useBoolean(false)

const memberLevelMap: Record<number, string> = {
  1: '月卡会员',
  2: '年卡会员',
  3: '终身会员',
}

const memberLevelOptions = [
  { label: '月卡会员', value: 1 },
  { label: '年卡会员', value: 2 },
  { label: '终身会员', value: 3 },
]

const getDefaultForm = (): Entity.MemberPlanSaveParam => ({
  planCode: '',
  planName: '',
  memberLevel: 1,
  durationDays: 30,
  originalPrice: 0,
  currentPrice: 0,
  discountLabel: '',
  aiDailyQuota: 50,
  maxActivePlans: 3,
  maxCycleDays: 21,
  maxAdjustments: 5,
  assessmentMonthlyQuota: 3,
  pointsMultiplier: 2,
  storeDiscount: 0.95,
  pointsToYuanRatio: 50,
  benefitsJson: '',
  subGrowthBonus: 0,
  sortOrder: 0,
  isEnabled: 1,
})

const formRef = ref<FormInst | null>(null)
const isEdit = ref(false)
const formData = ref<Entity.MemberPlanSaveParam>(getDefaultForm())

const formRules: FormRules = {
  planCode: { required: true, message: '请输入套餐代码', trigger: 'blur' },
  planName: { required: true, message: '请输入套餐名称', trigger: 'blur' },
  memberLevel: { required: true, type: 'number', message: '请选择会员等级', trigger: 'change' },
  durationDays: { required: true, type: 'number', message: '请输入会员时长', trigger: 'blur' },
  originalPrice: { required: true, type: 'number', message: '请输入原价', trigger: 'blur' },
  currentPrice: { required: true, type: 'number', message: '请输入现价', trigger: 'blur' },
}

const handleOpenCreate = () => {
  isEdit.value = false
  formData.value = getDefaultForm()
  openModal()
}

const handleOpenEdit = (row: Entity.MemberPlanVO) => {
  isEdit.value = true
  formData.value = {
    id: row.id,
    planCode: row.planCode,
    planName: row.planName,
    memberLevel: row.memberLevel,
    durationDays: row.durationDays,
    originalPrice: row.originalPrice,
    currentPrice: row.currentPrice,
    discountLabel: row.discountLabel,
    aiDailyQuota: row.aiDailyQuota,
    maxActivePlans: row.maxActivePlans,
    maxCycleDays: row.maxCycleDays,
    maxAdjustments: row.maxAdjustments,
    assessmentMonthlyQuota: row.assessmentMonthlyQuota,
    pointsMultiplier: row.pointsMultiplier,
    storeDiscount: row.storeDiscount,
    pointsToYuanRatio: row.pointsToYuanRatio,
    benefitsJson: row.benefitsJson,
    subGrowthBonus: row.subGrowthBonus,
    sortOrder: row.sortOrder,
    isEnabled: row.isEnabled,
  }
  openModal()
}

const handleSave = async () => {
  await formRef.value?.validate()
  startSaving()
  try {
    if (isEdit.value && formData.value.id) {
      await modifyMemberPlan(formData.value.id, formData.value)
      window.$message.success('修改成功')
    } else {
      await createMemberPlan(formData.value)
      window.$message.success('创建成功')
    }
    closeModal()
    getList()
  } finally {
    endSaving()
  }
}

const handleRemove = (id: string) => {
  window.$dialog.warning({
    title: '确认删除',
    content: '确定要删除这个套餐吗？',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: () => {
      removeMemberPlan(id).then(() => {
        window.$message.success('删除成功')
        getList()
      })
    },
  })
}

const columns: DataTableColumns<Entity.MemberPlanVO> = [
  { title: '套餐代码', key: 'planCode', width: 100 },
  { title: '套餐名称', key: 'planName', width: 100 },
  {
    title: '等级',
    key: 'memberLevel',
    width: 90,
    render: row => memberLevelMap[row.memberLevel ?? 0] ?? '-',
  },
  { title: '时长(天)', key: 'durationDays', width: 80, align: 'center' },
  {
    title: '原价(分)',
    key: 'originalPrice',
    width: 90,
    align: 'right',
    render: row => row.originalPrice !== null && row.originalPrice !== undefined ? `¥${(row.originalPrice / 100).toFixed(2)}` : '-',
  },
  {
    title: '现价(分)',
    key: 'currentPrice',
    width: 90,
    align: 'right',
    render: row => row.currentPrice !== null && row.currentPrice !== undefined ? `¥${(row.currentPrice / 100).toFixed(2)}` : '-',
  },
  { title: 'AI配额', key: 'aiDailyQuota', width: 80, align: 'center', render: row => row.aiDailyQuota === -1 ? '无限' : row.aiDailyQuota },
  { title: '方案上限', key: 'maxActivePlans', width: 80, align: 'center', render: row => row.maxActivePlans === -1 ? '无限' : row.maxActivePlans },
  { title: '积分倍率', key: 'pointsMultiplier', width: 80, align: 'center', render: row => `${row.pointsMultiplier}x` },
  { title: '开通成长值', key: 'subGrowthBonus', width: 90, align: 'center' },
  {
    title: '状态',
    key: 'isEnabled',
    width: 70,
    align: 'center',
    render: row => <NTag type={row.isEnabled === 1 ? 'success' : 'error'} size="small">{row.isEnabled === 1 ? '启用' : '禁用'}</NTag>,
  },
  { title: '创建时间', key: 'createTime', width: 170 },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    align: 'center',
    fixed: 'right',
    render: (row) => {
      return (
        <NSpace justify="center" size="small">
          <NButton size="small" type="info" onClick={() => handleOpenEdit(row)}>编辑</NButton>
          <NButton size="small" type="error" onClick={() => handleRemove(row.id ?? '')}>删除</NButton>
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.MemberPlanVO[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

const getList = () => {
  startLoading()
  getMemberPlanPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

const changePage = (page: number, size: number) => {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <NSpace vertical>
    <n-card>
      <template #header>
        <NButton type="primary" @click="handleOpenCreate">
          <template #icon>
            <icon-park-outline-add />
          </template>
          新增套餐
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: Entity.MemberPlanVO) => row.id ?? ''" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>

    <!-- 新增/编辑弹窗 -->
    <n-modal v-model:show="showModal" preset="card" :title="isEdit ? '编辑套餐' : '新增套餐'" style="width: 640px" :mask-closable="false">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="110">
        <n-grid :cols="2" :x-gap="16">
          <n-form-item-gi label="套餐代码" path="planCode">
            <n-input v-model:value="formData.planCode" placeholder="如 monthly、yearly、quarterly" />
          </n-form-item-gi>
          <n-form-item-gi label="套餐名称" path="planName">
            <n-input v-model:value="formData.planName" placeholder="如 月卡会员" />
          </n-form-item-gi>
          <n-form-item-gi label="会员等级" path="memberLevel">
            <n-select v-model:value="formData.memberLevel" :options="memberLevelOptions" />
          </n-form-item-gi>
          <n-form-item-gi label="时长(天)" path="durationDays">
            <n-input-number v-model:value="formData.durationDays" :min="0" placeholder="0=终身" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="原价(分)" path="originalPrice">
            <n-input-number v-model:value="formData.originalPrice" :min="0" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="现价(分)" path="currentPrice">
            <n-input-number v-model:value="formData.currentPrice" :min="0" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="折扣标签">
            <n-input v-model:value="formData.discountLabel" placeholder="如 限时8折" />
          </n-form-item-gi>
          <n-form-item-gi label="排序">
            <n-input-number v-model:value="formData.sortOrder" :min="0" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="AI每日配额">
            <n-input-number v-model:value="formData.aiDailyQuota" :min="-1" placeholder="-1=无限" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="方案上限">
            <n-input-number v-model:value="formData.maxActivePlans" :min="-1" placeholder="-1=无限" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="方案周期(天)">
            <n-input-number v-model:value="formData.maxCycleDays" :min="1" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="调整次数">
            <n-input-number v-model:value="formData.maxAdjustments" :min="-1" placeholder="-1=无限" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="评估配额/月">
            <n-input-number v-model:value="formData.assessmentMonthlyQuota" :min="-1" placeholder="-1=无限" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="积分倍率">
            <n-input-number v-model:value="formData.pointsMultiplier" :min="1" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="商城折扣">
            <n-input-number v-model:value="formData.storeDiscount" :min="0" :max="1" :step="0.05" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="积分抵现比">
            <n-input-number v-model:value="formData.pointsToYuanRatio" :min="1" placeholder="50=50积分=1元" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="开通成长值">
            <n-input-number v-model:value="formData.subGrowthBonus" :min="0" placeholder="开通/续费奖励成长值" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="启用">
            <n-switch v-model:value="formData.isEnabled" :checked-value="1" :unchecked-value="0" />
          </n-form-item-gi>
        </n-grid>
      </n-form>
      <template #footer>
        <NSpace justify="end">
          <NButton @click="closeModal">取消</NButton>
          <NButton type="primary" :loading="saving" @click="handleSave">确认</NButton>
        </NSpace>
      </template>
    </n-modal>
  </NSpace>
</template>
