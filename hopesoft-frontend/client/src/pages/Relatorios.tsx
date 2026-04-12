import { useEffect, useMemo, useState } from 'react';
import {
  Bar,
  BarChart,
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { Card } from '@/components/ui/card';
import api from '@/lib/api';
import {
  formatCurrency,
  formatDate,
  formatPaymentLabel,
  toInputDate,
} from '@/lib/format';
import { getErrorMessage } from '@/lib/http';
import type { RelatorioDia, Venda } from '@/lib/types';
import { toast } from 'sonner';

const chartColors = ['#17313e', '#366f8c', '#f4b63d', '#6a8d73', '#b8402f'];

export default function Relatorios() {
  const [selectedDate, setSelectedDate] = useState(toInputDate());
  const [relatorio, setRelatorio] = useState<RelatorioDia | null>(null);
  const [vendas, setVendas] = useState<Venda[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadReport() {
      setIsLoading(true);

      try {
        const [relatorioResponse, vendasResponse] = await Promise.all([
          api.get<RelatorioDia>('/relatorios/dia', { params: { data: selectedDate } }),
          api.get<Venda[]>('/vendas', { params: { data: selectedDate } }),
        ]);

        setRelatorio(relatorioResponse.data);
        setVendas(vendasResponse.data);
      } catch (error) {
        toast.error(getErrorMessage(error, 'Nao foi possivel gerar o relatorio.'));
      } finally {
        setIsLoading(false);
      }
    }

    void loadReport();
  }, [selectedDate]);

  const ticketMedio = useMemo(() => {
    if (!relatorio || relatorio.quantidadeVendas === 0) {
      return 0;
    }

    return relatorio.total / relatorio.quantidadeVendas;
  }, [relatorio]);

  const hourlySales = useMemo(() => {
    const totalsByHour = new Map<number, number>();

    vendas.forEach((venda) => {
      const hour = new Date(venda.criadoEm).getHours();
      totalsByHour.set(hour, (totalsByHour.get(hour) ?? 0) + venda.total);
    });

    return Array.from(totalsByHour.entries())
      .sort(([hourA], [hourB]) => hourA - hourB)
      .map(([hour, total]) => ({
        hora: `${String(hour).padStart(2, '0')}:00`,
        total,
      }));
  }, [vendas]);

  return (
    <DashboardLayout
      currentPage="Relatorios"
      title="Relatorios gerenciais"
      subtitle="Acompanhe volume, ticket medio e distribuicao das vendas para tomar decisoes melhores."
    >
      <div className="space-y-6">
        <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Consulta diaria</p>
              <h2 className="mt-2 text-2xl font-black text-slate-900">Resumo por data</h2>
            </div>
            <div className="flex items-center gap-3">
              <input
                type="date"
                value={selectedDate}
                onChange={(event) => setSelectedDate(event.target.value)}
                className="h-12 rounded-2xl border border-slate-300 bg-white px-4 text-sm text-slate-900"
              />
              <div className="rounded-2xl bg-slate-100 px-4 py-3 text-sm text-slate-600">
                {formatDate(selectedDate)}
              </div>
            </div>
          </div>
        </Card>

        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Faturamento</p>
            <p className="mt-3 text-3xl font-black text-slate-900">
              {isLoading ? '...' : formatCurrency(relatorio?.total)}
            </p>
          </Card>
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Quantidade de vendas</p>
            <p className="mt-3 text-3xl font-black text-slate-900">
              {isLoading ? '...' : relatorio?.quantidadeVendas ?? 0}
            </p>
          </Card>
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Ticket medio</p>
            <p className="mt-3 text-3xl font-black text-slate-900">{formatCurrency(ticketMedio)}</p>
          </Card>
          <Card className="rounded-[1.75rem] border-0 bg-[#17313e] p-6 text-white shadow-sm">
            <p className="text-sm uppercase tracking-[0.2em] text-slate-300">Maior participacao</p>
            <p className="mt-3 text-lg font-semibold">
              {relatorio?.totaisPorFormaPagamento?.[0]
                ? formatPaymentLabel(relatorio.totaisPorFormaPagamento[0].formaPagamento)
                : 'Sem dados'}
            </p>
          </Card>
        </div>

        <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <h2 className="text-2xl font-black text-slate-900">Vendas ao longo do dia</h2>
            <p className="mt-2 text-sm text-slate-600">
              Visualizacao por hora para perceber picos e preparar o atendimento.
            </p>
            <div className="mt-6 h-[320px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={hourlySales}>
                  <XAxis dataKey="hora" stroke="#64748b" />
                  <YAxis stroke="#64748b" />
                  <Tooltip formatter={(value: number) => formatCurrency(value)} />
                  <Bar dataKey="total" radius={[10, 10, 0, 0]} fill="#17313e" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>

          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <h2 className="text-2xl font-black text-slate-900">Participacao por pagamento</h2>
            <p className="mt-2 text-sm text-slate-600">
              Entenda como os clientes estao pagando para ajustar operacao e conferencias.
            </p>
            <div className="mt-6 h-[320px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={relatorio?.totaisPorFormaPagamento ?? []}
                    cx="50%"
                    cy="50%"
                    outerRadius={100}
                    innerRadius={55}
                    paddingAngle={4}
                    dataKey="total"
                    nameKey="formaPagamento"
                  >
                    {(relatorio?.totaisPorFormaPagamento ?? []).map((item, index) => (
                      <Cell
                        key={item.formaPagamento}
                        fill={chartColors[index % chartColors.length]}
                      />
                    ))}
                  </Pie>
                  <Tooltip
                    formatter={(value: number, _name, entry) => [
                      formatCurrency(value),
                      formatPaymentLabel(entry.payload.formaPagamento),
                    ]}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="mt-4 space-y-3">
              {(relatorio?.totaisPorFormaPagamento ?? []).map((item, index) => (
                <div
                  key={item.formaPagamento}
                  className="flex items-center justify-between rounded-2xl bg-slate-50 px-4 py-3 text-sm"
                >
                  <div className="flex items-center gap-3">
                    <span
                      className="h-3 w-3 rounded-full"
                      style={{ backgroundColor: chartColors[index % chartColors.length] }}
                    />
                    <span>{formatPaymentLabel(item.formaPagamento)}</span>
                  </div>
                  <strong>{formatCurrency(item.total)}</strong>
                </div>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
