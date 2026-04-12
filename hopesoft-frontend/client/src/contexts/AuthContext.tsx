import React, { createContext, useContext, useEffect, useState } from 'react';
import api from '@/lib/api';
import { getErrorMessage } from '@/lib/http';
import type { LoginResponse, UsuarioAutenticado } from '@/lib/types';

export interface AuthContextType {
  user: UsuarioAutenticado | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  token: string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function persistSession(token: string, user: UsuarioAutenticado) {
  localStorage.setItem('hopesoft_token', token);
  localStorage.setItem('hopesoft_user', JSON.stringify(user));
}

function clearSession() {
  localStorage.removeItem('hopesoft_token');
  localStorage.removeItem('hopesoft_user');
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UsuarioAutenticado | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('hopesoft_token');
    const storedUser = localStorage.getItem('hopesoft_user');

    async function restoreSession() {
      if (!storedToken) {
        setIsLoading(false);
        return;
      }

      setToken(storedToken);

      if (storedUser) {
        try {
          setUser(JSON.parse(storedUser));
        } catch {
          clearSession();
        }
      }

      try {
        const response = await api.get<UsuarioAutenticado>('/auth/me');
        setUser(response.data);
        persistSession(storedToken, response.data);
      } catch {
        clearSession();
        setToken(null);
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    }

    void restoreSession();
  }, []);

  const login = async (email: string, senha: string) => {
    setIsLoading(true);

    try {
      const response = await api.post<LoginResponse>('/auth/login', { email, senha });
      persistSession(response.data.token, response.data.usuario);
      setToken(response.data.token);
      setUser(response.data.usuario);
    } catch (error) {
      throw new Error(getErrorMessage(error, 'Falha na autenticacao.'));
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    clearSession();
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        token,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }

  return context;
};
