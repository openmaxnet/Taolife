import { get } from '@/utils/request'
import type { HomeBannerItem } from '@/types/biz/fee/homeBanner'

/** 获取首页轮播列表 */
export const getBannerList = async () => {
  return get<HomeBannerItem[]>('/api/fee/banner/getBannerList')
}
