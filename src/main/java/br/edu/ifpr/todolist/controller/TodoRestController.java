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
