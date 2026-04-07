/* ================================
   API SERVICE - Comunicação com Backend
   ================================ */

const API_BASE_URL = 'http://localhost:8080';

class ApiService {
    constructor() {
        this.token = localStorage.getItem('token') || null;
    }

    setToken(token) {
        this.token = token;
        localStorage.setItem('token', token);
    }

    removeToken() {
        this.token = null;
        localStorage.removeItem('token');
    }

    getAuthHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    }

    async request(method, endpoint, data = null) {
        try {
            const options = {
                method,
                headers: this.getAuthHeaders()
            };

            if (data) {
                options.body = JSON.stringify(data);
            }

            const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

            // Se 401, token expirou
            if (response.status === 401) {
                this.removeToken();
                window.location.href = '/index.html';
                throw new Error('Sessão expirada. Faça login novamente.');
            }

            const responseData = await response.json();

            if (!response.ok) {
                throw new Error(responseData.message || `Erro ${response.status}`);
            }

            return responseData;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // ===== AUTH =====
    async login(email, senha) {
        return this.request('POST', '/auth/login', { email, senha });
    }

    async getMe() {
        return this.request('GET', '/auth/me');
    }

    // ===== PRODUTOS =====
    async getProdutos() {
        return this.request('GET', '/produtos');
    }

    async getProdutoById(id) {
        return this.request('GET', `/produtos/${id}`);
    }

    async createProduto(data) {
        return this.request('POST', '/produtos', data);
    }

    async updateProduto(id, data) {
        return this.request('PUT', `/produtos/${id}`, data);
    }

    async desativarProduto(id) {
        return this.request('DELETE', `/produtos/${id}`);
    }

    async getProdutosByName(nome) {
        return this.request('GET', `/produtos/buscar?nome=${encodeURIComponent(nome)}`);
    }

    async getProdutosComEstoqueBaixo() {
        return this.request('GET', '/produtos/estoque/baixo');
    }

    // ===== VENDAS =====
    async registrarVenda(data) {
        return this.request('POST', '/vendas', data);
    }

    // ===== RELATÓRIOS =====
    async getRelatorioDia(data) {
        return this.request('GET', `/relatorios/dia?data=${data}`);
    }
}

// Instância global
const api = new ApiService();

