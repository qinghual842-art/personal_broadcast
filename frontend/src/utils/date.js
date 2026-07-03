import dayjs from 'dayjs'

export function formatDate(date, format = 'YYYY-MM-DD HH:mm') {
  if (!date) return ''
  return dayjs(date).format(format)
}

export function formatDateShort(date) {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}
