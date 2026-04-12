import { useEffect, useMemo, useRef, useState } from 'react';
import { Minus, PauseCircle, Plus, Printer, Receipt, Search, Trash2 } from 'lucide-react';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import api from '@/lib/api';
import {
  formatCurrency,
  formatDateTime,
  formatPaymentLabel,
  toInputDate,
} from '@/lib/format';
import { getErrorMessage } from '@/lib/http';
import type {
  ComprovanteResponse,
  DocumentoFiscal,
  FormaPagamento,
  Produto,
  TipoDocumentoFiscal,
  ValeTroca,
  Venda,
  VendaPayload,
} from '@/lib/types';
import { toast } from 'sonner';

interface CartItem {
  produto: Produto;
  quantidade: number;
  descontoValor: string;
}

interface PaymentLine {
  formaPagamento: FormaPagamento;
  valor: string;
  valorRecebido: string;
  parcelas: string;
  codigoValeTroca: string;
}

const paymentOptions: FormaPagamento[] = [
  'DINHEIRO',
  'PIX',
  'CARTAO_DEBITO',
  'CARTAO_CREDITO',
  'FIADO',
  'VALE_TROCA',
];

const emptyPayment = (formaPagamento: FormaPagamento = 'PIX'): PaymentLine => ({
  formaPagamento,
  valor: '0.00',
  valorRecebido: '',
  parcelas: '',
  codigoValeTroca: '',
});

export default function Vendas() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [vendasHoje, setVendasHoje] = useState<Venda[]>([]);
  const [vendasEmEspera, setVendasEmEspera] = useState<Venda[]>([]);
  const [valesAtivos, setValesAtivos] = useState<ValeTroca[]>([]);
  const [search, setSearch] = useState('');
  const [cart, setCart] = useState<CartItem[]>([]);
  const [payments, setPayments] = useState<PaymentLine[]>([emptyPayment()]);
  const [saleDiscount, setSaleDiscount] = useState('0.00');
  const [saleAddition, setSaleAddition] = useState('0.00');
  const [observacao, setObservacao] = useState('');
  const [managerPassword, setManagerPassword] = useState('');
  const [pendingSaleId, setPendingSaleId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [scannerStatus, setScannerStatus] = useState('Scanner pronto');
  const [voucherSale, setVoucherSale] = useState<Venda | null>(null);
  const [voucherQuantities, setVoucherQuantities] = useState<Record<number, string>>({});
  const [voucherCustomerName, setVoucherCustomerName] = useState('');
  const [voucherCustomerDocument, setVoucherCustomerDocument] = useState('');
  const [voucherObservation, setVoucherObservation] = useState('');
  const [selectedFiscalType, setSelectedFiscalType] = useState<TipoDocumentoFiscal>('NFCE');
  const searchInputRef = useRef<HTMLInputElement | null>(null);
  const scannerBufferRef = useRef('');
  const scannerTimerRef = useRef<number | null>(null);

  async function loadData() {
    setIsLoading(true);

    try {
      const hoje = toInputDate();
      const [produtosResponse, vendasResponse, esperaResponse, valesResponse] = await Promise.all([
        api.get<Produto[]>('/produtos'),
        api.get<Venda[]>('/vendas', { params: { data: hoje } }),
        api.get<Venda[]>('/vendas/em-espera'),
        api.get<ValeTroca[]>('/vales-troca'),
      ]);

      setProdutos(produtosResponse.data);
      setVendasHoje(vendasResponse.data);
      setVendasEmEspera(esperaResponse.data);
      setValesAtivos(valesResponse.data);
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel carregar o caixa.'));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  useEffect(() => {
    if (payments.length === 1) {
      setPayments((current) =>
        current.map((payment, index) =>
          index === 0 ? { ...payment, valor: totalFinal.toFixed(2) } : payment
        )
      );
    }
  }, [cart.length, saleDiscount, saleAddition]);

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'F2') {
        event.preventDefault();
        searchInputRef.current?.focus();
        setScannerStatus('Pesquisa rapida ativada');
        return;
      }

      const target = event.target as HTMLElement | null;
      const isInteractive = target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(target.tagName);
      if (isInteractive || event.ctrlKey || event.metaKey || event.altKey) {
        return;
      }

      if (event.key === 'Enter') {
        const code = scannerBufferRef.current.trim();
        if (code.length >= 6) {
          event.preventDefault();
          void buscarProdutoPorCodigo(code);
        }
        scannerBufferRef.current = '';
        return;
      }

      if (event.key.length === 1) {
        scannerBufferRef.current += event.key;
        setScannerStatus(`Lendo codigo: ${scannerBufferRef.current}`);

        if (scannerTimerRef.current) {
          window.clearTimeout(scannerTimerRef.current);
        }

        scannerTimerRef.current = window.setTimeout(() => {
          scannerBufferRef.current = '';
          setScannerStatus('Scanner pronto');
        }, 350);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      if (scannerTimerRef.current) {
        window.clearTimeout(scannerTimerRef.current);
      }
    };
  }, [produtos]);

  async function buscarProdutoPorCodigo(codigoBarras: string) {
    try {
      const response = await api.get<Produto>(`/produtos/codigo-barras/${codigoBarras}`);
      addToCart(response.data);
      setScannerStatus(`Produto lido: ${response.data.nome}`);
    } catch {
      setScannerStatus('Codigo nao encontrado');
      toast.error('Codigo de barras nao encontrado.');
    }
  }

  const produtosFiltrados = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return produtos;
    }

    return produtos.filter((produto) =>
      [
        produto.nome,
        produto.modelo,
        produto.referencia,
        produto.codigoBarras ?? '',
        produto.cor ?? '',
        produto.tamanho ?? '',
        produto.categoria?.nome ?? '',
      ]
        .join(' ')
        .toLowerCase()
        .includes(query)
    );
  }, [produtos, search]);

  const subtotalBruto = useMemo(
    () => cart.reduce((accumulator, item) => accumulator + item.produto.preco * item.quantidade, 0),
    [cart]
  );

  const descontosItens = useMemo(
    () => cart.reduce((accumulator, item) => accumulator + Number(item.descontoValor || 0), 0),
    [cart]
  );

  const totalFinal = useMemo(() => {
    const descontoVenda = Number(saleDiscount || 0);
    const acrescimoVenda = Number(saleAddition || 0);
    return Math.max(subtotalBruto - descontosItens - descontoVenda + acrescimoVenda, 0);
  }, [subtotalBruto, descontosItens, saleDiscount, saleAddition]);

  const totalPagamentos = useMemo(
    () => payments.reduce((accumulator, payment) => accumulator + Number(payment.valor || 0), 0),
    [payments]
  );

  const troco = useMemo(
    () =>
      payments
        .filter((payment) => payment.formaPagamento === 'DINHEIRO')
        .reduce(
          (accumulator, payment) =>
            accumulator + Math.max(Number(payment.valorRecebido || 0) - Number(payment.valor || 0), 0),
          0
        ),
    [payments]
  );

  const addToCart = (produto: Produto) => {
    if (produto.estoque <= 0) {
      toast.error('Produto sem estoque disponivel.');
      return;
    }

    setCart((currentCart) => {
      const existingItem = currentCart.find((item) => item.produto.id === produto.id);

      if (!existingItem) {
        return [...currentCart, { produto, quantidade: 1, descontoValor: '0.00' }];
      }

      if (existingItem.quantidade >= produto.estoque) {
        toast.error('Quantidade acima do estoque disponivel.');
        return currentCart;
      }

      return currentCart.map((item) =>
        item.produto.id === produto.id ? { ...item, quantidade: item.quantidade + 1 } : item
      );
    });
  };

  const updateQuantity = (produtoId: number, delta: number) => {
    setCart((currentCart) =>
      currentCart
        .map((item) => {
          if (item.produto.id !== produtoId) {
            return item;
          }

          const nextQuantity = item.quantidade + delta;

          if (nextQuantity > item.produto.estoque) {
            toast.error('Quantidade acima do estoque disponivel.');
            return item;
          }

          return { ...item, quantidade: nextQuantity };
        })
        .filter((item) => item.quantidade > 0)
    );
  };

  const updateItemDiscount = (produtoId: number, descontoValor: string) => {
    setCart((currentCart) =>
      currentCart.map((item) =>
        item.produto.id === produtoId ? { ...item, descontoValor } : item
      )
    );
  };

  const removeFromCart = (produtoId: number) => {
    setCart((currentCart) => currentCart.filter((item) => item.produto.id !== produtoId));
  };

  const clearCart = () => {
    setCart([]);
    setPayments([emptyPayment()]);
    setSaleDiscount('0.00');
    setSaleAddition('0.00');
    setObservacao('');
    setManagerPassword('');
    setPendingSaleId(null);
  };

  const addPaymentLine = () => {
    setPayments((current) => [...current, emptyPayment()]);
  };

  const updatePaymentLine = (index: number, patch: Partial<PaymentLine>) => {
    setPayments((current) =>
      current.map((payment, paymentIndex) =>
        paymentIndex === index ? { ...payment, ...patch } : payment
      )
    );
  };

  const removePaymentLine = (index: number) => {
    setPayments((current) => current.filter((_, paymentIndex) => paymentIndex !== index));
  };

  const distribuirPagamentoUnico = (formaPagamento: FormaPagamento) => {
    setPayments([
      {
        formaPagamento,
        valor: totalFinal.toFixed(2),
        valorRecebido: formaPagamento === 'DINHEIRO' ? totalFinal.toFixed(2) : '',
        parcelas: '',
        codigoValeTroca: '',
      },
    ]);
  };

  const fillFromPendingSale = (sale: Venda) => {
    const cartFromSale: CartItem[] = sale.itens
      .map((item) => {
        const produto = produtos.find((product) => product.id === item.produtoId);
        if (!produto) {
          return null;
        }

        return {
          produto,
          quantidade: item.quantidade,
          descontoValor: item.descontoValor.toFixed(2),
        };
      })
      .filter(Boolean) as CartItem[];

    setCart(cartFromSale);
    setPayments([emptyPayment()]);
    setSaleDiscount((sale.descontoTotal - sale.itens.reduce((acc, item) => acc + item.descontoValor, 0)).toFixed(2));
    setSaleAddition(sale.acrescimoTotal.toFixed(2));
    setObservacao(sale.observacao ?? '');
    setPendingSaleId(sale.id);
    toast.success(`Venda em espera #${sale.id} carregada no caixa.`);
  };

  const cancelarVendaEmEspera = async (saleId: number) => {
    try {
      await api.delete(`/vendas/${saleId}/em-espera`);
      if (pendingSaleId === saleId) {
        clearCart();
      }
      toast.success(`Venda em espera #${saleId} cancelada.`);
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel cancelar a venda em espera.'));
    }
  };

  const abrirPainelValeTroca = (sale: Venda) => {
    const initialQuantities = sale.itens.reduce<Record<number, string>>((accumulator, item) => {
      const disponivel = item.quantidade - item.quantidadeDevolvida;
      if (disponivel > 0) {
        accumulator[item.produtoId] = '0';
      }
      return accumulator;
    }, {});

    setVoucherSale(sale);
    setVoucherQuantities(initialQuantities);
    setVoucherCustomerName('');
    setVoucherCustomerDocument('');
    setVoucherObservation('');
  };

  const fecharPainelValeTroca = () => {
    setVoucherSale(null);
    setVoucherQuantities({});
    setVoucherCustomerName('');
    setVoucherCustomerDocument('');
    setVoucherObservation('');
  };

  const atualizarQuantidadeVale = (produtoId: number, valor: string) => {
    setVoucherQuantities((current) => ({ ...current, [produtoId]: valor }));
  };

  const gerarValeTroca = async () => {
    if (!voucherSale) {
      return;
    }

    const itens = voucherSale.itens
      .map((item) => ({
        produtoId: item.produtoId,
        quantidade: Number(voucherQuantities[item.produtoId] || 0),
        disponivel: item.quantidade - item.quantidadeDevolvida,
      }))
      .filter((item) => item.quantidade > 0);

    if (itens.length === 0) {
      toast.error('Selecione ao menos um item para gerar o vale-troca.');
      return;
    }

    const itemInvalido = itens.find((item) => item.quantidade > item.disponivel);
    if (itemInvalido) {
      toast.error('Uma das quantidades de devolucao esta acima do disponivel para troca.');
      return;
    }

    try {
      const response = await api.post<ValeTroca>(`/vendas/${voucherSale.id}/vale-troca`, {
        itens: itens.map((item) => ({
          produtoId: item.produtoId,
          quantidade: item.quantidade,
        })),
        nomeCliente: voucherCustomerName || null,
        documentoCliente: voucherCustomerDocument || null,
        observacao: voucherObservation || null,
      });

      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(response.data.codigo);
      }

      toast.success(`Vale-troca ${response.data.codigo} gerado e copiado.`);
      fecharPainelValeTroca();
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel gerar o vale-troca.'));
    }
  };

  const imprimirComprovante = async (saleId: number) => {
    try {
      const response = await api.get<ComprovanteResponse>(`/vendas/${saleId}/comprovante`);
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(response.data.conteudo);
      }
      toast.success('Comprovante gerado e copiado para impressao.');
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel gerar o comprovante.'));
    }
  };

  const emitirDocumentoFiscal = async (saleId: number) => {
    try {
      const response = await api.post<DocumentoFiscal>(
        `/fiscal/vendas/${saleId}/emitir`,
        null,
        { params: { tipoDocumento: selectedFiscalType } }
      );
      toast.success(`Documento ${response.data.tipoDocumento} registrado com status ${response.data.status}.`);
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel preparar o documento fiscal.'));
    }
  };

  const buildPayload = (vendaEmEspera: boolean): VendaPayload => ({
    itens: cart.map((item) => ({
      produtoId: item.produto.id,
      quantidade: item.quantidade,
      descontoValor: Number(item.descontoValor || 0),
    })),
    pagamentos: vendaEmEspera
      ? []
      : payments
          .filter((payment) => Number(payment.valor || 0) > 0)
          .map((payment) => ({
            formaPagamento: payment.formaPagamento,
            valor: Number(payment.valor),
            valorRecebido:
              payment.formaPagamento === 'DINHEIRO' && payment.valorRecebido
                ? Number(payment.valorRecebido)
                : undefined,
            parcelas:
              payment.formaPagamento === 'CARTAO_CREDITO' && payment.parcelas
                ? Number(payment.parcelas)
                : undefined,
            codigoValeTroca:
              payment.formaPagamento === 'VALE_TROCA' ? payment.codigoValeTroca || undefined : undefined,
          })),
    descontoValor: Number(saleDiscount || 0),
    acrescimoValor: Number(saleAddition || 0),
    vendaEmEspera,
    observacao: observacao || null,
    senhaGerenteAutorizacao: managerPassword || null,
  });

  const submitVenda = async (vendaEmEspera: boolean) => {
    if (cart.length === 0) {
      toast.error('Adicione ao menos um produto antes de continuar.');
      return;
    }

    if (!vendaEmEspera && Math.abs(totalPagamentos - totalFinal) > 0.009) {
      toast.error('A soma dos pagamentos precisa bater com o total da venda.');
      return;
    }

    setIsSubmitting(true);

    try {
      if (pendingSaleId && !vendaEmEspera) {
        await api.put(`/vendas/${pendingSaleId}/finalizar`, buildPayload(false));
      } else {
        await api.post('/vendas', buildPayload(vendaEmEspera));
      }

      toast.success(vendaEmEspera ? 'Venda enviada para espera.' : 'Venda registrada com sucesso.');
      clearCart();
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel concluir a venda.'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <DashboardLayout
      currentPage="Vendas"
      title="Caixa PDV"
      subtitle="Leitura por codigo, F2 para busca, pagamentos mistos e venda em espera para o fluxo do balcão."
    >
      <div className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
        <div className="space-y-6">
          {voucherSale && (
            <Card className="rounded-[1.75rem] border border-amber-200 bg-amber-50 p-6 shadow-sm">
              <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                <div>
                  <p className="text-sm uppercase tracking-[0.2em] text-amber-700">Trocas e devolucoes</p>
                  <h2 className="mt-2 text-2xl font-black text-slate-900">Gerar vale da venda #{voucherSale.id}</h2>
                </div>
                <Button variant="outline" className="rounded-2xl" onClick={fecharPainelValeTroca}>
                  Fechar painel
                </Button>
              </div>

              <div className="mt-5 grid gap-3 md:grid-cols-3">
                <Input
                  value={voucherCustomerName}
                  onChange={(event) => setVoucherCustomerName(event.target.value)}
                  className="h-12 rounded-2xl bg-white"
                  placeholder="Nome do cliente"
                />
                <Input
                  value={voucherCustomerDocument}
                  onChange={(event) => setVoucherCustomerDocument(event.target.value)}
                  className="h-12 rounded-2xl bg-white"
                  placeholder="CPF ou documento"
                />
                <Input
                  value={voucherObservation}
                  onChange={(event) => setVoucherObservation(event.target.value)}
                  className="h-12 rounded-2xl bg-white"
                  placeholder="Observacao"
                />
              </div>

              <div className="mt-5 space-y-3">
                {voucherSale.itens.map((item) => {
                  const disponivel = item.quantidade - item.quantidadeDevolvida;

                  return (
                    <div key={`${voucherSale.id}-${item.produtoId}`} className="grid gap-3 rounded-3xl border border-amber-200 bg-white p-4 md:grid-cols-[1fr_auto] md:items-center">
                      <div>
                        <p className="font-semibold text-slate-900">{item.produtoNome}</p>
                        <p className="text-sm text-slate-600">
                          Vendido {item.quantidade} • Ja devolvido {item.quantidadeDevolvida} • Disponivel {disponivel}
                        </p>
                      </div>
                      <Input
                        type="number"
                        min="0"
                        max={String(disponivel)}
                        step="1"
                        value={voucherQuantities[item.produtoId] ?? '0'}
                        onChange={(event) => atualizarQuantidadeVale(item.produtoId, event.target.value)}
                        className="h-12 w-full rounded-2xl bg-white md:w-28"
                      />
                    </div>
                  );
                })}
              </div>

              <div className="mt-5 flex justify-end">
                <Button className="rounded-2xl bg-[#17313e] text-white hover:bg-[#0f222d]" onClick={() => void gerarValeTroca()}>
                  Gerar vale-troca
                </Button>
              </div>
            </Card>
          )}

          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Vales ativos</p>
                <h2 className="mt-2 text-2xl font-black text-slate-900">Creditos para reaproveitar</h2>
              </div>
              <span className="rounded-2xl bg-slate-100 px-4 py-2 text-sm text-slate-600">{valesAtivos.length} ativos</span>
            </div>

            <div className="mt-6 space-y-3">
              {valesAtivos.length === 0 ? (
                <div className="rounded-3xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-500">
                  Nenhum vale-troca ativo no momento.
                </div>
              ) : (
                valesAtivos.slice(0, 5).map((vale) => (
                  <div key={vale.id} className="flex flex-col gap-3 rounded-3xl border border-slate-100 bg-slate-50 p-4 md:flex-row md:items-center md:justify-between">
                    <div>
                      <p className="font-semibold text-slate-900">{vale.codigo}</p>
                      <p className="text-sm text-slate-600">
                        Saldo {formatCurrency(vale.saldo)} • Venda #{vale.vendaOrigemId}
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      className="rounded-2xl"
                      onClick={async () => {
                        if (navigator.clipboard?.writeText) {
                          await navigator.clipboard.writeText(vale.codigo);
                        }
                        toast.success(`Codigo ${vale.codigo} copiado.`);
                      }}
                    >
                      Copiar codigo
                    </Button>
                  </div>
                ))
              )}
            </div>
          </Card>

          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Catalogo de venda</p>
                <h2 className="mt-2 text-2xl font-black text-slate-900">Scanner e pesquisa rapida</h2>
                <p className="mt-2 text-sm text-slate-600">
                  F2 foca a pesquisa. O scanner pode bipar a qualquer momento nesta tela.
                </p>
              </div>
              <div className="rounded-2xl bg-slate-100 px-4 py-3 text-sm text-slate-700">{scannerStatus}</div>
            </div>

            <div className="mt-6 relative">
              <Search className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
              <Input
                ref={searchInputRef}
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Buscar por nome, referencia, variante ou codigo"
                className="h-12 rounded-2xl pl-11"
              />
            </div>

            <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {isLoading ? (
                <p className="text-sm text-slate-500">Carregando produtos...</p>
              ) : produtosFiltrados.length === 0 ? (
                <div className="rounded-3xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-500">
                  Nenhum produto encontrado.
                </div>
              ) : (
                produtosFiltrados.slice(0, 12).map((produto) => (
                  <button
                    key={produto.id}
                    type="button"
                    onClick={() => addToCart(produto)}
                    className="rounded-[1.5rem] border border-slate-200 bg-[#fcfbf8] p-5 text-left transition hover:-translate-y-0.5 hover:border-[#f4b63d] hover:shadow-sm"
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <p className="font-semibold text-slate-900">{produto.modelo}</p>
                        <p className="mt-1 text-xs uppercase tracking-[0.15em] text-slate-500">
                          {produto.referencia} • {[produto.cor, produto.tamanho].filter(Boolean).join(' / ')}
                        </p>
                      </div>
                      <span className={`rounded-full px-3 py-1 text-xs font-semibold ${produto.estoqueBaixo ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'}`}>
                        estoque {produto.estoque}
                      </span>
                    </div>
                    <p className="mt-6 text-2xl font-black text-slate-900">{formatCurrency(produto.preco)}</p>
                    <p className="mt-2 text-sm text-slate-500">{produto.codigoBarras || 'Sem codigo de barras'}</p>
                  </button>
                ))
              )}
            </div>
          </Card>

          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Vendas em espera</p>
                <h2 className="mt-2 text-2xl font-black text-slate-900">Fila pausada</h2>
              </div>
              <span className="rounded-2xl bg-slate-100 px-4 py-2 text-sm text-slate-600">{vendasEmEspera.length} aguardando</span>
            </div>

            <div className="mt-6 space-y-3">
              {vendasEmEspera.length === 0 ? (
                <div className="rounded-3xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-500">
                  Nenhuma venda em espera no momento.
                </div>
              ) : (
                vendasEmEspera.map((venda) => (
                  <div key={venda.id} className="flex flex-col gap-3 rounded-3xl border border-slate-100 bg-slate-50 p-4 md:flex-row md:items-center md:justify-between">
                    <div>
                      <p className="font-semibold text-slate-900">Venda #{venda.id}</p>
                      <p className="text-sm text-slate-600">{venda.itens.length} itens • {formatCurrency(venda.total)}</p>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      <Button className="rounded-2xl bg-[#17313e] text-white hover:bg-[#0f222d]" onClick={() => fillFromPendingSale(venda)}>
                        Retomar no caixa
                      </Button>
                      <Button
                        variant="outline"
                        className="rounded-2xl border-red-200 text-red-700 hover:bg-red-50"
                        onClick={() => void cancelarVendaEmEspera(venda.id)}
                      >
                        Cancelar espera
                      </Button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </Card>
        </div>

        <div className="space-y-6">
          <Card className="rounded-[1.75rem] border-0 bg-[#17313e] p-6 text-white shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.2em] text-slate-300">Carrinho atual</p>
                <h2 className="mt-2 text-2xl font-black">{pendingSaleId ? `Retomando venda #${pendingSaleId}` : 'Resumo da venda'}</h2>
              </div>
              <Receipt className="h-8 w-8 text-[#f4b63d]" />
            </div>

            <div className="mt-6 space-y-3">
              {cart.length === 0 ? (
                <div className="rounded-3xl bg-white/10 p-5 text-sm text-slate-300">
                  Selecione um produto para iniciar a venda.
                </div>
              ) : (
                cart.map((item) => (
                  <div key={item.produto.id} className="rounded-3xl bg-white/10 p-4">
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <p className="font-semibold text-white">{item.produto.modelo}</p>
                        <p className="text-sm text-slate-300">{[item.produto.cor, item.produto.tamanho].filter(Boolean).join(' / ')}</p>
                      </div>
                      <button type="button" onClick={() => removeFromCart(item.produto.id)} className="rounded-full bg-white/10 p-2 text-slate-200 transition hover:bg-white/20">
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>

                    <div className="mt-4 grid gap-3 md:grid-cols-[auto_auto_auto_1fr] md:items-center">
                      <div className="flex items-center gap-2">
                        <Button variant="outline" className="rounded-xl border-white/20 bg-transparent text-white hover:bg-white/10" onClick={() => updateQuantity(item.produto.id, -1)}>
                          <Minus className="h-4 w-4" />
                        </Button>
                        <span className="w-10 text-center text-lg font-bold">{item.quantidade}</span>
                        <Button variant="outline" className="rounded-xl border-white/20 bg-transparent text-white hover:bg-white/10" onClick={() => updateQuantity(item.produto.id, 1)}>
                          <Plus className="h-4 w-4" />
                        </Button>
                      </div>
                      <span className="text-sm text-slate-200">{formatCurrency(item.produto.preco)} un.</span>
                      <Input
                        type="number"
                        min="0"
                        step="0.01"
                        value={item.descontoValor}
                        onChange={(event) => updateItemDiscount(item.produto.id, event.target.value)}
                        className="h-10 rounded-xl border-white/20 bg-white text-slate-900"
                        placeholder="Desconto item"
                      />
                      <strong className="text-right text-lg">{formatCurrency(item.produto.preco * item.quantidade - Number(item.descontoValor || 0))}</strong>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="mt-6 space-y-4 rounded-[1.5rem] bg-white p-5 text-slate-900">
              <div className="grid gap-3 md:grid-cols-2">
                <label>
                  <span className="mb-2 block text-sm font-semibold text-slate-700">Desconto no total</span>
                  <Input type="number" min="0" step="0.01" value={saleDiscount} onChange={(event) => setSaleDiscount(event.target.value)} className="h-12 rounded-2xl" />
                </label>
                <label>
                  <span className="mb-2 block text-sm font-semibold text-slate-700">Acrescimo</span>
                  <Input type="number" min="0" step="0.01" value={saleAddition} onChange={(event) => setSaleAddition(event.target.value)} className="h-12 rounded-2xl" />
                </label>
              </div>

              <label>
                <span className="mb-2 block text-sm font-semibold text-slate-700">Senha do gerente para desconto alto</span>
                <Input type="password" value={managerPassword} onChange={(event) => setManagerPassword(event.target.value)} className="h-12 rounded-2xl" />
              </label>

              <label>
                <span className="mb-2 block text-sm font-semibold text-slate-700">Observacao</span>
                <Input value={observacao} onChange={(event) => setObservacao(event.target.value)} className="h-12 rounded-2xl" />
              </label>

              <div>
                <div className="mb-3 flex items-center justify-between">
                  <h3 className="text-lg font-black text-slate-900">Pagamentos</h3>
                  <div className="flex gap-2">
                    <Button variant="outline" className="rounded-2xl" onClick={() => distribuirPagamentoUnico('PIX')}>
                      Pix total
                    </Button>
                    <Button variant="outline" className="rounded-2xl" onClick={() => distribuirPagamentoUnico('DINHEIRO')}>
                      Dinheiro total
                    </Button>
                  </div>
                </div>

                <div className="space-y-3">
                  {payments.map((payment, index) => (
                    <div key={`${payment.formaPagamento}-${index}`} className="rounded-3xl border border-slate-200 bg-slate-50 p-4">
                      <div className="grid gap-3 md:grid-cols-2">
                        <select
                          value={payment.formaPagamento}
                          onChange={(event) => updatePaymentLine(index, { formaPagamento: event.target.value as FormaPagamento })}
                          className="h-12 w-full rounded-2xl border border-slate-300 bg-white px-4 text-sm"
                        >
                          {paymentOptions.map((option) => (
                            <option key={option} value={option}>{formatPaymentLabel(option)}</option>
                          ))}
                        </select>
                        <Input type="number" min="0" step="0.01" value={payment.valor} onChange={(event) => updatePaymentLine(index, { valor: event.target.value })} className="h-12 rounded-2xl" placeholder="Valor" />
                        {payment.formaPagamento === 'DINHEIRO' && (
                          <Input type="number" min="0" step="0.01" value={payment.valorRecebido} onChange={(event) => updatePaymentLine(index, { valorRecebido: event.target.value })} className="h-12 rounded-2xl" placeholder="Valor recebido" />
                        )}
                        {payment.formaPagamento === 'CARTAO_CREDITO' && (
                          <Input type="number" min="1" step="1" value={payment.parcelas} onChange={(event) => updatePaymentLine(index, { parcelas: event.target.value })} className="h-12 rounded-2xl" placeholder="Parcelas" />
                        )}
                        {payment.formaPagamento === 'VALE_TROCA' && (
                          <Input value={payment.codigoValeTroca} onChange={(event) => updatePaymentLine(index, { codigoValeTroca: event.target.value })} className="h-12 rounded-2xl" placeholder="Codigo do vale" />
                        )}
                      </div>
                      {payments.length > 1 && (
                        <div className="mt-3 flex justify-end">
                          <Button variant="outline" className="rounded-2xl border-red-200 text-red-700 hover:bg-red-50" onClick={() => removePaymentLine(index)}>
                            Remover linha
                          </Button>
                        </div>
                      )}
                    </div>
                  ))}
                </div>

                <Button variant="outline" className="mt-3 rounded-2xl" onClick={addPaymentLine}>
                  Adicionar forma de pagamento
                </Button>
              </div>

              <div className="rounded-3xl bg-slate-100 p-5">
                <div className="flex items-center justify-between text-sm text-slate-600">
                  <span>Subtotal bruto</span>
                  <strong>{formatCurrency(subtotalBruto)}</strong>
                </div>
                <div className="mt-2 flex items-center justify-between text-sm text-slate-600">
                  <span>Descontos</span>
                  <strong>{formatCurrency(descontosItens + Number(saleDiscount || 0))}</strong>
                </div>
                <div className="mt-2 flex items-center justify-between text-sm text-slate-600">
                  <span>Acrescimos</span>
                  <strong>{formatCurrency(Number(saleAddition || 0))}</strong>
                </div>
                <div className="mt-4 flex items-center justify-between text-base font-semibold text-slate-900">
                  <span>Total</span>
                  <strong>{formatCurrency(totalFinal)}</strong>
                </div>
                <div className="mt-2 flex items-center justify-between text-sm text-slate-600">
                  <span>Pagamentos informados</span>
                  <strong className={Math.abs(totalPagamentos - totalFinal) > 0.009 ? 'text-red-600' : 'text-emerald-700'}>
                    {formatCurrency(totalPagamentos)}
                  </strong>
                </div>
              </div>

              <div className="rounded-[1.5rem] bg-[#f4b63d] p-5 text-center text-[#17313e]">
                <p className="text-sm font-semibold uppercase tracking-[0.2em]">Troco</p>
                <p className="mt-2 text-5xl font-black">{formatCurrency(troco)}</p>
              </div>

              <div className="grid gap-3">
                <Button onClick={() => void submitVenda(false)} disabled={isSubmitting || cart.length === 0} className="h-12 rounded-2xl bg-[#17313e] text-white hover:bg-[#0f222d]">
                  {isSubmitting ? 'Finalizando venda...' : pendingSaleId ? 'Finalizar venda retomada' : 'Finalizar venda'}
                </Button>
                <Button variant="outline" className="h-12 rounded-2xl" onClick={() => void submitVenda(true)}>
                  <PauseCircle className="mr-2 h-4 w-4" />
                  Colocar em espera
                </Button>
                <Button variant="outline" className="h-12 rounded-2xl" onClick={clearCart}>
                  Limpar carrinho
                </Button>
              </div>
            </div>
          </Card>

          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Historico do dia</p>
                <h2 className="mt-2 text-2xl font-black text-slate-900">Ultimas vendas</h2>
              </div>
              <div className="flex flex-col items-end gap-2">
                <span className="rounded-2xl bg-slate-100 px-4 py-2 text-sm text-slate-600">{vendasHoje.length} hoje</span>
                <select
                  value={selectedFiscalType}
                  onChange={(event) => setSelectedFiscalType(event.target.value as TipoDocumentoFiscal)}
                  className="h-10 rounded-2xl border border-slate-300 bg-white px-3 text-sm text-slate-900"
                >
                  <option value="NFCE">NFC-e</option>
                  <option value="SAT">SAT</option>
                  <option value="MFE">MFE</option>
                </select>
              </div>
            </div>

            <div className="mt-6 space-y-3">
              {vendasHoje.length === 0 ? (
                <div className="rounded-3xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-500">
                  Nenhuma venda concluida hoje.
                </div>
              ) : (
                vendasHoje.slice(0, 5).map((venda) => (
                  <div key={venda.id} className="rounded-3xl border border-slate-100 bg-slate-50 p-4">
                    <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                      <div>
                        <p className="font-semibold text-slate-900">Venda #{venda.id}</p>
                        <p className="text-sm text-slate-600">{formatDateTime(venda.criadoEm)} • {venda.itens.length} itens</p>
                      </div>
                      <strong className="text-lg text-slate-900">{formatCurrency(venda.total)}</strong>
                    </div>
                    <div className="mt-3 flex flex-wrap gap-2">
                      {venda.pagamentos.map((payment, index) => (
                        <span key={`${venda.id}-${payment.formaPagamento}-${index}`} className="rounded-full bg-white px-3 py-1 text-xs font-semibold text-slate-600">
                          {formatPaymentLabel(payment.formaPagamento)} {formatCurrency(payment.valor)}
                        </span>
                      ))}
                    </div>
                    <div className="mt-4 flex flex-wrap gap-2">
                      <Button variant="outline" className="rounded-2xl" onClick={() => void imprimirComprovante(venda.id)}>
                        <Printer className="mr-2 h-4 w-4" />
                        Comprovante
                      </Button>
                      <Button variant="outline" className="rounded-2xl" onClick={() => abrirPainelValeTroca(venda)}>
                        Vale-troca
                      </Button>
                      <Button variant="outline" className="rounded-2xl" onClick={() => void emitirDocumentoFiscal(venda.id)}>
                        Preparar {selectedFiscalType}
                      </Button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
