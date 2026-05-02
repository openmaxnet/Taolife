import { useCachedResource } from '@/composables/useCachedResource'
import { getLatestResult } from '@/api/wisdom/constitution'
import type { ConstitutionResult } from '@/types/biz/ai/constitution'

export function useUserProfile() {
  const { data: constitution, loading, fetch: fetchConstitution, clearCache } = useCachedResource<ConstitutionResult>({
    storageKey: 'tf_user_constitution',
    cacheTtl: 30 * 60 * 1000,
    fetchFn: getLatestResult,
  })

  return { constitution, loading, fetchConstitution, clearCache }
}
