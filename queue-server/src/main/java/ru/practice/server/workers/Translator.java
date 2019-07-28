package ru.practice.server.workers;

import org.json.JSONObject;
import org.json.JSONStringer;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.WebSocketSender;
import ru.practice.server.utils.YandexTranslate;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Обработчик задач перевода текста
 */
public class Translator implements Runnable {
    /**
     * Объект для работы с очередью в базе данных
     */
    private Queue queue;

    /**
     * Конструктор класса
     *
     * @param queue объект для работы с очередью в базе данных
     */
    public Translator(Queue queue) {
        this.queue = queue;
    }

    /**
     * Выполнить задачи в отдельном потоке
     */
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

                String status = "";
                // Если нет подключения к интернету
                if (e.getClass().equals(UnknownHostException.class)) {
                    status = Queue.NO_INTERNET;
                } else
                    // Если указан неверный язык
                    if (e.getClass().equals(IOException.class)) {
                        status = Queue.WRONG_LANGUAGE;
                    }

                queue.beginTransaction();
                currentTask.setStatus(status);
                queue.endTransaction();

                System.out.println(String.format(Queue.TEMPLATE_TO_PRINT,
                        currentTask.getId(), status));
            }

            queue.beginTransaction();
            /*
            При отсутствии ошибок установить статус успешного выполнения
            и зафиксировать результат выполнения
            */
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
