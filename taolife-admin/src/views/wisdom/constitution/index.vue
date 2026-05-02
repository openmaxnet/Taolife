<script setup lang="tsx">
import type { Component } from 'vue'
import ConstitutionType from './type.vue'
import ConstitutionQuestion from './question.vue'

const tabs = [
  { name: 'type', label: '体质类型', component: ConstitutionType },
  { name: 'question', label: '问卷题目', component: ConstitutionQuestion },
] as const

const currentTab = ref<(typeof tabs)[number]['name']>('type')
</script>

<template>
  <n-tabs v-model:value="currentTab" type="line" animated>
    <n-tab-pane v-for="tab in tabs" :key="tab.name" :name="tab.name" :tab="tab.label">
      <component :is="tab.component" />
    </n-tab-pane>
  </n-tabs>
</template>
