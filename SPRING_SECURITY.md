# Proteção do endpoint `GET /api/todos` com Spring Security

## 1. Onde a implementação fica no projeto?

Em uma aplicação Spring Boot organizada em camadas, a configuração de segurança fica em uma pasta própria, dentro do pacote principal:

```text
src/main/java/br/edu/ifpr/todolist/
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── TodoController.java
│   └── TodoRestController.java
├── model/
│   └── Todo.java
└── rep/
    └── todoRep.java
```

- `config/SecurityConfig.java`: define as regras de acesso, autenticação HTTP Basic e usuário em memória.
- `controller/TodoRestController.java`: implementa o endpoint REST `GET /api/todos`.
- `rep/todoRep.java`: consulta as tarefas no banco por meio do Spring Data JPA.
- `pom.xml`: declara a dependência do Spring Security.

O `TodoController` original foi mantido, pois atende à interface web com Thymeleaf. O novo controlador REST foi separado para não misturar as responsabilidades MVC e API.

## 2. Dependência necessária

No `pom.xml`, foi adicionada a dependência:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Depois de salvar o arquivo, atualize/recarregue as dependências Maven na IDE. O Spring Boot configura os componentes do Spring Security a partir dessa dependência.

## 3. Configuração de segurança

Arquivo: `src/main/java/br/edu/ifpr/todolist/config/SecurityConfig.java`

```java
package br.edu.ifpr.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/todos").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails usuario = User.withUsername("professor")
            .password("{noop}123456")
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(usuario);
    }
}
```

### Entendendo a configuração

1. `@Configuration`: indica que a classe contém configurações da aplicação.
2. `SecurityFilterChain`: define quais requisições precisam de autenticação.
3. `requestMatchers(HttpMethod.GET, "/api/todos").authenticated()`: exige que o usuário esteja autenticado para consultar a lista de tarefas.
4. `anyRequest().permitAll()`: mantém as demais rotas liberadas por esta configuração.
5. `httpBasic()`: habilita a autenticação HTTP Basic.
6. `InMemoryUserDetailsManager`: cria um usuário armazenado em memória, sem precisar cadastrá-lo no MySQL.

**Usuário de teste:**

| Campo | Valor |
|---|---|
| Usuário | `professor` |
| Senha | `123456` |
| Perfil | `ROLE_USER` |

O prefixo `{noop}` indica que a senha está sem codificação. Isso é aceitável somente para este exercício didático. Em um sistema real, utilize um codificador como `BCryptPasswordEncoder`.

O CSRF foi desabilitado para simplificar o teste da API REST com autenticação Basic. Em aplicações reais, a decisão depende de como a autenticação e os clientes são implementados.

## 4. Criação do endpoint REST

Arquivo: `src/main/java/br/edu/ifpr/todolist/controller/TodoRestController.java`

```java
package br.edu.ifpr.todolist.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.todolist.model.Todo;
import br.edu.ifpr.todolist.rep.todoRep;

@RestController
@RequestMapping("/api/todos")
public class TodoRestController {

    private final todoRep todoRepository;

    public TodoRestController(todoRep todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<Todo> listarTodos() {
        return todoRepository.findAll();
    }
}
```

O método `findAll()` consulta todas as tarefas cadastradas. Como a classe utiliza `@RestController`, o Spring transforma a lista retornada em uma resposta JSON.

A rota criada é:

```text
GET http://localhost:8080/api/todos
```

## 5. Testando a autenticação

Inicie a aplicação pelo Maven ou pela IDE. A aplicação está configurada para utilizar a porta `8080`.

### Teste 1 — Sem credenciais

No terminal, execute:

```bash
curl -i http://localhost:8080/api/todos
```

**Resultado esperado:**

```http
HTTP/1.1 401
WWW-Authenticate: Basic
```

O status `401 Unauthorized` significa que a requisição não apresentou credenciais válidas.

### Teste 2 — Com credenciais válidas

Execute:

```bash
curl -i -u professor:123456 http://localhost:8080/api/todos
```

**Resultado esperado:**

```http
HTTP/1.1 200
Content-Type: application/json
```

O corpo da resposta será uma lista JSON com as tarefas existentes no banco. Se não houver tarefas cadastradas, será retornada uma lista vazia:

```json
[]
```

O status `200 OK` confirma que a autenticação foi aceita e que o endpoint foi acessado.

### Comparação

| Requisição | Credenciais | Resultado |
|---|---|---|
| `GET /api/todos` | Nenhuma | `401 Unauthorized` |
| `GET /api/todos` | `professor:123456` | `200 OK` |

## 6. Resumo do funcionamento

```text
Cliente HTTP
     |
     | GET /api/todos
     v
Spring Security
     |
     +---- Sem credenciais ------> 401 Unauthorized
     |
     +---- Credenciais inválidas -> 401 Unauthorized
     |
     +---- Credenciais válidas
                    |
                    v
             TodoRestController
                    |
                    v
                todoRep
                    |
                    v
                Banco MySQL
                    |
                    v
             Resposta JSON (200)
```

## 7. Observações sobre o projeto

- O controlador MVC original foi preservado.
- A API REST foi implementada em um controlador separado.
- O usuário é armazenado em memória; ele não é inserido na tabela de usuários do MySQL.
- A proteção solicitada foi aplicada especificamente ao método `GET /api/todos`. As demais rotas continuam permitidas por esta configuração.
- A porta do servidor foi alterada de `3306` para `8080` em `application.properties`, pois `3306` é normalmente utilizada pelo MySQL e não deve ser compartilhada com o servidor HTTP.
- A URL e as credenciais do MySQL existentes no projeto não foram alteradas. Verifique se o banco de dados está criado e se a conexão está configurada corretamente antes de iniciar a aplicação.

## 8. Conclusão

O Spring Security intercepta as requisições antes que elas cheguem ao controlador. Quando uma requisição sem autenticação tenta acessar `GET /api/todos`, o framework retorna `401`. Quando o usuário e a senha são válidos, a requisição é autorizada, o controlador consulta o repositório e a aplicação retorna `200` com os dados em JSON.
