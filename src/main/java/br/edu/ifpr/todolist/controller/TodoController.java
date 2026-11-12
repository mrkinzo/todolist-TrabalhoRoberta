package br.edu.ifpr.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.edu.ifpr.todolist.model.Todo;
import br.edu.ifpr.todolist.rep.todoRep;

@Controller
public class TodoController {
    private final todoRep todoRepository;

    public TodoController(todoRep todoRepository) {
        this.todoRepository = todoRepository;
    }

    // criar uma nova tarefa
    @PostMapping("/create")
    public String create(Todo todo) {
        todoRepository.save(todo);
        return "redirect:/";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

}