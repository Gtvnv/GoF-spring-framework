# GoF com Spring Framework

API REST que demonstra, na prática, **Padrões de Projeto (GoF)** aplicados com o **Spring Framework**. Projeto do desafio final sobre *Design Patterns com Java: Dos Clássicos (GoF) ao Spring Framework*.

A API gerencia **Clientes** e, ao cadastrar um cliente, consulta automaticamente o endereço a partir do **CEP** usando a API pública [ViaCEP](https://viacep.com.br/).

## Padrões aplicados

| Padrão | Onde está no código | O que faz |
|--------|---------------------|-----------|
| **Singleton** | Beans do Spring (`@Service`, `@Repository`, `@RestController`) | O container do Spring cria uma única instância de cada bean e a reutiliza em toda a aplicação. |
| **Strategy** | [`ClienteService`](src/main/java/com/gtvnv/designpatterns/service/ClienteService.java) | Interface que define o contrato da regra de negócio. O controller depende da abstração, não da implementação. |
| **Facade** | [`ClienteServiceImpl`](src/main/java/com/gtvnv/designpatterns/service/impl/ClienteServiceImpl.java) | Esconde e orquestra integrações diversas (dois repositórios JPA + cliente HTTP ViaCEP) atrás de uma interface simples. |
| **Proxy** | [`ViaCepClient`](src/main/java/com/gtvnv/designpatterns/client/ViaCepClient.java) | Cliente HTTP declarativo (OpenFeign). O Spring gera, em runtime, um proxy dinâmico que traduz a chamada do método em uma requisição HTTP. |
| **Repository** | [`ClienteRepository`](src/main/java/com/gtvnv/designpatterns/repository/ClienteRepository.java), [`EnderecoRepository`](src/main/java/com/gtvnv/designpatterns/repository/EnderecoRepository.java) | Spring Data JPA gera a implementação da camada de acesso a dados. |

## Melhorias/toques pessoais

Além de reproduzir o projeto de referência, este repositório inclui:

- **Documentação OpenAPI/Swagger** interativa (springdoc).
- **Validação de entrada** com Bean Validation (`@Valid`, `@NotBlank`).
- **Tratamento global de exceções** ([`GlobalExceptionHandler`](src/main/java/com/gtvnv/designpatterns/exception/GlobalExceptionHandler.java)) com respostas HTTP padronizadas (404, 400, 500).
- **Normalização de CEP**: aceita `01001000` ou `01001-000` e evita cadastros duplicados de endereço.
- **Cache de endereço**: um CEP já consultado não é buscado de novo na ViaCEP.
- **Maven Wrapper** (`mvnw`): roda sem precisar instalar o Maven.

## Stack

- Java 17
- Spring Boot 3.3.5 (Web, Data JPA, Validation)
- Spring Cloud OpenFeign
- Banco H2 em memória
- springdoc-openapi (Swagger UI)

## Como executar

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Console H2:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:gofdb`, usuário `sa`, sem senha)

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/clientes` | Lista todos os clientes |
| `GET` | `/clientes/{id}` | Busca um cliente por id |
| `POST` | `/clientes` | Cria um cliente (endereço preenchido via ViaCEP) |
| `PUT` | `/clientes/{id}` | Atualiza um cliente |
| `DELETE` | `/clientes/{id}` | Remove um cliente |

### Exemplos

Criar um cliente (o endereço é preenchido automaticamente pelo CEP):

```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"Gustavo Vianna","endereco":{"cep":"01001000"}}'
```

Resposta:

```json
{
  "id": 1,
  "nome": "Gustavo Vianna",
  "endereco": {
    "cep": "01001000",
    "logradouro": "Praça da Sé",
    "bairro": "Sé",
    "localidade": "São Paulo",
    "uf": "SP",
    "ddd": "11"
  }
}
```

Listar todos:

```bash
curl http://localhost:8080/clientes
```

## Estrutura do projeto

```
src/main/java/com/gtvnv/designpatterns/
├─ GofSpringFrameworkApplication.java   # @SpringBootApplication + @EnableFeignClients (Singleton, Proxy)
├─ client/
│  └─ ViaCepClient.java                 # Proxy (OpenFeign)
├─ controller/
│  └─ ClienteController.java            # API REST
├─ exception/
│  ├─ GlobalExceptionHandler.java       # Tratamento global de erros
│  └─ RecursoNaoEncontradoException.java
├─ model/
│  ├─ Cliente.java                      # Entidade
│  └─ Endereco.java                     # Entidade
├─ repository/
│  ├─ ClienteRepository.java            # Repository
│  └─ EnderecoRepository.java           # Repository
└─ service/
   ├─ ClienteService.java               # Strategy (interface)
   └─ impl/
      └─ ClienteServiceImpl.java        # Strategy impl + Facade
```

## Referências

- [Design Patterns com Java: Dos Clássicos (GoF) ao Spring Framework](https://refactoring.guru/design-patterns)
- [Padrões de Projeto com Java Puro (referência do laboratório)](https://github.com/digitalinnovationone/lab-padroes-projeto-java)
- [Padrões de Projeto com Spring (referência do laboratório)](https://github.com/digitalinnovationone/lab-padroes-projeto-spring)
- [ViaCEP](https://viacep.com.br/)
