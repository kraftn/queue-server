package ru.practice.server.models;

import javax.persistence.*;

@Entity
@Table(name = "task_types")
public class TaskType {
    public static final String QUADRATIC_EQUATION = "Квадратное уравнение";
    public static final String TRANSLATION = "Перевод";
    public static final String EMAIL = "E-mail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "kind", nullable = false)
    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
