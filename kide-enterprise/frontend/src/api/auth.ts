import { fetchClient } from './client';
import { User } from '../types/models';

export const authApi = {
  login: async (email: string, password: string): Promise<{ access_token: string, token_type: string }> => {
    // using form data for oauth2
    const formData = new URLSearchParams();
    formData.append('username', email);
    formData.append('password', password);

    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: formData,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    
    if (!response.ok) {
      throw new Error('Login failed');
    }
    return response.json();
  },
  
  register: async (data: { email: string; password: string; fullName: string; orgName?: string }) => {
    return fetchClient('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        email: data.email,
        password: data.password,
        full_name: data.fullName,
        org_name: data.orgName
      }),
    });
  },

  getProfile: async (): Promise<User> => {
    return fetchClient('/auth/me');
  }
};

