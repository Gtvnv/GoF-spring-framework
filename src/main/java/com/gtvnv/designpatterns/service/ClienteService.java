package com.gtvnv.designpatterns.service;

import com.gtvnv.designpatterns.model.Cliente;

/**
 * Padrao <b>Strategy</b>: esta interface define o contrato (as "estrategias"
 * possiveis) para a regra de negocio de clientes. O controller depende apenas
 * desta abstracao, permitindo trocar a implementacao sem alterar quem a consome.
 */
public interface ClienteService {

    Iterable<Cliente> buscarTodos();

    Cliente buscarPorId(Long id);

    Cliente inserir(Cliente cliente);

    Cliente atualizar(Long id, Cliente cliente);

    void deletar(Long id);
}
