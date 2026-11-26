package br.edu.ifpr.todolist.dto;

import java.time.LocalDate;

import br.edu.ifpr.todolist.model.Todo;

/**
 * DTO de resposta — controla quais campos da entidade são expostos na API.
 */
public class TodoResponse {

    private Long id;
    private String title;
    private LocalDate createdAt;
    private LocalDate deadLine;
    private LocalDate finishedAt;
    private boolean finished;

    public TodoResponse() {
    }

    /**
     * Converte uma entidade Todo em um DTO de resposta.
     */
    public static TodoResponse fromEntity(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setCreatedAt(todo.getCreatedAt());
        response.setDeadLine(todo.getDeadLine());
        response.setFinishedAt(todo.getFinishedAt());
        response.setFinished(todo.getFinishedAt() != null);
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public LocalDate getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDate finishedAt) {
        this.finishedAt = finishedAt;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
