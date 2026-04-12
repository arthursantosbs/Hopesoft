import type { ApiError } from '@/lib/types';

export function getErrorMessage(error: unknown, fallback = 'Nao foi possivel concluir a operacao.') {
  if (typeof error === 'string') {
    return error;
  }

  if (error && typeof error === 'object') {
    const maybeError = error as { message?: string; response?: { data?: ApiError | string } };

    if (typeof maybeError.response?.data === 'string') {
      return maybeError.response.data;
    }

    if (maybeError.response?.data && typeof maybeError.response.data === 'object') {
      return maybeError.response.data.message || fallback;
    }

    if (maybeError.message) {
      return maybeError.message;
    }
  }

  return fallback;
}
