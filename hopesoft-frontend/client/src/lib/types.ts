export type Perfil = 'ADMIN' | 'OPERADOR';

export type FormaPagamento =
  | 'DINHEIRO'
  | 'PIX'
  | 'CARTAO_DEBITO'
  | 'CARTAO_CREDITO'
  | 'FIADO'
  | 'VALE_TROCA'
  | 'MISTO';

export type StatusVenda = 'EM_ESPERA' | 'FINALIZADA' | 'CANCELADA' | 'DEVOLVIDA';

export type StatusCaixaSessao = 'ABERTO' | 'FECHADO';

export type TipoDocumentoFiscal = 'NFCE' | 'SAT' | 'MFE';

export interface UsuarioAutenticado {
  id: number;
  empresaId: number;
  nome: string;
  email: string;
  perfil: Perfil;
}

export interface LoginResponse {
  token: string;
  tipoToken: string;
  expiraEmSegundos: number;
  usuario: UsuarioAutenticado;
}

export interface Categoria {
  id: number;
  nome: string;
}

export interface Produto {
  id: number;
  empresaId: number;
  nome: string;
  modelo: string;
  referencia: string;
  gradeGrupo: string;
  cor: string | null;
  tamanho: string | null;
  codigoBarras: string | null;
  preco: number;
  estoque: number;
  estoqueMin: number;
  estoqueBaixo: boolean;
  ativo: boolean;
  categoria: Categoria | null;
}

export interface ProdutoPayload {
  nome: string;
  modelo: string;
  referencia: string;
  cor?: string | null;
  tamanho?: string | null;
  codigoBarras?: string | null;
  preco: number;
  estoque: number;
  estoqueMin: number;
  categoriaId?: number | null;
  ativo?: boolean;
}

export interface ItemVendaPayload {
  produtoId: number;
  quantidade: number;
  descontoValor?: number;
}

export interface ItemVenda {
  produtoId: number;
  produtoNome: string;
  variante: string | null;
  quantidade: number;
  precoUnit: number;
  subtotalBruto: number;
  descontoValor: number;
  acrescimoValor: number;
  quantidadeDevolvida: number;
  subtotal: number;
}

export interface PagamentoVendaPayload {
  formaPagamento: FormaPagamento;
  valor: number;
  valorRecebido?: number;
  parcelas?: number | null;
  codigoValeTroca?: string | null;
}

export interface PagamentoVenda {
  formaPagamento: FormaPagamento;
  valor: number;
  valorRecebido: number | null;
  parcelas: number | null;
  codigoValeTroca: string | null;
}

export interface Venda {
  id: number;
  empresaId: number;
  usuarioId: number;
  usuarioNome: string;
  subtotalBruto: number;
  descontoTotal: number;
  acrescimoTotal: number;
  total: number;
  formaPagamento: FormaPagamento | null;
  status: StatusVenda;
  troco: number | null;
  observacao: string | null;
  criadoEm: string;
  itens: ItemVenda[];
  pagamentos: PagamentoVenda[];
}

export interface VendaPayload {
  itens: ItemVendaPayload[];
  formaPagamento?: FormaPagamento;
  valorRecebido?: number;
  pagamentos?: PagamentoVendaPayload[];
  descontoValor?: number;
  acrescimoValor?: number;
  vendaEmEspera?: boolean;
  observacao?: string | null;
  senhaGerenteAutorizacao?: string | null;
}

export interface RelatorioFormaPagamento {
  formaPagamento: FormaPagamento;
  total: number;
}

export interface RelatorioDia {
  data: string;
  total: number;
  quantidadeVendas: number;
  totaisPorFormaPagamento: RelatorioFormaPagamento[];
}

export interface ProdutoGradePayload {
  nomeBase: string;
  modelo: string;
  referencia: string;
  precoBase: number;
  estoqueMin: number;
  categoriaId?: number | null;
  cores: string[];
  tamanhos: string[];
  estoqueInicialPadrao: number;
}

export interface ProdutoGradeResponse {
  gradeGrupo: string;
  modelo: string;
  referencia: string;
  variantes: Produto[];
}

export interface MovimentoCaixa {
  id: number;
  tipo: 'ABERTURA' | 'SANGRIA' | 'SUPRIMENTO' | 'FECHAMENTO';
  valor: number;
  observacao: string | null;
  usuarioNome: string;
  criadoEm: string;
}

export interface CaixaSessao {
  id: number;
  status: StatusCaixaSessao;
  fundoTrocoInicial: number;
  valorInformadoFechamento: number | null;
  valorEsperadoFechamento: number;
  totalSangrias: number;
  totalSuprimentos: number;
  totalVendasDinheiro: number;
  totalVendasPix: number;
  totalVendasCartao: number;
  totalVendasFiado: number;
  totalVendasValeTroca: number;
  usuarioAbertura: string;
  usuarioFechamento: string | null;
  observacao: string | null;
  abertoEm: string;
  fechadoEm: string | null;
  movimentos: MovimentoCaixa[];
}

export interface ValeTroca {
  id: number;
  codigo: string;
  saldoOriginal: number;
  saldo: number;
  status: 'ATIVO' | 'CONSUMIDO' | 'EXPIRADO' | 'CANCELADO';
  nomeCliente: string | null;
  documentoCliente: string | null;
  criadoEm: string;
  expiraEm: string | null;
  observacao: string | null;
  vendaOrigemId: number;
}

export interface EtiquetaResponse {
  linguagem: string;
  conteudo: string;
}

export interface ComprovanteResponse {
  formato: string;
  conteudo: string;
}

export interface DocumentoFiscal {
  id: number;
  tipoDocumento: TipoDocumentoFiscal;
  status: 'EMITIDO' | 'PENDENTE_CONFIGURACAO' | 'REJEITADO';
  chaveAcesso: string | null;
  protocolo: string | null;
  mensagemRetorno: string | null;
  payloadGerado: string | null;
  vendaId: number;
  criadoEm: string;
}

export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  code?: string;
  message?: string;
  path?: string;
}
