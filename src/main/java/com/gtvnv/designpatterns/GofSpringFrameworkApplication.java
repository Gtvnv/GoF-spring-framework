package com.gtvnv.designpatterns;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Ponto de entrada da aplicacao.
 *
 * <p>A propria classe anotada com {@link SpringBootApplication} ja demonstra o
 * padrao <b>Singleton</b> em acao: o contexto do Spring instancia cada bean
 * ({@code @Service}, {@code @Repository}, {@code @RestController}) uma unica vez
 * e reutiliza essa instancia em toda a aplicacao.</p>
 *
 * <p>{@link EnableFeignClients} habilita o padrao <b>Proxy</b>: o Spring Cloud
 * OpenFeign gera, em tempo de execucao, um proxy dinamico que implementa a
 * interface do cliente HTTP declarativo (ViaCEP).</p>
 */
@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = @Info(
        title = "GoF com Spring Framework",
        version = "1.0.0",
        description = "API REST que demonstra Padroes de Projeto (GoF) aplicados com Spring: "
                + "Singleton, Strategy, Facade e Proxy."
))
public class GofSpringFrameworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(GofSpringFrameworkApplication.class, args);
    }
}
