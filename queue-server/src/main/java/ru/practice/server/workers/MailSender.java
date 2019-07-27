package ru.practice.server.workers;

import com.sun.mail.smtp.SMTPAddressFailedException;
import org.json.JSONObject;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.GmailSender;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.WebSocketSender;

import java.net.UnknownHostException;

public class MailSender implements Runnable {
    private Queue queue;
    private GmailSender gmailSender;

    public MailSender(Queue queue){
        this.queue = queue;
        this.gmailSender = new GmailSender("sendermail23333@gmail.com", "qwertyuiop1!");
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
                gmailSender.send(subject, text, "sendermail23333@gmail.com", toEmail);
            } catch (RuntimeException e){
                isErrorOccurred = true;

                queue.beginTransaction();
                Throwable cause = e.getCause().getCause();
                String status = "";


                e.printStackTrace();

                if (cause.getClass().equals(UnknownHostException.class)) {
                    status = Queue.NO_INTERNET;
                } else if(cause.getClass().equals(SMTPAddressFailedException.class)){
                    status = Queue.WRONG_EMAIL_ADDRESS;
                }
                currentTask.setStatus(status);
                System.out.println(String.format(Queue.TEMPLATE_TO_PRINT,
                        currentTask.getId(), status));
                queue.endTransaction();
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
