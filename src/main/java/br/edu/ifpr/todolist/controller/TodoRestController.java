package br.edu.ifpr.todolist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.todolist.dto.TodoRequest;
import br.edu.ifpr.todolist.dto.TodoResponse;
import br.edu.ifpr.todolist.service.TodoService;

/**
 * Controlador REST com CRUD completo para o recurso Todo.
 * Todos os endpoints ficam sob /api/todos e são protegidos por Spring Security.
 */
@RestController
@RequestMapping("/api/todos")
public class TodoRestController {

    private final TodoService todoService;

    public TodoRestController(TodoService todoService) {
        this.todoService = todoService;
    }

    // GET /api/todos — listar todas as tarefas
    @GetMapping
    public ResponseEntity<List<TodoResponse>> listarTodos() {
        return ResponseEntity.ok(todoService.listarTodos());
    }

    // GET /api/todos/{id} — buscar tarefa por ID
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.buscarPorId(id));
    }

    // POST /api/todos — criar nova tarefa
    @PostMapping
    public ResponseEntity<TodoResponse> criar(@RequestBody TodoRequest request) {
        TodoResponse criado = todoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // PUT /api/todos/{id} — atualizar tarefa existente
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> atualizar(@PathVariable Long id, @RequestBody TodoRequest request) {
        return ResponseEntity.ok(todoService.atualizar(id, request));
    }

    // DELETE /api/todos/{id} — remover tarefa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        todoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/todos/{id}/finish — marcar tarefa como concluída
    @PatchMapping("/{id}/finish")
    public ResponseEntity<TodoResponse> marcarComoConcluida(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.marcarComoConcluida(id));
    }
}
