package br.edu.ifpr.todolist;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.edu.ifpr.todolist.model.Todo;
import br.edu.ifpr.todolist.rep.todoRep;

@SpringBootApplication
public class TodolistifApplication {

	private final todoRep todoRepository;

    TodolistifApplication(todoRep todoRepository) {
        this.todoRepository = todoRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(TodolistifApplication.class, args);
	}

	CommandLineRunner executar(todoRep todoRepository) {
		return args -> {
			Todo todo = new Todo();
			todo.setTitle("tarefa teste");
			todoRepository.save(todo);

			System.out.println("Aplicação iniciada com sucesso!");
		};
	}

}