import { useCachedResource } from '@/composables/useCachedResource'
import { getHealthPlan } from '@/api/plan/plan'
import type { PlanSummary } from '@/types/biz/plan/plan'

export function useActivePlan() {
  const { data: plan, loading, fetch: fetchPlan, clearCache } = useCachedResource<PlanSummary>({
    storageKey: 'tf_active_plan',
    cacheTtl: 15 * 60 * 1000,
    fetchFn: getHealthPlan,
  })

  return { plan, loading, fetchPlan, clearCache }
}
