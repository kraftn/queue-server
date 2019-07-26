package ru.practice.server.workers;

import org.json.JSONObject;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.GmailSender;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.WebSocketSender;

public class MailSender implements Runnable {
    private Queue queue;

    public MailSender(Queue queue){
        this.queue = queue;
    }

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
                new GmailSender("sendermail23333@gmail.com", "qwertyuiop1!")
                        .send(subject, text, "sendermail23333@gmail.com", toEmail);
            } catch (RuntimeException e){
                queue.beginTransaction();
                currentTask.setStatus(Queue.NO_INTERNET);
                queue.endTransaction();

                isErrorOccurred = true;
                e.printStackTrace();
            }

            queue.beginTransaction();
            if (!isErrorOccurred) {
                currentTask.setStatus(Queue.DONE);
            }
            WebSocketSender.send(currentTask.toJsonString());
            currentTask = queue.top(TaskType.EMAIL);
            queue.endTransaction();
        }
    }
}
