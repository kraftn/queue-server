package ru.practice.server.models;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;

import javax.persistence.*;

/**
 * Класс задачи на сервере очередей
 */
@Entity
@Table(name = "main_queue")
public class Task {
    /** Поле идентификатора */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /** Поле типа задачи */
    @ManyToOne
    @JoinColumn(name = "kind", nullable = false)
    private TaskType kind;

    /** Поле статуса */
    @Column(name = "status", nullable = false)
    private String status;

    /** Поле входных данных задачи */
    @Column(name = "input", nullable = false)
    private String input;

    /** Поле выходных данных задачи */
    @Column(name = "output")
    private String output;

    /**
     * Метод для получения типа задачи
     * @return тип задачи
     */
    public TaskType getKind() {
        return kind;
    }

    /**
     * Метод для установки типа задачи
     * @param kind тип задачи
     */
    public void setKind(TaskType kind) {
        this.kind = kind;
    }

    /**
     * Метод для получения статуса задачи
     * @return статус задачи
     */
    public String getStatus() {
        return status;
    }

    /**
     * Метод для установки статуса задачи
     * @param status статус задачи
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Метод для получения входных данных
     * @return входные данные
     */
    public String getInput() {
        return input;
    }

    /**
     * Метод для установки входных данных
     * @param input входные данные
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * Метод для получения выходных данных
     * @return выходные данные
     */
    public String getOutput() {
        return output;
    }

    /**
     * Метод для установки выходных данных
     * @param output выходные данные
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Метод для получения идентификатора
     * @return идентификатор
     */
    public long getId() {
        return id;
    }

    /**
     * Преобразование задачи в строку в формате JSON
     * @return описание задачи в виде строки в формате JSON
     */
    public String toJsonString() {
        JSONWriter writer = new JSONStringer().object()
                .key("id").value(id)
                .key("kind").value(kind.getKind())
                .key("status").value(status)
                .key("input").value(new JSONObject(input));

        // Если выходные данные присутствуют
        if (null != output) {
            writer.key("output").value(new JSONObject(output));
        }
        // в противном случае
        else {
            writer.key("output").value(null);
        }
        return writer.endObject().toString();
    }
}
