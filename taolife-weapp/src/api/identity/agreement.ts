import { get } from '@/utils/request'

export interface AgreementVO {
  title: string
  content: string
  version: number
}

export const getAgreementByCode = async (code: string) => {
  return get<AgreementVO>('/api/identity/agreement/getByCode', { code })
}
