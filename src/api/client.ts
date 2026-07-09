import type { ApiResponse, UserRole } from '@/types/domain'
import { measureApi } from '@/utils/performance'

// 默认连接本地后端，可通过 .env 覆盖。
const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:9091'
const pendingGets = new Map<string, Promise<unknown>>()

interface RequestOptions extends RequestInit {
  role?: UserRole | string
}

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message)
  }
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const startedAt = typeof performance === 'undefined' ? Date.now() : performance.now()
  const headers = new Headers(options.headers)
  const method = options.method ?? 'GET'

  if (!headers.has('Content-Type') && options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }

  // 后端以 X-User-Role 做演示鉴权，默认读取当前会话角色。
  const storedRole = typeof localStorage === 'undefined' ? '' : localStorage.getItem('agri-link-role')
  const role = options.role ?? storedRole
  if (role) {
    headers.set('X-User-Role', role)
  }

  const getKey = method === 'GET' && !options.body ? `${path}|${role ?? ''}` : ''
  if (getKey && pendingGets.has(getKey)) {
    return pendingGets.get(getKey) as Promise<T>
  }

  const task = (async () => {
    const response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers,
    })
    // 兼容 204 或非 JSON 响应，错误仍按 HTTP 状态处理。
    const payload = (await response.json().catch(() => null)) as ApiResponse<T> | null

    if (!response.ok || payload?.success === false) {
      throw new ApiError(payload?.message ?? `HTTP ${response.status}`, response.status)
    }

    return payload && 'data' in payload ? payload.data : (undefined as T)
  })()

  if (getKey) {
    pendingGets.set(getKey, task)
  }

  try {
    return await task
  } finally {
    if (getKey) {
      pendingGets.delete(getKey)
    }
    const endedAt = typeof performance === 'undefined' ? Date.now() : performance.now()
    measureApi(path, endedAt - startedAt, method)
  }
}

export const api = {
  get: <T>(path: string, options?: RequestOptions) => request<T>(path, options),
  post: <T>(path: string, body: unknown, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'POST', body: JSON.stringify(body) }),
  put: <T>(path: string, body: unknown, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'PUT', body: JSON.stringify(body) }),
  patch: <T>(path: string, body: unknown, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'PATCH', body: JSON.stringify(body) }),
  delete: <T>(path: string, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'DELETE' }),
}
