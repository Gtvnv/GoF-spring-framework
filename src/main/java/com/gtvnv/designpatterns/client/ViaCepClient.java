package com.gtvnv.designpatterns.client;

import com.gtvnv.designpatterns.model.Endereco;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Padrao <b>Proxy</b>: cliente HTTP declarativo para a API publica ViaCEP.
 *
 * <p>Nao escrevemos a implementacao; o Spring Cloud OpenFeign gera um proxy
 * dinamico em tempo de execucao que traduz a chamada do metodo em uma
 * requisicao HTTP e converte a resposta JSON em um objeto {@link Endereco}.</p>
 *
 * @see <a href="https://viacep.com.br/">ViaCEP</a>
 */
@FeignClient(name = "viaCep", url = "https://viacep.com.br/ws")
public interface ViaCepClient {

    @GetMapping("/{cep}/json/")
    Endereco consultarCep(@PathVariable("cep") String cep);
}
