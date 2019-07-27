package ru.practice.server.workers;

import org.json.JSONObject;
import org.json.JSONStringer;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.WebSocketSender;
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

            boolean isErrorOccurred = false;
            String translated = null;
            try {
                translated = YandexTranslate.translate(lang, text);
            } catch (IOException e) {
                isErrorOccurred = true;

                queue.beginTransaction();
                currentTask.setStatus(Queue.NO_INTERNET);
                queue.endTransaction();

                System.out.println(String.format(Queue.TEMPLATE_TO_PRINT,
                        currentTask.getId(), Queue.NO_INTERNET));
            }

            queue.beginTransaction();
            if (!isErrorOccurred) {
                String result = new JSONStringer().object()
                        .key("translated")
                        .value(translated)
                        .endObject().toString();

                currentTask.setOutput(result);
                currentTask.setStatus(Queue.DONE);
            }
            WebSocketSender.send(currentTask.toJsonString());
            currentTask = queue.top(TaskType.TRANSLATION);
            queue.endTransaction();
        }
    }
}
