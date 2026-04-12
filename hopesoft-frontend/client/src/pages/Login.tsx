import { useState } from 'react';
import { useLocation } from 'wouter';
import { AlertCircle, Loader2, ShieldCheck } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from 'sonner';

export default function Login() {
  const [email, setEmail] = useState('admin@hopesoft.com');
  const [senha, setSenha] = useState('hopesoft123');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [, navigate] = useLocation();
  const { login } = useAuth();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);

    try {
      await login(email, senha);
      toast.success('Acesso liberado com sucesso.');
      navigate('/dashboard');
    } catch (requestError) {
      const message =
        requestError instanceof Error ? requestError.message : 'Nao foi possivel entrar agora.';
      setError(message);
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-[#17313e] px-4 py-8">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,_rgba(244,182,61,0.24),_transparent_35%),radial-gradient(circle_at_bottom_right,_rgba(54,111,140,0.45),_transparent_30%)]" />
      <div className="absolute inset-0 opacity-20 [background-image:linear-gradient(rgba(255,255,255,0.08)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.08)_1px,transparent_1px)] [background-size:42px_42px]" />

      <div className="relative z-10 grid w-full max-w-6xl gap-8 lg:grid-cols-[1.1fr_0.9fr]">
        <section className="hidden rounded-[2rem] border border-white/10 bg-white/5 p-10 text-white shadow-2xl backdrop-blur lg:block">
          <p className="text-sm uppercase tracking-[0.4em] text-[#f4d9a2]">HopeSoft PDV</p>
          <h1 className="mt-6 max-w-lg text-5xl font-black leading-tight">
            Frente de caixa organizada para vender rapido e com menos erro.
          </h1>
          <p className="mt-6 max-w-xl text-lg text-slate-200">
            O sistema esta sendo alinhado para operacao real: fluxo enxuto, informacao clara e menos
            cliques na rotina do caixa.
          </p>

          <div className="mt-10 grid gap-4 md:grid-cols-2">
            <div className="rounded-3xl bg-white/10 p-5">
              <p className="text-sm text-slate-300">Foco no operador</p>
              <p className="mt-2 text-2xl font-bold">Venda guiada em poucos passos</p>
            </div>
            <div className="rounded-3xl bg-white/10 p-5">
              <p className="text-sm text-slate-300">Foco no gestor</p>
              <p className="mt-2 text-2xl font-bold">Estoque e relatorios no mesmo painel</p>
            </div>
          </div>
        </section>

        <Card className="rounded-[2rem] border-0 bg-[#fcfbf8] shadow-2xl">
          <div className="p-8 sm:p-10">
            <div className="mb-8 flex items-start justify-between gap-4">
              <div>
                <div className="mb-4 inline-flex h-14 w-14 items-center justify-center rounded-2xl bg-[#17313e] text-xl font-black text-[#f4b63d]">
                  HS
                </div>
                <h2 className="text-3xl font-black text-slate-900">Entrar no sistema</h2>
                <p className="mt-2 text-sm text-slate-600">
                  Use suas credenciais para abrir o painel operacional.
                </p>
              </div>
              <div className="hidden rounded-2xl bg-emerald-50 px-4 py-3 text-emerald-700 sm:block">
                <ShieldCheck className="mb-2 h-5 w-5" />
                <p className="text-xs font-semibold uppercase tracking-[0.2em]">JWT ativo</p>
              </div>
            </div>

            {error && (
              <div className="mb-6 flex gap-3 rounded-2xl border border-red-200 bg-red-50 p-4 text-red-700">
                <AlertCircle className="mt-0.5 h-5 w-5 shrink-0" />
                <p className="text-sm">{error}</p>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-5">
              <div>
                <Label htmlFor="email" className="text-slate-700">
                  Email
                </Label>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  disabled={isSubmitting}
                  className="mt-2 h-12 rounded-2xl border-slate-300 bg-white"
                  placeholder="admin@hopesoft.com"
                  required
                />
              </div>

              <div>
                <Label htmlFor="senha" className="text-slate-700">
                  Senha
                </Label>
                <Input
                  id="senha"
                  type="password"
                  value={senha}
                  onChange={(event) => setSenha(event.target.value)}
                  disabled={isSubmitting}
                  className="mt-2 h-12 rounded-2xl border-slate-300 bg-white"
                  placeholder="Digite sua senha"
                  required
                />
              </div>

              <div className="grid gap-3 sm:grid-cols-2">
                <Button
                  type="button"
                  variant="outline"
                  className="h-11 rounded-2xl"
                  onClick={() => {
                    setEmail('admin@hopesoft.com');
                    setSenha('hopesoft123');
                  }}
                >
                  Preencher admin
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  className="h-11 rounded-2xl"
                  onClick={() => {
                    setEmail('operador@hopesoft.com');
                    setSenha('hopesoft123');
                  }}
                >
                  Preencher operador
                </Button>
              </div>

              <Button
                type="submit"
                disabled={isSubmitting}
                className="h-12 w-full rounded-2xl bg-[#17313e] text-base font-semibold text-white hover:bg-[#0f222d]"
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Entrando...
                  </>
                ) : (
                  'Abrir painel'
                )}
              </Button>
            </form>

            <div className="mt-8 rounded-3xl bg-[#f3efe5] p-5">
              <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">
                Credenciais iniciais
              </p>
              <div className="mt-3 space-y-2 text-sm text-slate-700">
                <p>
                  <strong>Admin:</strong> admin@hopesoft.com / hopesoft123
                </p>
                <p>
                  <strong>Operador:</strong> operador@hopesoft.com / hopesoft123
                </p>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
