import { useEffect, useMemo, useState } from 'react';
import { AlertTriangle, Boxes, DollarSign, Package, ShoppingCart } from 'lucide-react';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { Card } from '@/components/ui/card';
import api from '@/lib/api';
import { formatCurrency, formatDateTime, formatPaymentLabel, toInputDate } from '@/lib/format';
import { getErrorMessage } from '@/lib/http';
import type { Categoria, Produto, RelatorioDia, Venda } from '@/lib/types';
import { toast } from 'sonner';

interface DashboardData {
  relatorio: RelatorioDia | null;
  vendas: Venda[];
  produtos: Produto[];
  categorias: Categoria[];
  estoqueBaixo: Produto[];
}

export default function Dashboard() {
  const [data, setData] = useState<DashboardData>({
    relatorio: null,
    vendas: [],
    produtos: [],
    categorias: [],
    estoqueBaixo: [],
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadDashboard() {
      setIsLoading(true);
      const hoje = toInputDate();

      try {
        const [relatorioResponse, vendasResponse, produtosResponse, categoriasResponse, estoqueResponse] =
          await Promise.all([
            api.get<RelatorioDia>('/relatorios/dia', { params: { data: hoje } }),
            api.get<Venda[]>('/vendas', { params: { data: hoje } }),
            api.get<Produto[]>('/produtos'),
            api.get<Categoria[]>('/categorias'),
            api.get<Produto[]>('/produtos/estoque/baixo'),
          ]);

        setData({
          relatorio: relatorioResponse.data,
          vendas: vendasResponse.data,
          produtos: produtosResponse.data,
          categorias: categoriasResponse.data,
          estoqueBaixo: estoqueResponse.data,
        });
      } catch (error) {
        toast.error(getErrorMessage(error, 'Nao foi possivel carregar o dashboard.'));
      } finally {
        setIsLoading(false);
      }
    }

    void loadDashboard();
  }, []);

  const ticketMedio = useMemo(() => {
    if (!data.relatorio || data.relatorio.quantidadeVendas === 0) {
      return 0;
    }

    return data.relatorio.total / data.relatorio.quantidadeVendas;
  }, [data.relatorio]);

  const metrics = [
    {
      label: 'Faturamento do dia',
      value: formatCurrency(data.relatorio?.total),
      icon: DollarSign,
      tone: 'bg-emerald-50 text-emerald-700',
    },
    {
      label: 'Vendas realizadas',
      value: String(data.relatorio?.quantidadeVendas ?? 0),
      icon: ShoppingCart,
      tone: 'bg-blue-50 text-blue-700',
    },
    {
      label: 'Produtos ativos',
      value: String(data.produtos.length),
      icon: Package,
      tone: 'bg-orange-50 text-orange-700',
    },
    {
      label: 'Ticket medio',
      value: formatCurrency(ticketMedio),
      icon: Boxes,
      tone: 'bg-violet-50 text-violet-700',
    },
  ];

  const getResumoPagamento = (venda: Venda) => {
    if (venda.pagamentos.length === 0) {
      return venda.formaPagamento ? formatPaymentLabel(venda.formaPagamento) : 'Sem pagamento';
    }

    if (venda.pagamentos.length === 1) {
      return formatPaymentLabel(venda.pagamentos[0].formaPagamento);
    }

    return `${venda.pagamentos.length} formas`;
  };

  return (
    <DashboardLayout
      currentPage="Dashboard"
      title="Painel do dia"
      subtitle="Visao direta do caixa, estoque e ritmo da operacao para voce agir rapido."
    >
      <div className="space-y-6">
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {metrics.map((metric) => {
            const Icon = metric.icon;

            return (
              <Card key={metric.label} className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-500">{metric.label}</p>
                    <p className="mt-3 text-3xl font-black text-slate-900">
                      {isLoading ? '...' : metric.value}
                    </p>
                  </div>
                  <div className={`rounded-2xl p-3 ${metric.tone}`}>
                    <Icon className="h-6 w-6" />
                  </div>
                </div>
              </Card>
            );
          })}
        </div>

        <div className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Ultimas vendas</p>
                <h2 className="mt-2 text-2xl font-black text-slate-900">Movimento de hoje</h2>
              </div>
              <div className="rounded-2xl bg-slate-100 px-4 py-2 text-sm text-slate-600">
                {data.vendas.length} vendas registradas hoje
              </div>
            </div>

            <div className="mt-6 space-y-3">
              {isLoading ? (
                <p className="text-sm text-slate-500">Carregando vendas...</p>
              ) : data.vendas.length === 0 ? (
                <div className="rounded-3xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center text-sm text-slate-500">
                  Nenhuma venda registrada hoje ainda.
                </div>
              ) : (
                data.vendas.slice(0, 6).map((venda) => (
                  <div
                    key={venda.id}
                    className="flex flex-col gap-3 rounded-3xl border border-slate-100 bg-slate-50 p-4 md:flex-row md:items-center md:justify-between"
                  >
                    <div>
                      <p className="font-semibold text-slate-900">Venda #{venda.id}</p>
                      <p className="text-sm text-slate-600">
                        {venda.usuarioNome} • {formatDateTime(venda.criadoEm)}
                      </p>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="rounded-full bg-white px-3 py-1 text-xs font-semibold text-slate-600">
                        {getResumoPagamento(venda)}
                      </span>
                      <span className="text-lg font-black text-slate-900">
                        {formatCurrency(venda.total)}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </Card>

          <div className="space-y-6">
            <Card className="rounded-[1.75rem] border-0 bg-[#17313e] p-6 text-white shadow-sm">
              <p className="text-sm uppercase tracking-[0.2em] text-slate-300">Formas de pagamento</p>
              <div className="mt-5 space-y-3">
                {(data.relatorio?.totaisPorFormaPagamento ?? []).map((item) => (
                  <div
                    key={item.formaPagamento}
                    className="flex items-center justify-between rounded-2xl bg-white/10 px-4 py-3"
                  >
                    <span>{formatPaymentLabel(item.formaPagamento)}</span>
                    <strong>{formatCurrency(item.total)}</strong>
                  </div>
                ))}
                {!isLoading && (data.relatorio?.totaisPorFormaPagamento?.length ?? 0) === 0 && (
                  <p className="text-sm text-slate-300">Sem movimento por forma de pagamento hoje.</p>
                )}
              </div>
            </Card>

            <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
              <div className="flex items-center gap-3">
                <div className="rounded-2xl bg-amber-100 p-3 text-amber-700">
                  <AlertTriangle className="h-5 w-5" />
                </div>
                <div>
                  <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Reposicao</p>
                  <h2 className="text-xl font-black text-slate-900">Estoque baixo</h2>
                </div>
              </div>

              <div className="mt-5 space-y-3">
                {isLoading ? (
                  <p className="text-sm text-slate-500">Carregando alertas...</p>
                ) : data.estoqueBaixo.length === 0 ? (
                  <div className="rounded-3xl bg-emerald-50 p-4 text-sm text-emerald-700">
                    Nenhum item em estoque baixo no momento.
                  </div>
                ) : (
                  data.estoqueBaixo.slice(0, 5).map((produto) => (
                    <div
                      key={produto.id}
                      className="rounded-3xl border border-amber-200 bg-amber-50 p-4 text-sm"
                    >
                      <p className="font-semibold text-slate-900">{produto.nome}</p>
                      <p className="mt-1 text-amber-700">
                        Estoque {produto.estoque} • minimo {produto.estoqueMin}
                      </p>
                    </div>
                  ))
                )}
              </div>
            </Card>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
