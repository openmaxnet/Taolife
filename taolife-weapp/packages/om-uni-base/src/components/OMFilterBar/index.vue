<template>
  <view class="filter-bar">
    <!-- tabs 展开模式 -->
    <scroll-view v-if="mode === 'tabs'" class="filter-tabs" scroll-x :show-scrollbar="false">
      <view class="filter-tabs-inner">
        <view
          v-for="item in dimensions[0].options"
          :key="String(item.value)"
          class="filter-tab"
          :class="{ active: activeValue === item.value }"
          @click="handleTabClick(item.value)"
        >
          <text>{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- dropdown 下拉模式 -->
    <view v-if="mode === 'dropdown'" class="filter-dropdown">
      <!-- 菜单栏 -->
      <view class="dropdown-bar">
        <view
          v-for="dim in dimensions"
          :id="'trigger-' + dim.key"
          :key="dim.key"
          class="dropdown-trigger"
          :class="{ open: openKey === dim.key }"
          @click="toggleDropdown(dim.key)"
        >
          <text class="dropdown-label">{{ getDropdownTitle(dim) }}</text>
          <view class="dropdown-arrow" :class="{ up: openKey === dim.key }">
            <view class="i-solar:alt-arrow-down-linear arrow-icon" />
          </view>
        </view>
      </view>

      <!-- 下拉面板 -->
      <view v-if="openKey" class="dropdown-panel" :style="panelStyle">
        <view class="dropdown-mask" @click="closeDropdown" />
        <view class="dropdown-list">
          <view
            v-for="(item, index) in getOpenDim()?.options"
            :key="String(item.value)"
            v-show="index > 0"
            class="dropdown-option"
            :class="{ selected: currentValues[openKey!] === item.value }"
            @click="handleOptionClick(openKey!, item.value)"
          >
            <text>{{ item.label }}</text>
            <view v-if="currentValues[openKey!] === item.value" class="i-solar:check-circle-bold option-check" />
          </view>
          <!-- 重置按钮 -->
          <view class="dropdown-reset" @click="handleReset(openKey!)">
            <text>重置</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, getCurrentInstance } from 'vue';

export interface FilterOption {
  label: string;
  value: string | number | null;
}

export interface FilterDimension {
  key: string;
  options: FilterOption[];
  defaultValue?: string | number | null;
}

export type FilterParams = Record<string, string | number | null>;

const props = withDefaults(defineProps<{
  mode?: 'tabs' | 'dropdown';
  dimensions: FilterDimension[];
}>(), {
  mode: 'tabs',
});

const emit = defineEmits<{
  (e: 'change', params: FilterParams): void;
}>();

// tabs 模式
const tabKey = props.dimensions[0]?.key ?? '';
const activeValue = ref<string | number | null>(
  props.dimensions[0]?.defaultValue ?? props.dimensions[0]?.options[0]?.value ?? null
);

// dropdown 模式
const currentValues = ref<FilterParams>({});
for (const dim of props.dimensions) {
  currentValues.value[dim.key] = dim.defaultValue ?? dim.options[0]?.value ?? null;
}

const openKey = ref<string | null>(null);
const panelLeft = ref(0);
const panelMinWidth = ref(0);
const instance = getCurrentInstance();

const toggleDropdown = (key: string) => {
  if (openKey.value === key) {
    openKey.value = null;
    return;
  }
  const query = uni.createSelectorQuery().in(instance?.proxy);
  query.select(`#trigger-${key}`).boundingClientRect((rect: any) => {
    if (rect) {
      panelLeft.value = rect.left;
      panelMinWidth.value = rect.width;
    }
    openKey.value = key;
  }).exec();
}

const panelStyle = computed(() => ({
  left: panelLeft.value + 'px',
  minWidth: panelMinWidth.value + 'px',
}));

const handleTabClick = (value: string | number | null) => {
  activeValue.value = value;
  emit('change', { [tabKey]: value });
}

const closeDropdown = () => {
  openKey.value = null;
}

const getOpenDim = () =>
  openKey.value ? props.dimensions.find(d => d.key === openKey.value) : null

const handleOptionClick = (key: string, value: string | number | null) => {
  currentValues.value = { ...currentValues.value, [key]: value };
  openKey.value = null;
  emit('change', { ...currentValues.value });
}

const handleReset = (key: string) => {
  const dim = props.dimensions.find(d => d.key === key);
  const defaultValue = dim?.defaultValue ?? dim?.options[0]?.value ?? null;
  currentValues.value = { ...currentValues.value, [key]: defaultValue };
  openKey.value = null;
  emit('change', { ...currentValues.value });
}

const getDropdownTitle = (dim: FilterDimension): string => {
  const current = currentValues.value[dim.key];
  const found = dim.options.find(o => o.value === current);
  return found?.label ?? dim.options[0]?.label ?? '';
}
</script>

<style lang="scss">
.filter-bar {
  margin-bottom: $om-space-4;
  position: relative;
  padding-top: $om-space-1;
  background: $om-page-bg-color;
}

// tabs 模式
.filter-tabs {
  width: 100%;
  background: $om-surface;
  border-radius: $om-radius-lg;
  padding: $om-space-2;
  white-space: nowrap;
  box-sizing: border-box;
}

.filter-tabs-inner {
  display: inline-flex;
  align-items: center;
}

.filter-tab {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $om-space-5 $om-space-6;
  border-radius: $om-radius-md;
  transition: all 0.2s ease;

  text {
    font-size: $om-text-base;
    color: $om-gray-600;
  }

  &.active {
    background: $om-brand-bg-light;

    text {
      color: $om-primary-color;
      font-weight: 600;
    }
  }
}

// dropdown 模式
.filter-dropdown {
  position: relative;
}

.dropdown-bar {
  display: flex;
  align-items: center;
  background: $om-surface;
  border-radius: $om-radius-lg;
  padding: $om-space-2;
}

.dropdown-trigger {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $om-space-1;
  padding: 22rpx 0;
  transition: color 0.2s;

  &.open {
    background: $om-brand-bg-light;
    border-radius: $om-radius-md;

    .dropdown-label {
      color: $om-primary-color;
    }

    .arrow-icon {
      color: $om-primary-color;
    }
  }
}

.dropdown-label {
  font-size: $om-text-base;
  color: $om-gray-600;
}

.dropdown-arrow {
  display: flex;
  align-items: center;
  transition: transform 0.2s;

  &.up {
    transform: rotate(180deg);
  }
}

.arrow-icon {
  font-size: $om-text-sm;
  color: $om-gray-400;
}

.dropdown-panel {
  position: absolute;
  top: 100%;
  z-index: 100;
}

.dropdown-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 99;
}

.dropdown-list {
  position: relative;
  z-index: 100;
  background: $om-surface;
  border-radius: $om-radius-lg;
  margin-top: $om-space-2;
  padding: $om-space-2 0;
  box-shadow: $om-shadow-card;
  white-space: nowrap;
  width: fit-content;
}

.dropdown-option {
  display: flex;
  align-items: center;
  padding: $om-space-6 $om-space-8;

  text {
    font-size: $om-text-base;
    color: $om-gray-700;
    margin-right: $om-space-6;
  }

  &.selected {
    text {
      color: $om-primary-color;
      font-weight: 600;
    }
  }
}

.option-check {
  font-size: $om-text-md;
  color: $om-primary-color;
}

.dropdown-reset {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $om-space-5 $om-space-8;
  margin: $om-space-2 $om-space-6 0;
  border-top: 1rpx solid var(--om-gray-300);
  color: $om-primary-color;

  text {
    font-size: $om-text-base;
    font-weight: 500;
    color: $om-primary-color;
  }
}
</style>
