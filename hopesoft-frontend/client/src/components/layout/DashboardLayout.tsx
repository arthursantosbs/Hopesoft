import { useMemo, useState } from 'react';
import { Link, useLocation } from 'wouter';
import {
  BarChart3,
  BriefcaseBusiness,
  LayoutDashboard,
  LogOut,
  Menu,
  Package,
  ShoppingCart,
  UserCircle2,
  X,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';
import { formatRoleLabel } from '@/lib/format';

interface DashboardLayoutProps {
  children: React.ReactNode;
  currentPage?: string;
  title?: string;
  subtitle?: string;
}

export default function DashboardLayout({
  children,
  currentPage,
  title,
  subtitle,
}: DashboardLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [location, navigate] = useLocation();
  const { user, logout } = useAuth();

  const menuItems = useMemo(
    () => [
      { label: 'Dashboard', href: '/dashboard', icon: LayoutDashboard },
      { label: 'Caixa', href: '/caixa', icon: BriefcaseBusiness },
      { label: 'Vendas', href: '/vendas', icon: ShoppingCart },
      { label: 'Produtos', href: '/produtos', icon: Package },
      { label: 'Relatorios', href: '/relatorios', icon: BarChart3 },
    ],
    []
  );

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="flex min-h-screen bg-[#f5f3ee] text-slate-900">
      <aside
        className={`${
          sidebarOpen ? 'w-72' : 'w-24'
        } hidden min-h-screen flex-col border-r border-slate-200 bg-[#17313e] text-white transition-all duration-300 lg:flex`}
      >
        <div className="flex items-center justify-between border-b border-white/10 px-5 py-5">
          <div className="flex items-center gap-3 overflow-hidden">
            <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#f4b63d] text-lg font-black text-[#17313e]">
              HS
            </div>
            {sidebarOpen && (
              <div className="min-w-0">
                <p className="truncate text-lg font-bold">HopeSoft PDV</p>
                <p className="text-xs uppercase tracking-[0.25em] text-slate-300">Operacao</p>
              </div>
            )}
          </div>
          <button
            onClick={() => setSidebarOpen((value) => !value)}
            className="rounded-xl p-2 text-slate-300 transition hover:bg-white/10 hover:text-white"
            aria-label="Alternar menu"
          >
            {sidebarOpen ? <X size={18} /> : <Menu size={18} />}
          </button>
        </div>

        <nav className="flex-1 px-4 py-6">
          <div className="space-y-2">
            {menuItems.map((item) => {
              const Icon = item.icon;
              const active = location === item.href || currentPage === item.label;

              return (
                <Link key={item.href} href={item.href}>
                  <a
                    className={`flex items-center gap-3 rounded-2xl px-4 py-3 transition ${
                      active
                        ? 'bg-[#f4b63d] text-[#17313e] shadow-sm'
                        : 'text-slate-200 hover:bg-white/10 hover:text-white'
                    }`}
                  >
                    <Icon size={20} />
                    {sidebarOpen && <span className="font-medium">{item.label}</span>}
                  </a>
                </Link>
              );
            })}
          </div>
        </nav>

        <div className="border-t border-white/10 px-4 py-5">
          {sidebarOpen && user && (
            <div className="mb-4 rounded-2xl bg-white/5 p-4">
              <div className="mb-3 flex items-center gap-3">
                <div className="flex h-11 w-11 items-center justify-center rounded-full bg-white/10 text-[#f4b63d]">
                  <UserCircle2 size={24} />
                </div>
                <div className="min-w-0">
                  <p className="truncate font-semibold">{user.nome}</p>
                  <p className="truncate text-sm text-slate-300">{user.email}</p>
                </div>
              </div>
              <p className="text-xs uppercase tracking-[0.2em] text-slate-400">
                {formatRoleLabel(user.perfil)}
              </p>
            </div>
          )}

          <Button
            onClick={handleLogout}
            className="w-full rounded-2xl bg-[#b8402f] text-white hover:bg-[#9d382a]"
          >
            <LogOut className="mr-2 h-4 w-4" />
            {sidebarOpen ? 'Encerrar turno' : 'Sair'}
          </Button>
        </div>
      </aside>

      <div className="flex min-h-screen flex-1 flex-col">
        <header className="border-b border-slate-200 bg-[#fcfbf8] px-5 py-5 sm:px-8">
          <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.3em] text-slate-500">Painel operacional</p>
              <h1 className="mt-2 text-3xl font-black text-slate-900">
                {title || currentPage || 'HopeSoft'}
              </h1>
              {subtitle && <p className="mt-2 max-w-3xl text-sm text-slate-600">{subtitle}</p>}
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-600 shadow-sm">
              <p className="font-semibold text-slate-900">Fluxo simples para o operador</p>
              <p>Buscar produto, finalizar venda e acompanhar estoque sem tela poluida.</p>
            </div>
          </div>
        </header>

        <main className="flex-1 px-5 py-6 sm:px-8">{children}</main>
      </div>
    </div>
  );
}
