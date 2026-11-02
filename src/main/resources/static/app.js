const API = "/clientes";

// ---- Elementos ----
const form = document.getElementById("cliente-form");
const formTitle = document.getElementById("form-title");
const idInput = document.getElementById("cliente-id");
const nomeInput = document.getElementById("nome");
const cepInput = document.getElementById("cep");
const cepStatus = document.getElementById("cep-status");
const enderecoPreview = document.getElementById("endereco-preview");
const enderecoTexto = document.getElementById("endereco-texto");
const btnSubmit = document.getElementById("btn-submit");
const btnCancel = document.getElementById("btn-cancel");
const tableBody = document.getElementById("clientes-body");
const counter = document.getElementById("counter");
const emptyState = document.getElementById("empty-state");
const toast = document.getElementById("toast");

// ---- Utilitários ----
function soDigitos(valor) {
    return (valor || "").replace(/\D/g, "");
}

function mostrarToast(mensagem, tipo = "success") {
    toast.textContent = mensagem;
    toast.className = `toast show ${tipo}`;
    setTimeout(() => (toast.className = "toast"), 3200);
}

function escaparHtml(texto) {
    const div = document.createElement("div");
    div.textContent = texto ?? "";
    return div.innerHTML;
}

// ---- Preview do CEP (consulta ViaCEP direto, só para feedback visual) ----
let cepTimer;
cepInput.addEventListener("input", () => {
    clearTimeout(cepTimer);
    const cep = soDigitos(cepInput.value);
    enderecoPreview.classList.add("hidden");
    cepStatus.textContent = "";
    cepStatus.className = "cep-status";

    if (cep.length !== 8) return;

    cepStatus.textContent = "buscando...";
    cepStatus.className = "cep-status loading";

    cepTimer = setTimeout(async () => {
        try {
            const resp = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const dados = await resp.json();
            if (dados.erro) {
                cepStatus.textContent = "CEP inválido";
                cepStatus.className = "cep-status error";
                return;
            }
            cepStatus.textContent = "✓ encontrado";
            cepStatus.className = "cep-status ok";
            enderecoTexto.innerHTML = `${escaparHtml(dados.logradouro)}${dados.bairro ? " · " + escaparHtml(dados.bairro) : ""}<br/>${escaparHtml(dados.localidade)} / ${escaparHtml(dados.uf)}`;
            enderecoPreview.classList.remove("hidden");
        } catch {
            cepStatus.textContent = "erro na busca";
            cepStatus.className = "cep-status error";
        }
    }, 450);
});

// ---- Carregar lista ----
async function carregarClientes() {
    try {
        const resp = await fetch(API);
        if (!resp.ok) throw new Error("Falha ao carregar");
        const clientes = await resp.json();
        renderizar(clientes);
    } catch (e) {
        mostrarToast("Não foi possível carregar os clientes.", "error");
    }
}

function renderizar(clientes) {
    tableBody.innerHTML = "";
    counter.textContent = clientes.length;

    if (clientes.length === 0) {
        emptyState.classList.remove("hidden");
        return;
    }
    emptyState.classList.add("hidden");

    for (const c of clientes) {
        const e = c.endereco || {};
        const logradouro = [e.logradouro, e.bairro].filter(Boolean).join(", ") || "—";
        const cidade = [e.localidade, e.uf].filter(Boolean).join(" / ") || "—";
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><span class="id-badge">${c.id}</span></td>
            <td>
                <div class="cliente-nome">${escaparHtml(c.nome)}</div>
                <div class="cliente-cep">CEP ${escaparHtml(e.cep || "—")}</div>
            </td>
            <td>${escaparHtml(logradouro)}</td>
            <td>${escaparHtml(cidade)}</td>
            <td class="actions">
                <button class="icon-btn" title="Editar" data-edit="${c.id}">✏️</button>
                <button class="icon-btn danger" title="Excluir" data-del="${c.id}">🗑️</button>
            </td>`;
        tableBody.appendChild(tr);
    }
}

// ---- Delegação de eventos da tabela ----
tableBody.addEventListener("click", (ev) => {
    const editId = ev.target.getAttribute("data-edit");
    const delId = ev.target.getAttribute("data-del");
    if (editId) iniciarEdicao(editId);
    if (delId) excluir(delId);
});

// ---- Criar / Atualizar ----
form.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const id = idInput.value;
    const payload = {
        nome: nomeInput.value.trim(),
        endereco: { cep: soDigitos(cepInput.value) },
    };

    btnSubmit.disabled = true;
    try {
        const resp = await fetch(id ? `${API}/${id}` : API, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        if (!resp.ok) {
            const erro = await resp.json().catch(() => ({}));
            throw new Error(erro.mensagem || "Erro ao salvar");
        }

        mostrarToast(id ? "Cliente atualizado!" : "Cliente cadastrado!", "success");
        resetarForm();
        carregarClientes();
    } catch (e) {
        mostrarToast(e.message, "error");
    } finally {
        btnSubmit.disabled = false;
    }
});

// ---- Edição ----
async function iniciarEdicao(id) {
    try {
        const resp = await fetch(`${API}/${id}`);
        if (!resp.ok) throw new Error("Cliente não encontrado");
        const c = await resp.json();

        idInput.value = c.id;
        nomeInput.value = c.nome;
        cepInput.value = (c.endereco && c.endereco.cep) || "";
        cepInput.dispatchEvent(new Event("input"));

        formTitle.textContent = `Editar cliente #${c.id}`;
        btnSubmit.textContent = "Salvar alterações";
        btnCancel.classList.remove("hidden");
        window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (e) {
        mostrarToast(e.message, "error");
    }
}

btnCancel.addEventListener("click", resetarForm);

function resetarForm() {
    form.reset();
    idInput.value = "";
    formTitle.textContent = "Novo cliente";
    btnSubmit.textContent = "Cadastrar";
    btnCancel.classList.add("hidden");
    enderecoPreview.classList.add("hidden");
    cepStatus.textContent = "";
    cepStatus.className = "cep-status";
}

// ---- Exclusão ----
async function excluir(id) {
    if (!confirm(`Excluir o cliente #${id}?`)) return;
    try {
        const resp = await fetch(`${API}/${id}`, { method: "DELETE" });
        if (!resp.ok) throw new Error("Erro ao excluir");
        mostrarToast("Cliente excluído.", "success");
        if (idInput.value === String(id)) resetarForm();
        carregarClientes();
    } catch (e) {
        mostrarToast(e.message, "error");
    }
}

// ---- Início ----
carregarClientes();
