import { useEffect, useMemo, useState } from 'react';
import { Edit3, PackagePlus, Printer, RotateCcw, Search, Shirt, Trash2 } from 'lucide-react';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import api from '@/lib/api';
import { formatCurrency } from '@/lib/format';
import { getErrorMessage } from '@/lib/http';
import type { Categoria, EtiquetaResponse, Produto, ProdutoGradePayload, ProdutoPayload } from '@/lib/types';
import { toast } from 'sonner';

const emptyForm = {
  nome: '',
  modelo: '',
  referencia: '',
  cor: '',
  tamanho: '',
  codigoBarras: '',
  preco: '0.00',
  estoque: '0',
  estoqueMin: '0',
  categoriaId: '',
  ativo: true,
};

const emptyGradeForm = {
  nomeBase: '',
  modelo: '',
  referencia: '',
  precoBase: '0.00',
  estoqueMin: '0',
  categoriaId: '',
  cores: 'Azul, Branca, Preta',
  tamanhos: 'P, M, G, GG',
  estoqueInicialPadrao: '0',
};

type ProdutoFormState = typeof emptyForm;
type GradeFormState = typeof emptyGradeForm;

export default function Produtos() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [search, setSearch] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isSavingGrade, setIsSavingGrade] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<ProdutoFormState>(emptyForm);
  const [gradeForm, setGradeForm] = useState<GradeFormState>(emptyGradeForm);

  async function loadData() {
    setIsLoading(true);

    try {
      const [produtosResponse, categoriasResponse] = await Promise.all([
        api.get<Produto[]>('/produtos'),
        api.get<Categoria[]>('/categorias'),
      ]);

      setProdutos(produtosResponse.data);
      setCategorias(categoriasResponse.data);
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel carregar os produtos.'));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  const filteredProducts = useMemo(() => {
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

  const lowStockCount = produtos.filter((produto) => produto.estoqueBaixo).length;
  const gradesCount = new Set(produtos.map((produto) => produto.gradeGrupo)).size;

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const startEditing = (produto: Produto) => {
    setEditingId(produto.id);
    setForm({
      nome: produto.nome,
      modelo: produto.modelo,
      referencia: produto.referencia,
      cor: produto.cor ?? '',
      tamanho: produto.tamanho ?? '',
      codigoBarras: produto.codigoBarras ?? '',
      preco: produto.preco.toFixed(2),
      estoque: String(produto.estoque),
      estoqueMin: String(produto.estoqueMin),
      categoriaId: produto.categoria?.id ? String(produto.categoria.id) : '',
      ativo: produto.ativo,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsSaving(true);

    const payload: ProdutoPayload = {
      nome: form.nome.trim(),
      modelo: form.modelo.trim(),
      referencia: form.referencia.trim(),
      cor: form.cor.trim() || null,
      tamanho: form.tamanho.trim() || null,
      codigoBarras: form.codigoBarras.trim() || null,
      preco: Number(form.preco),
      estoque: Number(form.estoque),
      estoqueMin: Number(form.estoqueMin),
      categoriaId: form.categoriaId ? Number(form.categoriaId) : null,
      ativo: form.ativo,
    };

    try {
      if (editingId) {
        await api.put(`/produtos/${editingId}`, payload);
        toast.success('Produto atualizado com sucesso.');
      } else {
        await api.post('/produtos', payload);
        toast.success('Produto cadastrado com sucesso.');
      }

      resetForm();
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel salvar o produto.'));
    } finally {
      setIsSaving(false);
    }
  };

  const handleCreateGrade = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsSavingGrade(true);

    const payload: ProdutoGradePayload = {
      nomeBase: gradeForm.nomeBase.trim(),
      modelo: gradeForm.modelo.trim(),
      referencia: gradeForm.referencia.trim(),
      precoBase: Number(gradeForm.precoBase),
      estoqueMin: Number(gradeForm.estoqueMin),
      categoriaId: gradeForm.categoriaId ? Number(gradeForm.categoriaId) : null,
      cores: gradeForm.cores.split(',').map((item) => item.trim()).filter(Boolean),
      tamanhos: gradeForm.tamanhos.split(',').map((item) => item.trim()).filter(Boolean),
      estoqueInicialPadrao: Number(gradeForm.estoqueInicialPadrao),
    };

    try {
      const response = await api.post('/produtos/grade', payload);
      toast.success(`Grade criada com ${response.data.variantes.length} variantes.`);
      setGradeForm(emptyGradeForm);
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel criar a grade do produto.'));
    } finally {
      setIsSavingGrade(false);
    }
  };

  const handleDeactivate = async (produtoId: number) => {
    try {
      await api.delete(`/produtos/${produtoId}`);
      toast.success('Produto desativado com sucesso.');
      await loadData();
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel desativar o produto.'));
    }
  };

  const handleEtiqueta = async (produtoId: number) => {
    try {
      const response = await api.get<EtiquetaResponse>(`/produtos/${produtoId}/etiqueta`);
      await navigator.clipboard.writeText(response.data.conteudo);
      toast.success('Etiqueta copiada para a area de transferencia.');
    } catch (error) {
      toast.error(getErrorMessage(error, 'Nao foi possivel gerar a etiqueta.'));
    }
  };

  return (
    <DashboardLayout
      currentPage="Produtos"
      title="Produtos e estoque"
      subtitle="Cadastre grades, controle variantes por cor e tamanho e deixe o caixa pronto para bipar rapido."
    >
      <div className="space-y-6">
        <div className="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex items-center gap-3">
              <div className="rounded-2xl bg-[#17313e] p-3 text-[#f4b63d]">
                <Shirt className="h-5 w-5" />
              </div>
              <div>
                <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Cadastro em grade</p>
                <h2 className="text-2xl font-black text-slate-900">Criar matriz de produto</h2>
              </div>
            </div>

            <form onSubmit={handleCreateGrade} className="mt-6 grid gap-4 md:grid-cols-2">
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Nome base</span>
                <Input value={gradeForm.nomeBase} onChange={(event) => setGradeForm((state) => ({ ...state, nomeBase: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Modelo</span>
                <Input value={gradeForm.modelo} onChange={(event) => setGradeForm((state) => ({ ...state, modelo: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Referencia</span>
                <Input value={gradeForm.referencia} onChange={(event) => setGradeForm((state) => ({ ...state, referencia: event.target.value.toUpperCase() }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Categoria</span>
                <select
                  value={gradeForm.categoriaId}
                  onChange={(event) => setGradeForm((state) => ({ ...state, categoriaId: event.target.value }))}
                  className="h-12 w-full rounded-2xl border border-slate-300 bg-white px-4 text-sm text-slate-900"
                >
                  <option value="">Sem categoria</option>
                  {categorias.map((categoria) => (
                    <option key={categoria.id} value={categoria.id}>
                      {categoria.nome}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Preco base</span>
                <Input type="number" min="0.01" step="0.01" value={gradeForm.precoBase} onChange={(event) => setGradeForm((state) => ({ ...state, precoBase: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Estoque minimo</span>
                <Input type="number" min="0" step="1" value={gradeForm.estoqueMin} onChange={(event) => setGradeForm((state) => ({ ...state, estoqueMin: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Cores</span>
                <Input value={gradeForm.cores} onChange={(event) => setGradeForm((state) => ({ ...state, cores: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Tamanhos</span>
                <Input value={gradeForm.tamanhos} onChange={(event) => setGradeForm((state) => ({ ...state, tamanhos: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Estoque inicial por variante</span>
                <Input type="number" min="0" step="1" value={gradeForm.estoqueInicialPadrao} onChange={(event) => setGradeForm((state) => ({ ...state, estoqueInicialPadrao: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <div className="md:col-span-2">
                <Button type="submit" disabled={isSavingGrade} className="rounded-2xl bg-[#17313e] text-white hover:bg-[#0f222d]">
                  <PackagePlus className="mr-2 h-4 w-4" />
                  {isSavingGrade ? 'Criando grade...' : 'Gerar variantes automaticamente'}
                </Button>
              </div>
            </form>
          </Card>

          <Card className="rounded-[1.75rem] border-0 bg-[#17313e] p-6 text-white shadow-sm">
            <p className="text-sm uppercase tracking-[0.25em] text-slate-300">Visao rapida</p>
            <div className="mt-6 grid gap-4">
              <div className="rounded-3xl bg-white/10 p-5">
                <p className="text-sm text-slate-300">Variantes ativas</p>
                <p className="mt-2 text-4xl font-black">{produtos.length}</p>
              </div>
              <div className="rounded-3xl bg-[#f4b63d] p-5 text-[#17313e]">
                <p className="text-sm font-semibold uppercase tracking-[0.2em]">Grades cadastradas</p>
                <p className="mt-2 text-4xl font-black">{gradesCount}</p>
                <p className="mt-2 text-sm">Use referencia e grade para evitar cadastro duplicado.</p>
              </div>
              <div className="rounded-3xl bg-white/10 p-5">
                <p className="text-sm text-slate-300">Estoque baixo</p>
                <p className="mt-2 text-4xl font-black">{lowStockCount}</p>
              </div>
            </div>
          </Card>
        </div>

        <div className="grid gap-4 xl:grid-cols-[1.2fr_0.8fr]">
          <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Cadastro unitario</p>
                <h2 className="mt-2 text-2xl font-black text-slate-900">
                  {editingId ? 'Editar variante' : 'Nova variante'}
                </h2>
              </div>
              {editingId && (
                <Button variant="outline" className="rounded-2xl" onClick={resetForm}>
                  <RotateCcw className="mr-2 h-4 w-4" />
                  Cancelar edicao
                </Button>
              )}
            </div>

            <form onSubmit={handleSubmit} className="mt-6 grid gap-4 md:grid-cols-2">
              <label className="md:col-span-2">
                <span className="mb-2 block text-sm font-medium text-slate-700">Nome exibido</span>
                <Input value={form.nome} onChange={(event) => setForm((state) => ({ ...state, nome: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Modelo</span>
                <Input value={form.modelo} onChange={(event) => setForm((state) => ({ ...state, modelo: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Referencia</span>
                <Input value={form.referencia} onChange={(event) => setForm((state) => ({ ...state, referencia: event.target.value.toUpperCase() }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Cor</span>
                <Input value={form.cor} onChange={(event) => setForm((state) => ({ ...state, cor: event.target.value }))} className="h-12 rounded-2xl" />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Tamanho</span>
                <Input value={form.tamanho} onChange={(event) => setForm((state) => ({ ...state, tamanho: event.target.value }))} className="h-12 rounded-2xl" />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Codigo de barras</span>
                <Input value={form.codigoBarras} onChange={(event) => setForm((state) => ({ ...state, codigoBarras: event.target.value }))} className="h-12 rounded-2xl" />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Categoria</span>
                <select
                  value={form.categoriaId}
                  onChange={(event) => setForm((state) => ({ ...state, categoriaId: event.target.value }))}
                  className="h-12 w-full rounded-2xl border border-slate-300 bg-white px-4 text-sm text-slate-900"
                >
                  <option value="">Sem categoria</option>
                  {categorias.map((categoria) => (
                    <option key={categoria.id} value={categoria.id}>
                      {categoria.nome}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Preco</span>
                <Input type="number" min="0.01" step="0.01" value={form.preco} onChange={(event) => setForm((state) => ({ ...state, preco: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Estoque atual</span>
                <Input type="number" min="0" step="1" value={form.estoque} onChange={(event) => setForm((state) => ({ ...state, estoque: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label>
                <span className="mb-2 block text-sm font-medium text-slate-700">Estoque minimo</span>
                <Input type="number" min="0" step="1" value={form.estoqueMin} onChange={(event) => setForm((state) => ({ ...state, estoqueMin: event.target.value }))} className="h-12 rounded-2xl" required />
              </label>
              <label className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <input type="checkbox" checked={form.ativo} onChange={(event) => setForm((state) => ({ ...state, ativo: event.target.checked }))} className="h-4 w-4" />
                <span className="text-sm font-medium text-slate-700">Variante ativa para venda</span>
              </label>
              <div className="md:col-span-2 flex flex-wrap gap-3">
                <Button type="submit" disabled={isSaving} className="rounded-2xl bg-[#17313e] text-white hover:bg-[#0f222d]">
                  <PackagePlus className="mr-2 h-4 w-4" />
                  {isSaving ? 'Salvando...' : editingId ? 'Salvar alteracoes' : 'Cadastrar variante'}
                </Button>
                <Button type="button" variant="outline" className="rounded-2xl" onClick={resetForm}>
                  Limpar formulario
                </Button>
              </div>
            </form>
          </Card>
        </div>

        <Card className="rounded-[1.75rem] border-0 bg-white p-6 shadow-sm">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Estoque operacional</p>
              <h2 className="mt-2 text-2xl font-black text-slate-900">Lista de variantes</h2>
            </div>
            <div className="relative w-full max-w-md">
              <Search className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
              <Input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar por nome, referencia, grade ou codigo" className="h-12 rounded-2xl pl-11" />
            </div>
          </div>

          <div className="mt-6 overflow-x-auto">
            <table className="w-full min-w-[1080px]">
              <thead>
                <tr className="border-b border-slate-200 text-left text-sm uppercase tracking-[0.2em] text-slate-500">
                  <th className="pb-4">Produto</th>
                  <th className="pb-4">Referencia</th>
                  <th className="pb-4">Variante</th>
                  <th className="pb-4">Codigo de barras</th>
                  <th className="pb-4">Preco</th>
                  <th className="pb-4">Estoque</th>
                  <th className="pb-4 text-right">Acoes</th>
                </tr>
              </thead>
              <tbody>
                {isLoading ? (
                  <tr>
                    <td colSpan={7} className="py-10 text-center text-slate-500">Carregando produtos...</td>
                  </tr>
                ) : filteredProducts.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="py-10 text-center text-slate-500">Nenhum produto encontrado.</td>
                  </tr>
                ) : (
                  filteredProducts.map((produto) => (
                    <tr key={produto.id} className="border-b border-slate-100 text-sm text-slate-700">
                      <td className="py-4">
                        <p className="font-semibold text-slate-900">{produto.modelo}</p>
                        <p className="text-xs text-slate-500">{produto.nome}</p>
                      </td>
                      <td className="py-4">
                        <p className="font-semibold text-slate-900">{produto.referencia}</p>
                        <p className="text-xs text-slate-500">{produto.gradeGrupo}</p>
                      </td>
                      <td className="py-4">{[produto.cor, produto.tamanho].filter(Boolean).join(' / ') || 'Sem variante'}</td>
                      <td className="py-4 font-mono text-xs">{produto.codigoBarras || 'Gerado automaticamente'}</td>
                      <td className="py-4 font-semibold text-slate-900">{formatCurrency(produto.preco)}</td>
                      <td className="py-4">
                        <span className={produto.estoqueBaixo ? 'font-bold text-amber-600' : ''}>{produto.estoque}</span>
                        <span className="ml-2 text-xs text-slate-500">min {produto.estoqueMin}</span>
                      </td>
                      <td className="py-4">
                        <div className="flex justify-end gap-2">
                          <Button variant="outline" className="rounded-2xl" onClick={() => handleEtiqueta(produto.id)}>
                            <Printer className="mr-2 h-4 w-4" />
                            Etiqueta
                          </Button>
                          <Button variant="outline" className="rounded-2xl" onClick={() => startEditing(produto)}>
                            <Edit3 className="mr-2 h-4 w-4" />
                            Editar
                          </Button>
                          <Button variant="outline" className="rounded-2xl border-red-200 text-red-700 hover:bg-red-50" onClick={() => handleDeactivate(produto.id)}>
                            <Trash2 className="mr-2 h-4 w-4" />
                            Desativar
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      </div>
    </DashboardLayout>
  );
}
