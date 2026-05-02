<template>
  <view class="page-container">

    <!-- 自定义导航栏 -->
    <TLTopBar title="编辑资料" />

    <!-- 主内容区 -->
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 头像（不可编辑） -->
      <view class="avatar-section">
        <image class="avatar-preview" :src="form.avatarUrl || defaultAvatar" mode="aspectFill" />
      </view>

      <!-- 表单 -->
      <view class="form-card">
        <!-- 昵称 -->
        <view class="form-item">
          <text class="form-label">昵称</text>
          <input
            class="form-input"
            type="text"
            :value="form.nickname"
            placeholder="请输入昵称"
            maxlength="20"
            @input="onNicknameInput"
          />
        </view>

        <!-- 性别 -->
        <view class="form-item" @click="genderVisible = true">
          <text class="form-label">性别</text>
          <view class="form-picker">
            <text :class="['picker-text', !form.gender && 'placeholder']">
              {{ genderText || '请选择性别' }}
            </text>
            <view class="picker-arrow i-solar:alt-arrow-right-outline" />
          </view>
        </view>

        <!-- 生日 -->
        <picker mode="date" :value="form.birthday || '2000-01-01'" start="1900-01-01" :end="today" @change="onBirthdayChange">
          <view class="form-item">
            <text class="form-label">生日</text>
            <view class="form-picker">
              <text :class="['picker-text', !form.birthday && 'placeholder']">
                {{ form.birthday || '请选择生日' }}
              </text>
              <view class="picker-arrow i-solar:alt-arrow-right-outline" />
            </view>
          </view>
        </picker>
      </view>

      <!-- 保存按钮 -->
      <view class="save-btn" :class="{ disabled: saving }" @click="handleSave">
        <text>{{ saving ? '保存中...' : '保存' }}</text>
      </view>

    </view>

    <!-- 性别选择器 -->
    <t-picker
      :visible="genderVisible"
      :value="genderValue"
      title="选择性别"
      cancel-btn="取消"
      confirm-btn="确认"
      :using-custom-navbar="true"
      @update:visible="genderVisible = $event"
      @confirm="onGenderConfirm"
    >
      <t-picker-item :options="genderOptions" />
    </t-picker>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { getUserInfo, modifyUserInfo } from '@/api/identity/account'
import type { UserInfoVO } from '@/types'

// 页面级样式隔离
defineOptions({ options: { styleIsolation: 'shared' } })

const defaultAvatar = '/static/logo.png'

const { contentPaddingTop } = usePageLayout()

// 今日日期（生日的最大值）
const today = new Date().toISOString().slice(0, 10)

// 表单数据
const form = ref({
  nickname: '',
  avatarUrl: '',
  gender: 0,
  birthday: ''
})

// 保存状态
const saving = ref(false)

// 性别选择器
const genderVisible = ref(false)
const genderOptions = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 }
]
const genderValue = computed(() => [form.value.gender])
const genderText = computed(() => genderOptions.find(o => o.value === form.value.gender)?.label || '')

// 昵称输入
const onNicknameInput = (e: any) => {
  form.value.nickname = e.detail.value
}

// 性别确认
const onGenderConfirm = (e: any) => {
  form.value.gender = e.value[0]
}

// 生日选择
const onBirthdayChange = (e: any) => {
  form.value.birthday = e.detail.value
}

// 保存
const handleSave = async () => {
  if (saving.value) return

  const nickname = form.value.nickname.trim()
  if (!nickname) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }

  saving.value = true
  try {
    await modifyUserInfo({
      nickname,
      gender: form.value.gender,
      birthday: form.value.birthday || undefined
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
    uni.$emit('profileUpdated')
    setTimeout(() => uni.navigateBack(), 500)
  } catch {
    uni.showToast({ title: '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}

// 加载用户数据
const loadUserInfo = async () => {
  try {
    const userData: UserInfoVO = await getUserInfo()
    form.value = {
      nickname: userData.nickname || '',
      avatarUrl: userData.avatarUrl || '',
      gender: userData.gender || 0,
      birthday: userData.birthday || ''
    }
  } catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.content-area {
  padding-bottom: 32rpx;
}

// 头像展示
.avatar-section {
  display: flex;
  justify-content: center;
  padding: 48rpx 0;
}

.avatar-preview {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  // border: 8rpx solid #ECFDF5;
}

// 表单卡片
.form-card {
  margin: 0 24rpx;
  background: $tf-surface;
  border-radius: 24rpx;
  overflow: hidden;
}

.form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: $tf-border-normal;

  &:last-child {
    border-bottom: none;
  }
}

.form-label {
  font-size: 28rpx;
  color: $tf-gray-800;
  flex-shrink: 0;
  width: 120rpx;
}

.form-input {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  color: $tf-gray-900;
}

.form-picker {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.picker-text {
  font-size: 28rpx;
  color: $tf-gray-900;

  &.placeholder {
    color: $tf-gray-500;
  }
}

.picker-arrow {
  width: 28rpx;
  height: 28rpx;
  color: $tf-gray-400;
}

// 保存按钮
.save-btn {
  margin: 48rpx 24rpx 0;
  padding: 24rpx 0;
  background: $tf-gradient-brand;
  border-radius: 48rpx;
  text-align: center;

  text {
    font-size: 30rpx;
    color: $tf-surface;
    font-weight: bold;
  }

  &.disabled {
    opacity: 0.6;
  }
}
</style>
