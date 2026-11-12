package br.edu.ifpr.todolist.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk do banco

    @Column(nullable = false)
    private String title; // tarefa

    @Column(nullable = false)
    private LocalDate createdAt;// data de criação
@DateTimeFormat(pattern = "dd/MM/yyyy")    @Column(nullable = false)
    private LocalDate deadLine;// prazo para concluir a tarefa

    private LocalDate finishedAt;// data de conclusão da tarefa

    public Todo() {
        this.createdAt = LocalDate.now();
    }

    public void markAsFinished() {
        this.finishedAt = LocalDate.now();
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

}