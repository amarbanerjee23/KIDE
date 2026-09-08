import { useAuthStore } from '../stores/authStore';

const BASE_URL = '/api/v1';

export async function fetchClient(endpoint: string, options: RequestInit = {}) {
  const token = useAuthStore.getState().token;
  const headers = new Headers(options.headers);
  headers.set('Content-Type', 'application/json');
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    if (response.status === 401) {
      useAuthStore.getState().logout();
      throw new Error('Unauthorized');
    }
    const errorData = await response.json().catch(() => ({}));
    let errorMessage = 'API request failed';
    if (Array.isArray(errorData.detail)) {
      errorMessage = errorData.detail.map((e: any) => `${e.loc.join('.')}: ${e.msg}`).join(', ');
    } else if (errorData.detail) {
      errorMessage = errorData.detail;
    }
    throw new Error(errorMessage);
  }

  return response.json();
}

