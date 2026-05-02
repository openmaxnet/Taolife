<script setup lang="ts">
import { useAppStore, useRouteStore } from '@/store'

const appStore = useAppStore()
const routeStore = useRouteStore()
</script>

<template>
  <n-el
    class="h-full"
    :class="[appStore.layoutMode === 'full-content' ? 'p-0' : 'p-16px']"
    style="background-color: var(--action-color)"
  >
    <router-view v-slot="{ Component, route }">
      <transition :name="appStore.transitionAnimation" mode="out-in">
        <keep-alive :include="routeStore.cacheRoutes">
          <div v-if="appStore.loadFlag && Component" :key="route.name || route.fullPath" class="h-full">
            <component :is="Component" />
          </div>
        </keep-alive>
      </transition>
    </router-view>
  </n-el>
</template>
