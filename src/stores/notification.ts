import { ref } from 'vue'
import { defineStore } from 'pinia'
import { api } from '@/api/client'
import type { Notification } from '@/types/domain'

export const useNotificationStore = defineStore('notification', () => {
  const unreadCount = ref(0)
  const notifications = ref<Notification[]>([])
  const loading = ref(false)

  async function fetchUnreadCount() {
    try {
      unreadCount.value = await api.get<number>('/api/notifications/unread-count')
    } catch {
      /* 非关键功能，静默 */
    }
  }

  async function fetchNotifications() {
    loading.value = true
    try {
      notifications.value = await api.get<Notification[]>('/api/notifications')
    } catch {
      notifications.value = []
    } finally {
      loading.value = false
    }
  }

  async function markAsRead(id: number) {
    try {
      await api.patch<void>(`/api/notifications/${id}/read`, {})
      const item = notifications.value.find((n) => n.notificationId === id)
      if (item && !item.isRead) {
        item.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    } catch {
      /* 静默 */
    }
  }

  async function markAllAsRead() {
    try {
      await api.patch<void>('/api/notifications/read-all', {})
      notifications.value.forEach((n) => (n.isRead = true))
      unreadCount.value = 0
    } catch {
      /* 静默 */
    }
  }

  return { unreadCount, notifications, loading, fetchUnreadCount, fetchNotifications, markAsRead, markAllAsRead }
})
