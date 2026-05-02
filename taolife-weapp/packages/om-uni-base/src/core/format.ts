/**
 * 格式化工具函数
 */

export const formatCount = (count: number): string => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + 'k'
  return count.toString()
}

export const parseTagsJson = (str: string | null | undefined): string[] => {
  if (!str) return []
  try {
    const arr: string[] = JSON.parse(str)
    return Array.isArray(arr) ? arr : []
  } catch {
    return []
  }
}

export const parseTagsCsv = (str: string): string[] => {
  if (!str) return []
  return str.split(',').filter(tag => tag.trim())
}

export const formatDate = (dateStr: string): string => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
