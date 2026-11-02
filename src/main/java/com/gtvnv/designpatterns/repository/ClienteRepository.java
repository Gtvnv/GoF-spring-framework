package com.gtvnv.designpatterns.repository;

import com.gtvnv.designpatterns.model.Cliente;
import org.springframework.data.repository.CrudRepository;

/**
 * Padrao <b>Repository</b>: o Spring Data JPA gera, em tempo de execucao, uma
 * implementacao (proxy) desta interface para abstrair o acesso aos dados.
 */
public interface ClienteRepository extends CrudRepository<Cliente, Long> {
}
