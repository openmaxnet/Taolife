<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { getAccountDetail } from '@/service'

const { bool: visible, setTrue: show } = useBoolean(false)
const loading = ref(false)
const detail = ref<Entity.AccountDetailVO>({})

const bloodTypeMap: Record<number, string> = { 1: 'A型', 2: 'B型', 3: 'AB型', 4: 'O型' }
const genderMap: Record<number, string> = { 0: '未知', 1: '男', 2: '女' }
const memberLevelMap: Record<number, string> = { 0: '普通用户', 1: '月卡会员', 2: '年卡会员', 3: '终身会员' }

async function loadDetail(id: string) {
  loading.value = true
  const res = await getAccountDetail(id)
  detail.value = res.data ?? {}
  loading.value = false
}

function open(id: string) {
  show()
  loadDetail(id)
}

defineExpose({ open })
</script>

<template>
  <n-drawer v-model:show="visible" :width="600">
    <n-drawer-content title="账号详情">
      <n-spin :show="loading">
        <n-divider>账号信息</n-divider>
        <n-descriptions :column="2" label-placement="left" bordered size="small">
          <n-descriptions-item label="账号ID">{{ detail.id }}</n-descriptions-item>
          <n-descriptions-item label="手机号">{{ detail.phone || '-' }}</n-descriptions-item>
          <n-descriptions-item label="昵称">{{ detail.nickname || '-' }}</n-descriptions-item>
          <n-descriptions-item label="性别">{{ genderMap[detail.gender ?? 0] || '-' }}</n-descriptions-item>
          <n-descriptions-item label="会员等级">
            <n-tag v-if="memberLevelMap[detail.memberLevel ?? 0]" size="small" type="warning">
              {{ memberLevelMap[detail.memberLevel ?? 0] }}
            </n-tag>
            <span v-else>-</span>
          </n-descriptions-item>
          <n-descriptions-item label="会员到期">{{ detail.memberExpireTime || '-' }}</n-descriptions-item>
          <n-descriptions-item label="注册时间">{{ detail.createTime || '-' }}</n-descriptions-item>
          <n-descriptions-item label="状态">
            <n-tag :type="detail.isDisabled === 0 ? 'success' : 'error'" size="small">
              {{ detail.isDisabled === 0 ? '正常' : '已禁用' }}
            </n-tag>
          </n-descriptions-item>
        </n-descriptions>

        <n-divider>健康档案</n-divider>
        <n-descriptions :column="2" label-placement="left" bordered size="small">
          <n-descriptions-item label="真实姓名">{{ detail.realName || '-' }}</n-descriptions-item>
          <n-descriptions-item label="身份证号">{{ detail.idCard || '-' }}</n-descriptions-item>
          <n-descriptions-item label="邮箱">{{ detail.email || '-' }}</n-descriptions-item>
          <n-descriptions-item label="血型">{{ bloodTypeMap[detail.bloodType ?? 0] || '-' }}</n-descriptions-item>
          <n-descriptions-item label="身高">{{ detail.height ? `${detail.height} cm` : '-' }}</n-descriptions-item>
          <n-descriptions-item label="体重">{{ detail.weight ? `${detail.weight} kg` : '-' }}</n-descriptions-item>
          <n-descriptions-item label="所在地" :span="2">
            {{ [detail.province, detail.city, detail.district].filter(Boolean).join(' ') || '-' }}
          </n-descriptions-item>
          <n-descriptions-item label="详细地址" :span="2">{{ detail.address || '-' }}</n-descriptions-item>
          <n-descriptions-item label="紧急联系人">{{ detail.emergencyContact || '-' }}</n-descriptions-item>
          <n-descriptions-item label="紧急联系电话">{{ detail.emergencyPhone || '-' }}</n-descriptions-item>
          <n-descriptions-item label="过敏史" :span="2">{{ detail.allergyHistory || '-' }}</n-descriptions-item>
          <n-descriptions-item label="病史" :span="2">{{ detail.medicalHistory || '-' }}</n-descriptions-item>
        </n-descriptions>
      </n-spin>
    </n-drawer-content>
  </n-drawer>
</template>
