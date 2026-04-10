const API_BASE_URL = window.location.origin;

class ApiService {
    constructor() {
        this.token = localStorage.getItem("token") || null;
    }

    setToken(token) {
        this.token = token;
        localStorage.setItem("token", token);
    }

    removeToken() {
        this.token = null;
        localStorage.removeItem("token");
    }

    getAuthHeaders(includeJson = false) {
        const headers = {};
        if (includeJson) {
            headers["Content-Type"] = "application/json";
        }
        if (this.token) {
            headers.Authorization = `Bearer ${this.token}`;
        }
        return headers;
    }

    async request(method, endpoint, data = null) {
        const options = {
            method,
            headers: this.getAuthHeaders(data !== null)
        };

        if (data !== null) {
            options.body = JSON.stringify(data);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

        if (response.status === 401) {
            this.removeToken();
            if (!window.location.pathname.endsWith("/index.html") && window.location.pathname !== "/") {
                window.location.assign("/index.html");
            }
        }

        let responseData = null;
        if (response.status !== 204) {
            const contentType = response.headers.get("content-type") || "";
            responseData = contentType.includes("application/json")
                ? await response.json()
                : await response.text();
        }

        if (!response.ok) {
            const message = typeof responseData === "object" && responseData !== null
                ? responseData.message
                : `Erro ${response.status}`;
            throw new Error(message || `Erro ${response.status}`);
        }

        return responseData;
    }

    async login(email, senha) {
        return this.request("POST", "/auth/login", { email, senha });
    }

    async getMe() {
        return this.request("GET", "/auth/me");
    }

    async getCategorias() {
        return this.request("GET", "/categorias");
    }

    async getProdutos() {
        return this.request("GET", "/produtos");
    }

    async getProdutoById(id) {
        return this.request("GET", `/produtos/${id}`);
    }

    async createProduto(data) {
        return this.request("POST", "/produtos", data);
    }

    async updateProduto(id, data) {
        return this.request("PUT", `/produtos/${id}`, data);
    }

    async desativarProduto(id) {
        return this.request("DELETE", `/produtos/${id}`);
    }

    async getProdutosByName(nome) {
        return this.request("GET", `/produtos/buscar?nome=${encodeURIComponent(nome)}`);
    }

    async getProdutosComEstoqueBaixo() {
        return this.request("GET", "/produtos/estoque/baixo");
    }

    async registrarVenda(data) {
        return this.request("POST", "/vendas", data);
    }

    async getRelatorioDia(data) {
        return this.request("GET", `/relatorios/dia?data=${data}`);
    }
}

const api = new ApiService();

const brlFormatter = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
});

function formatCurrency(value) {
    return brlFormatter.format(Number(value || 0));
}

function formatFormaPagamento(value) {
    const labels = {
        DINHEIRO: "Dinheiro",
        PIX: "PIX",
        CARTAO_DEBITO: "Cartao de debito",
        CARTAO_CREDITO: "Cartao de credito",
        FIADO: "Fiado"
    };
    return labels[value] || value;
}

function showPageMessage(containerId, message, type = "info") {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }

    container.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
}
