package br.edu.ifpr.todolist.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO para receber dados de criação/atualização de uma tarefa.
 * Separa a representação da API da entidade JPA.
 */
public class TodoRequest {

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadLine;

    public TodoRequest() {
    }

    public TodoRequest(String title, LocalDate deadLine) {
        this.title = title;
        this.deadLine = deadLine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }
}
