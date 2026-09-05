import { create } from 'zustand';
import { User } from '../types/models';
import { authApi } from '../api/auth';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, pass: string) => Promise<void>;
  register: (email: string, pass: string, fullName: string, orgName?: string) => Promise<void>;
  logout: () => void;
  loadFromStorage: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  login: async (email, password) => {
    const data = await authApi.login(email, password);
    set({ token: data.access_token });
    localStorage.setItem('kide_token', data.access_token);
    const user = await authApi.getProfile();
    set({ user, isAuthenticated: true });
  },
  register: async (email, password, fullName, orgName) => {
    await authApi.register({ email, password, fullName, orgName });
    const data = await authApi.login(email, password);
    set({ token: data.access_token });
    localStorage.setItem('kide_token', data.access_token);
    const user = await authApi.getProfile();
    set({ user, isAuthenticated: true });
  },
  logout: () => {
    localStorage.removeItem('kide_token');
    set({ user: null, token: null, isAuthenticated: false });
  },
  loadFromStorage: () => {
    const token = localStorage.getItem('kide_token');
    if (token) {
      set({ token, isAuthenticated: true });
      authApi.getProfile().then(user => set({ user })).catch(() => {
        localStorage.removeItem('kide_token');
        set({ token: null, isAuthenticated: false, user: null });
      });
    }
  }
}));

