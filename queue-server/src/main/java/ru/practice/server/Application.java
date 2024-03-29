package ru.practice.server;

import org.json.JSONStringer;
import org.json.JSONWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.ThreadManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Класс приложения
 */
@SpringBootApplication
public class Application {
    /**
     * Запустить приложение
     * @param args параметры командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ThreadManager.getInstance().start();
    }

    /**
     * Создать тестовый JSON файл
     * @param numberObjects количество объектов в файле
     * @param toEmail адрес для отправки e-mail
     */
    private static void initFile(int numberObjects, String toEmail) {
        String[] words = {"Привет", "Пока", "Автомобиль", "Апельсин", "Стол"};

        File file = new File(".\\src\\main\\java\\ru\\practice\\server\\input.json");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Random rnd = new Random(System.currentTimeMillis());
        JSONWriter resultWriter = new JSONStringer().array();

        for (int i = 0; i < numberObjects; i++) {
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
                    break;
                case 1:
                    resultWriter.object()
                            .key("kind").value(TaskType.TRANSLATION)
                            .key("input").object()
                            .key("lang").value("en")
                            .key("text").value(words[i % words.length])
                            .endObject()
                            .endObject();
                    break;
                case 2:
                    resultWriter.object()
                            .key("kind").value(TaskType.EMAIL)
                            .key("input").object()
                            .key("to").value(toEmail)
                            .key("subject").value("Новое письмо")
                            .key("text").value(words[i % words.length])
                            .endObject()
                            .endObject();
                    break;
            }
        }
        pw.print(resultWriter.endArray().toString());
        pw.close();
    }
}