import type { ApiResponse, FileUploadResponse, UserRole } from '@/types/domain'
import { measureApi } from '@/utils/performance'

// 默认连接本地后端，可通过 .env 覆盖。
const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:9091').replace(/\/$/, '')
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

  const storedUser = typeof localStorage === 'undefined' ? '' : localStorage.getItem('agri-link-user')
  if (storedUser) {
    headers.set('X-User-Name', storedUser)
  }

  const getKey = method === 'GET' && !options.body ? `${path}|${role ?? ''}` : ''
  if (getKey && pendingGets.has(getKey)) {
    return pendingGets.get(getKey) as Promise<T>
  }

  const task = (async () => {
    let response: Response
    try {
      response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers,
      })
    } catch (err) {
      // fetch 本身 reject 一般是网络层错误（后端未启动、端口不通、CORS 未放行等）
      const reason = err instanceof Error ? err.message : String(err)
      throw new ApiError(`无法连接后端服务（${reason}）。请确认 Spring Boot 已在 ${API_BASE} 运行。`, 0)
    }
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

// 后端上传接口返回 /files/** 相对路径，开发环境需补上后端端口；
// public/file、data URL、blob URL 和外部图片仍按原地址加载。
export function resolveAssetUrl(src?: string) {
  if (!src) return ''
  return src.startsWith('/files/') ? `${API_BASE}${src}` : src
}

export function uploadImage(file: File, role?: UserRole | string) {
  const body = new FormData()
  body.append('file', file)
  return request<FileUploadResponse>('/api/files/images', { method: 'POST', body, role })
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

export async function downloadFile(path: string, fileName: string) {
  const headers = new Headers()
  const storedRole = typeof localStorage === 'undefined' ? '' : localStorage.getItem('agri-link-role')
  if (storedRole) headers.set('X-User-Role', storedRole)
  const storedUser = typeof localStorage === 'undefined' ? '' : localStorage.getItem('agri-link-user')
  if (storedUser) headers.set('X-User-Name', storedUser)

  const response = await fetch(`${API_BASE}${path}`, { headers })
  if (!response.ok) throw new ApiError(`导出失败: HTTP ${response.status}`, response.status)
  const blob = await response.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}
