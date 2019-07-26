package ru.practice.server;

import org.json.JSONStringer;
import org.json.JSONWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.HibernateManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //initFile();
        SpringApplication.run(Application.class, args);
    }

    private static void initFile() {
        String fromEmail = "your email";

        HibernateManager manager = HibernateManager.getInstance();
        List<TaskType> taskTypes = HibernateManager.getInstance().getAllTaskTypes();

        File file = new File(".\\src\\main\\java\\ru\\practice\\server\\input.json");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] words = {"Hello", "Bye", "Cat", "Dog", "Bird", "Fish", "Car"};
        Random rnd = new Random(System.currentTimeMillis());
        JSONWriter resultWriter = new JSONStringer().array();

        for (int i = 0; i < 5; i++) {
            int type = rnd.nextInt(3);
            switch (type){
                case 0:
                    resultWriter.object()
                            .key("kind").value(TaskType.QUADRATIC_EQUATION)
                            .key("input").object()
                            .key("a").value(100 * rnd.nextDouble())
                            .key("b").value(100 * rnd.nextDouble())
                            .key("c").value(100 * rnd.nextDouble())
                            .endObject()
                            .endObject();
                case 1:
                    resultWriter.object()
                            .key("kind").value(TaskType.TRANSLATION)
                            .key("input").object()
                            .key("lang").value("ru")
                            .key("text").value(words[i % words.length])
                            .endObject()
                            .endObject();
                case 2:
                    resultWriter.object()
                            .key("kind").value(TaskType.EMAIL)
                            .key("input").object()
                            .key("to").value(fromEmail)
                            .key("subject").value("New testing email")
                            .key("text").value(words[i % words.length])
                            .endObject()
                            .endObject();
            }
        }
        pw.print(resultWriter.endArray().toString());
        pw.close();
    }

    private static TaskType findKind(String kind, List<TaskType> taskTypes) {
        int i = 0;
        while (!taskTypes.get(i).getKind().equals(kind)) {
            i++;
        }
        return taskTypes.get(i);
    }
}