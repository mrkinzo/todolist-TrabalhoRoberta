# API REST — Todo List com Spring Security

## 1. Estrutura do projeto

```text
src/main/java/br/edu/ifpr/todolist/
├── config/
│   └── SecurityConfig.java          # Regras de segurança e autenticação
├── controller/
│   ├── TodoController.java          # Controlador MVC (Thymeleaf)
│   └── TodoRestController.java      # Controlador REST (API JSON)
├── dto/
│   ├── TodoRequest.java             # DTO de entrada (corpo das requisições)
│   └── TodoResponse.java            # DTO de saída (respostas JSON)
├── exception/
│   ├── GlobalExceptionHandler.java  # Tratamento global de erros da API
│   └── ResourceNotFoundException.java  # Exceção para recurso não encontrado
├── model/
│   └── Todo.java                    # Entidade JPA (tabela no banco)
├── rep/
│   └── todoRep.java                 # Repositório (Spring Data JPA)
├── service/
│   └── TodoService.java             # Camada de serviço (lógica de negócio)
└── TodolistifApplication.java       # Classe principal
```

### Arquitetura em camadas

```text
  Requisição HTTP
       │
       ▼
  Controller  ←  recebe a requisição, valida parâmetros
       │
       ▼
   Service    ←  lógica de negócio, conversão DTO ↔ Entity
       │
       ▼
  Repository  ←  acesso ao banco de dados (Spring Data JPA)
       │
       ▼
  Banco MySQL
```

A separação em camadas (Controller → Service → Repository) é o padrão de uma API REST bem estruturada. O controller não acessa o banco diretamente — ele delega ao service, que é quem executa a lógica e converte entre entidade e DTO.

## 2. Endpoints da API

Base URL: `http://localhost:8080/api/todos`

| Método   | Rota                       | Descrição                     | Status de sucesso |
|----------|----------------------------|-------------------------------|-------------------|
| `GET`    | `/api/todos`               | Listar todas as tarefas       | `200 OK`          |
| `GET`    | `/api/todos/{id}`          | Buscar tarefa por ID          | `200 OK`          |
| `POST`   | `/api/todos`               | Criar nova tarefa             | `201 Created`     |
| `PUT`    | `/api/todos/{id}`          | Atualizar tarefa existente    | `200 OK`          |
| `DELETE` | `/api/todos/{id}`          | Remover tarefa                | `204 No Content`  |
| `PATCH`  | `/api/todos/{id}/finish`   | Marcar tarefa como concluída  | `200 OK`          |

Todos os endpoints exigem autenticação HTTP Basic.

## 3. DTOs (Data Transfer Objects)

### TodoRequest — corpo enviado pelo cliente

```json
{
  "title": "Estudar Spring Boot",
  "deadLine": "2026-12-15"
}
```

Usado em `POST` (criação) e `PUT` (atualização). O formato da data é `yyyy-MM-dd`.

### TodoResponse — corpo retornado pela API

```json
{
  "id": 1,
  "title": "Estudar Spring Boot",
  "createdAt": "2026-11-26",
  "deadLine": "2026-12-15",
  "finishedAt": null,
  "finished": false
}
```

O campo `finished` é calculado automaticamente: será `true` quando `finishedAt` não for nulo.

## 4. Camada de serviço (Service)

O `TodoService` concentra toda a lógica de negócio:

- **Conversão**: transforma `TodoRequest` em entidade `Todo` e entidade em `TodoResponse`.
- **Validação de existência**: lança `ResourceNotFoundException` quando o ID não é encontrado.
- **Marcar como concluída**: utiliza o método `markAsFinished()` da entidade, que atribui a data atual.

O controller não contém lógica de negócio — apenas recebe a requisição, chama o service e retorna a resposta.

## 5. Tratamento de erros

O `GlobalExceptionHandler` intercepta exceções e retorna respostas JSON padronizadas:

| Exceção                      | Status HTTP            | Exemplo                                      |
|------------------------------|------------------------|-----------------------------------------------|
| `ResourceNotFoundException`  | `404 Not Found`        | Tarefa com ID inexistente                     |
| `IllegalArgumentException`   | `400 Bad Request`      | Dados inválidos na requisição                 |
| Qualquer outra exceção       | `500 Internal Server Error` | Erro interno não tratado                 |

Exemplo de resposta de erro:

```json
{
  "timestamp": "2026-11-26T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Tarefa não encontrada com id: 99"
}
```

## 6. Spring Security

### Configuração

Arquivo: `config/SecurityConfig.java`

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/**").authenticated()
    .anyRequest().permitAll()
)
.httpBasic(Customizer.withDefaults());
```

- **`/api/**`**: todos os endpoints da API exigem autenticação.
- **Demais rotas**: liberadas (interface web Thymeleaf).
- **Autenticação**: HTTP Basic.
- **CSRF**: desabilitado para facilitar testes com `curl`/Postman.

### Usuário de teste

| Campo   | Valor       |
|---------|-------------|
| Usuário | `professor` |
| Senha   | `123456`    |
| Perfil  | `ROLE_USER` |

O prefixo `{noop}` indica que a senha está sem codificação. Aceitável apenas para fins didáticos.

## 7. Dependência necessária

No `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## 8. Testando com `curl`

### Listar todas as tarefas

```bash
curl -u professor:123456 http://localhost:8080/api/todos
```

### Buscar tarefa por ID

```bash
curl -u professor:123456 http://localhost:8080/api/todos/1
```

### Criar nova tarefa

```bash
curl -u professor:123456 \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudar Spring", "deadLine": "2026-12-15"}' \
  http://localhost:8080/api/todos
```

### Atualizar tarefa

```bash
curl -u professor:123456 \
  -X PUT \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudar Spring Boot 4", "deadLine": "2026-12-20"}' \
  http://localhost:8080/api/todos/1
```

### Remover tarefa

```bash
curl -u professor:123456 \
  -X DELETE \
  http://localhost:8080/api/todos/1
```

### Marcar como concluída

```bash
curl -u professor:123456 \
  -X PATCH \
  http://localhost:8080/api/todos/1/finish
```

### Teste sem credenciais (deve retornar 401)

```bash
curl -i http://localhost:8080/api/todos
```

Resultado esperado:

```http
HTTP/1.1 401
WWW-Authenticate: Basic
```

## 9. Resumo do funcionamento

```text
Cliente HTTP (curl / Postman / frontend)
     │
     │  GET/POST/PUT/DELETE/PATCH  /api/todos
     ▼
Spring Security (filtro)
     │
     ├── Sem credenciais ──────────► 401 Unauthorized
     ├── Credenciais inválidas ────► 401 Unauthorized
     │
     └── Credenciais válidas
              │
              ▼
       TodoRestController
              │
              ▼
         TodoService  ◄── lógica de negócio, conversão DTO ↔ Entity
              │
              ▼
           todoRep  ◄── Spring Data JPA
              │
              ▼
         Banco MySQL
              │
              ▼
        Resposta JSON  (200 / 201 / 204 / 404)
```

## 10. Observações

- O controlador MVC original (`TodoController`) foi preservado para a interface web com Thymeleaf.
- A API REST e a interface web convivem no mesmo projeto, em controllers separados.
- O usuário de teste é armazenado em memória — não é inserido no banco MySQL.
- A proteção foi aplicada a **todos** os endpoints `/api/**` (GET, POST, PUT, DELETE, PATCH).
- A URL do MySQL em `application.properties` aponta para o banco `todolistif` com criação automática.
