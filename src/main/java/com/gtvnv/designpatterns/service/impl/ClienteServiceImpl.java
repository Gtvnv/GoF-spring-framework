package com.gtvnv.designpatterns.service.impl;

import com.gtvnv.designpatterns.client.ViaCepClient;
import com.gtvnv.designpatterns.exception.RecursoNaoEncontradoException;
import com.gtvnv.designpatterns.model.Cliente;
import com.gtvnv.designpatterns.model.Endereco;
import com.gtvnv.designpatterns.repository.ClienteRepository;
import com.gtvnv.designpatterns.repository.EnderecoRepository;
import com.gtvnv.designpatterns.service.ClienteService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementacao do padrao <b>Strategy</b> {@link ClienteService}.
 *
 * <p>Tambem exerce o papel de <b>Facade</b>: abstrai e orquestra integracoes
 * diversas (dois repositorios JPA e o cliente HTTP ViaCEP) por tras de uma
 * interface simples, de modo que o controller nao precise conhecer esses
 * detalhes.</p>
 *
 * <p>Como bean do Spring ({@link Service}), e um <b>Singleton</b> gerenciado
 * pelo container.</p>
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepClient viaCepClient;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              EnderecoRepository enderecoRepository,
                              ViaCepClient viaCepClient) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viaCepClient = viaCepClient;
    }

    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente nao encontrado para o id " + id));
    }

    @Override
    public Cliente inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
        return cliente;
    }

    @Override
    public Cliente atualizar(Long id, Cliente cliente) {
        buscarPorId(id); // garante que o recurso existe (lanca 404 caso contrario)
        cliente.setId(id);
        salvarClienteComCep(cliente);
        return cliente;
    }

    @Override
    public void deletar(Long id) {
        buscarPorId(id);
        clienteRepository.deleteById(id);
    }

    /**
     * Facade em acao: se o endereco com o CEP informado nao estiver em cache
     * no banco, consulta a API ViaCEP (Proxy) e persiste antes de salvar o
     * cliente.
     *
     * <p>O CEP e normalizado (apenas digitos) para servir de chave estavel,
     * evitando divergencias entre o formato enviado pelo cliente
     * ({@code 01001000}) e o retornado pela ViaCEP ({@code 01001-000}).</p>
     */
    private void salvarClienteComCep(Cliente cliente) {
        String cep = normalizarCep(cliente.getEndereco().getCep());

        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco consultado = viaCepClient.consultarCep(cep);
            if (consultado == null || consultado.getCep() == null) {
                throw new RecursoNaoEncontradoException("CEP invalido ou nao encontrado: " + cep);
            }
            consultado.setCep(cep); // grava sempre no formato normalizado
            return enderecoRepository.save(consultado);
        });

        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }

    private String normalizarCep(String cep) {
        if (cep == null) {
            throw new RecursoNaoEncontradoException("CEP nao informado");
        }
        return cep.replaceAll("\\D", "");
    }
}
