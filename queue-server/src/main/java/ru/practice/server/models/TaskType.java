package ru.practice.server.models;

import javax.persistence.*;

/**
 * Класс типа задачи
 */
@Entity
@Table(name = "task_types")
public class TaskType {
    /** Задача решения квадратного уравнения */
    public static final String QUADRATIC_EQUATION = "Квадратное уравнение";
    /** Задача перевода текста с одного языка на другой */
    public static final String TRANSLATION = "Перевод";
    /** Задача отправки электронного письма */
    public static final String EMAIL = "E-mail";

    /** Поле идентификатора */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /** Поле типа задачи */
    @Column(name = "kind", nullable = false)
    private String kind;

    /**
     * Метод для получения типа задачи
     * @return строковое представление типа задачи
     */
    public String getKind() {
        return kind;
    }

    /**
     * Метод для установки типа задачи
     * @param kind строковое представление типа задачи
     */
    public void setKind(String kind) {
        this.kind = kind;
    }
}
