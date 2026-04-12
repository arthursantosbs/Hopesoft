import { useEffect, useState } from 'react';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import api from '@/lib/api';
import { formatCurrency, formatDateTime } from '@/lib/format';
import { getErrorMessage } from '@/lib/http';
import type { CaixaSessao } from '@/lib/types';
import { toast } from 'sonner';

export default function Caixa() {
  const [sessaoAtual, setSessaoAtual] = useState<CaixaSessao | null>(null);
  const [historico, setHistorico] = useState<CaixaSessao[]>([]);
  const [fundoTrocoInicial, setFundoTrocoInicial] = useState('100.00');
  const [valorMovimento, setValorMovimento] = useState('0.00');
  const [valorFechamento, setValorFechamento] = useState('0.00');
  const [observacao, setObservacao] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  async function loadData() {
    setIsLoading(true);

    try {
      const [sessaoResponse, historicoResponse] = await Promise.all([
        api.get<CaixaSessao>('/caixa/sessao-atual', { validateStatus: (status) => status === 200 || status === 204 }),
        api.get<CaixaSessao[]>('/caixa/historico'),
      ]);

      setSessaoAtual(sessaoResponse.status === 204 ? null : sessaoResponse.data);
      setHistorico(historicoResponse.data);
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel carregar o caixa.'));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  const abrirCaixa = async () => {
    try {
      await api.post('/caixa/abrir', {
        fundoTrocoInicial: Number(fundoTrocoInicial),
        observacao,
      });
      toast.success('Caixa aberto com sucesso.');
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel abrir o caixa.'));
    }
  };

  const registrarMovimento = async (tipo: 'sangria' | 'suprimento') => {
    try {
      await api.post(`/caixa/${tipo}`, {
        valor: Number(valorMovimento),
        observacao,
      });
      toast.success(tipo === 'sangria' ? 'Sangria registrada.' : 'Suprimento registrado.');
      setValorMovimento('0.00');
      setObservacao('');
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, `Nao foi possivel registrar ${tipo}.`));
    }
  };

  const fecharCaixa = async () => {
    try {
      await api.post('/caixa/fechar', {
        valorInformadoFechamento: Number(valorFechamento),
        observacao,
      });
      toast.success('Caixa fechado com sucesso.');
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel fechar o caixa.'));
    }
  };

  return (
    <DashboardLayout
      currentPage="Caixa"
      title="Turno de caixa"
      subtitle="Abra, acompanhe e feche o caixa com conferencia cega, sangrias e suprimentos."
    >
      <div className="space-y-6">
        <div className="grid gap-4 xl:grid-cols-[1fr_1fr]">
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Sessao atual</p>
            {isLoading ? (
              <p className="mt-4 text-sm text-slate-500">Carregando sessao...</p>
            ) : sessaoAtual ? (
              <div className="mt-6 grid gap-4 md:grid-cols-2">
                <div>
                  <p className="text-sm text-slate-500">Aberto por</p>
                  <p className="text-lg font-bold text-slate-900">{sessaoAtual.usuarioAbertura}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Aberto em</p>
                  <p className="text-lg font-bold text-slate-900">{formatDateTime(sessaoAtual.abertoEm)}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Fundo inicial</p>
                  <p className="text-lg font-bold text-slate-900">{formatCurrency(sessaoAtual.fundoTrocoInicial)}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Esperado em caixa</p>
                  <p className="text-lg font-bold text-emerald-700">{formatCurrency(sessaoAtual.valorEsperadoFechamento)}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Sangrias</p>
                  <p className="text-lg font-bold text-slate-900">{formatCurrency(sessaoAtual.totalSangrias)}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Suprimentos</p>
                  <p className="text-lg font-bold text-slate-900">{formatCurrency(sessaoAtual.totalSuprimentos)}</p>
                </div>
              </div>
            ) : (
              <div className="mt-6 rounded-3xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-500">
                Nenhum caixa aberto no momento.
              </div>
            )}
          </Card>

          <Card className="rounded-[1.75rem] border-0 bg-[#17313e] p-6 text-white shadow-sm">
            <p className="text-sm uppercase tracking-[0.2em] text-slate-300">Operacoes rapidas</p>
            <div className="mt-6 space-y-4">
              {!sessaoAtual && (
                <>
                  <label>
                    <span className="mb-2 block text-sm font-semibold">Fundo de troco inicial</span>
                    <Input
                      type="number"
                      value={fundoTrocoInicial}
                      onChange={(event) => setFundoTrocoInicial(event.target.value)}
                      className="h-12 rounded-2xl border-white/20 bg-white text-slate-900"
                    />
                  </label>
                  <Button className="w-full rounded-2xl bg-[#f4b63d] text-[#17313e] hover:bg-[#e6a832]" onClick={abrirCaixa}>
                    Abrir caixa
                  </Button>
                </>
              )}

              {sessaoAtual && (
                <>
                  <label>
                    <span className="mb-2 block text-sm font-semibold">Valor do movimento</span>
                    <Input
                      type="number"
                      value={valorMovimento}
                      onChange={(event) => setValorMovimento(event.target.value)}
                      className="h-12 rounded-2xl border-white/20 bg-white text-slate-900"
                    />
                  </label>
                  <label>
                    <span className="mb-2 block text-sm font-semibold">Observacao</span>
                    <Input
                      value={observacao}
                      onChange={(event) => setObservacao(event.target.value)}
                      className="h-12 rounded-2xl border-white/20 bg-white text-slate-900"
                    />
                  </label>
                  <div className="grid gap-3 md:grid-cols-2">
                    <Button className="rounded-2xl bg-[#b8402f] text-white hover:bg-[#a53a2b]" onClick={() => registrarMovimento('sangria')}>
                      Registrar sangria
                    </Button>
                    <Button className="rounded-2xl bg-[#366f8c] text-white hover:bg-[#2d5e77]" onClick={() => registrarMovimento('suprimento')}>
                      Registrar suprimento
                    </Button>
                  </div>
                  <label>
                    <span className="mb-2 block text-sm font-semibold">Valor contado no fechamento</span>
                    <Input
                      type="number"
                      value={valorFechamento}
                      onChange={(event) => setValorFechamento(event.target.value)}
                      className="h-12 rounded-2xl border-white/20 bg-white text-slate-900"
                    />
                  </label>
                  <Button className="w-full rounded-2xl bg-[#f4b63d] text-[#17313e] hover:bg-[#e6a832]" onClick={fecharCaixa}>
                    Fechar caixa
                  </Button>
                </>
              )}
            </div>
          </Card>
        </div>

        <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Historico</p>
          <div className="mt-6 space-y-3">
            {historico.length === 0 ? (
              <p className="text-sm text-slate-500">Nenhuma sessao registrada ainda.</p>
            ) : (
              historico.slice(0, 6).map((sessao) => (
                <div key={sessao.id} className="rounded-3xl border border-slate-100 bg-slate-50 p-4">
                  <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                    <div>
                      <p className="font-semibold text-slate-900">Caixa #{sessao.id} • {sessao.status}</p>
                      <p className="text-sm text-slate-600">
                        {formatDateTime(sessao.abertoEm)} • {sessao.usuarioAbertura}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-slate-500">Esperado no fechamento</p>
                      <p className="text-lg font-bold text-slate-900">{formatCurrency(sessao.valorEsperadoFechamento)}</p>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </Card>
      </div>
    </DashboardLayout>
  );
}
