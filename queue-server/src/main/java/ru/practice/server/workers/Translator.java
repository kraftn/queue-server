package ru.practice.server.workers;

import org.json.JSONObject;
import org.json.JSONStringer;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.YandexTranslate;

import java.io.IOException;

public class Translator implements Runnable {
    private Queue queue;

    public Translator(Queue queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.beginTransaction();
        Task currentTask = queue.top(TaskType.TRANSLATION);
        queue.endTransaction();

        while (currentTask != null) {
            queue.beginTransaction();
            currentTask.setStatus(Queue.RUNNING);
            queue.endTransaction();

            String inputString = currentTask.getInput();
            JSONObject inputJson = new JSONObject(inputString);
            String lang = inputJson.getString("lang");
            String text = inputJson.getString("text");

            String translated = null;
            try {
                translated = YandexTranslate.translate(lang, text);
            } catch (IOException e) {
                queue.beginTransaction();
                currentTask.setStatus(Queue.NO_INTERNET);
                currentTask = queue.top(TaskType.TRANSLATION);
                queue.endTransaction();
                e.printStackTrace();
                continue;
            }

            String result = new JSONStringer().object()
                    .key("translated")
                    .value(translated)
                    .endObject().toString();

            queue.beginTransaction();
            currentTask.setOutput(result);
            currentTask.setStatus(Queue.DONE);
            currentTask = queue.top(TaskType.TRANSLATION);
            queue.endTransaction();
        }
    }
}
