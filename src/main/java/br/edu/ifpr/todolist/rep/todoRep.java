package br.edu.ifpr.todolist.rep;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.todolist.model.Todo;
public interface todoRep extends JpaRepository<Todo, Long> {

}
