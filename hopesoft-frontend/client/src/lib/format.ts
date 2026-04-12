import type { FormaPagamento, Perfil } from '@/lib/types';

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
});

const dateTimeFormatter = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
  timeStyle: 'short',
});

const dateFormatter = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
});

const paymentLabels: Record<FormaPagamento, string> = {
  DINHEIRO: 'Dinheiro',
  PIX: 'Pix',
  CARTAO_DEBITO: 'Cartao de debito',
  CARTAO_CREDITO: 'Cartao de credito',
  FIADO: 'Fiado',
  VALE_TROCA: 'Vale-troca',
  MISTO: 'Pagamento misto',
};

const roleLabels: Record<Perfil, string> = {
  ADMIN: 'Administrador',
  OPERADOR: 'Operador',
};

export function formatCurrency(value: number | string | null | undefined) {
  return currencyFormatter.format(Number(value ?? 0));
}

export function formatDateTime(value: string | Date) {
  return dateTimeFormatter.format(typeof value === 'string' ? new Date(value) : value);
}

export function formatDate(value: string | Date) {
  return dateFormatter.format(typeof value === 'string' ? new Date(value) : value);
}

export function formatPaymentLabel(value: FormaPagamento) {
  return paymentLabels[value] ?? value;
}

export function formatRoleLabel(value: Perfil) {
  return roleLabels[value] ?? value;
}

export function toInputDate(date = new Date()) {
  return date.toISOString().slice(0, 10);
}
