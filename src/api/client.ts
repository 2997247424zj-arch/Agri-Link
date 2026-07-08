import type { ApiResponse, UserRole } from '@/types/domain'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:9091'

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
  const headers = new Headers(options.headers)

  if (!headers.has('Content-Type') && options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }

  const storedRole = typeof localStorage === 'undefined' ? '' : localStorage.getItem('agri-link-role')
  const role = options.role ?? storedRole
  if (role) {
    headers.set('X-User-Role', role)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })
  const payload = (await response.json().catch(() => null)) as ApiResponse<T> | null

  if (!response.ok || payload?.success === false) {
    throw new ApiError(payload?.message ?? `HTTP ${response.status}`, response.status)
  }

  return payload && 'data' in payload ? payload.data : (undefined as T)
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
