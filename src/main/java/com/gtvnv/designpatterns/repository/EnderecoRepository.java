package com.gtvnv.designpatterns.repository;

import com.gtvnv.designpatterns.model.Endereco;
import org.springframework.data.repository.CrudRepository;

/**
 * Padrao <b>Repository</b> para a entidade {@link Endereco}, usando o CEP como id.
 */
public interface EnderecoRepository extends CrudRepository<Endereco, String> {
}
