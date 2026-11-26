package br.edu.ifpr.todolist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.todolist.dto.TodoRequest;
import br.edu.ifpr.todolist.dto.TodoResponse;
import br.edu.ifpr.todolist.exception.ResourceNotFoundException;
import br.edu.ifpr.todolist.model.Todo;
import br.edu.ifpr.todolist.rep.todoRep;

/**
 * Camada de serviço — concentra a lógica de negócio e
 * a conversão entre entidade e DTOs.
 */
@Service
public class TodoService {

    private final todoRep todoRepository;

    public TodoService(todoRep todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoResponse> listarTodos() {
        return todoRepository.findAll()
                .stream()
                .map(TodoResponse::fromEntity)
                .toList();
    }

    public TodoResponse buscarPorId(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id: " + id));
        return TodoResponse.fromEntity(todo);
    }

    public TodoResponse criar(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDeadLine(request.getDeadLine());
        Todo salvo = todoRepository.save(todo);
        return TodoResponse.fromEntity(salvo);
    }

    public TodoResponse atualizar(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id: " + id));
        todo.setTitle(request.getTitle());
        todo.setDeadLine(request.getDeadLine());
        Todo atualizado = todoRepository.save(todo);
        return TodoResponse.fromEntity(atualizado);
    }

    public void deletar(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada com id: " + id);
        }
        todoRepository.deleteById(id);
    }

    public TodoResponse marcarComoConcluida(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id: " + id));
        todo.markAsFinished();
        Todo atualizado = todoRepository.save(todo);
        return TodoResponse.fromEntity(atualizado);
    }
}
