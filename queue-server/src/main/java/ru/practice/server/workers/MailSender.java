package ru.practice.server.workers;

import com.sun.mail.smtp.SMTPAddressFailedException;
import org.json.JSONObject;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.GmailSender;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.WebSocketSender;

import java.net.UnknownHostException;

/**
 * Обработчик задач отправки электронных писем
 */
public class MailSender implements Runnable {
    /** Объект для работы с очередью в базе данных */
    private Queue queue;
    /** Объект для отправки писем */
    private GmailSender gmailSender;

    /**
     * Конструктор класса
     *
     * @param queue объект для работы с очередью в базе данных
     */
    public MailSender(Queue queue) {
        this.queue = queue;
        this.gmailSender = new GmailSender("sendermail23333@gmail.com", "qwertyuiop1!");
    }

    /**
     * Выполнить задачи в отдельном потоке
     */
    @Override
    public void run() {
        queue.beginTransaction();
        Task currentTask = queue.top(TaskType.EMAIL);
        queue.endTransaction();

        while (currentTask != null) {
            queue.beginTransaction();
            currentTask.setStatus(Queue.RUNNING);
            queue.endTransaction();

            String inputString = currentTask.getInput();
            JSONObject inputJson = new JSONObject(inputString);
            String subject = inputJson.getString("subject");
            String toEmail = inputJson.getString("to");
            String text = inputJson.getString("text");

            boolean isErrorOccurred = false;
            try {
                gmailSender.send(subject, text, toEmail);
            } catch (RuntimeException e) {
                isErrorOccurred = true;

                Throwable cause = e.getCause().getCause();
                String status = "";

                // Если нет подключения к интернету
                if (cause.getClass().equals(UnknownHostException.class)) {
                    status = Queue.NO_INTERNET;
                } else
                    // Если указан неверный адрес электронной почты
                    if (cause.getClass().equals(SMTPAddressFailedException.class)) {
                        status = Queue.WRONG_EMAIL_ADDRESS;
                    }

                queue.beginTransaction();
                currentTask.setStatus(status);
                queue.endTransaction();

                System.out.println(String.format(Queue.TEMPLATE_TO_PRINT,
                        currentTask.getId(), status));
            }

            queue.beginTransaction();
            // При отсутствии ошибок установить статус успешного выполнения
            if (!isErrorOccurred) {
                currentTask.setStatus(Queue.DONE);
            }

            WebSocketSender.send(currentTask.toJsonString());
            currentTask = queue.top(TaskType.EMAIL);
            queue.endTransaction();
        }
    }
}
