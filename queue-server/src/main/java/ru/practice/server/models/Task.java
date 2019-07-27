package ru.practice.server.models;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;

import javax.persistence.*;

@Entity
@Table(name = "main_queue")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "kind", nullable = false)
    private TaskType kind;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "input", nullable = false)
    private String input;

    @Column(name = "output")
    private String output;

    public TaskType getKind() {
        return kind;
    }

    public void setKind(TaskType kind) {
        this.kind = kind;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public long getId() {
        return id;
    }

    public String toJsonString() {
        JSONWriter writer = new JSONStringer().object()
                .key("id").value(id)
                .key("kind").value(kind.getKind())
                .key("status").value(status)
                .key("input").value(new JSONObject(input));
        if (null != output) {
            writer.key("output").value(new JSONObject(output));
        } else {
            writer.key("output").value(null);
        }
        return writer.endObject().toString();
    }
}
