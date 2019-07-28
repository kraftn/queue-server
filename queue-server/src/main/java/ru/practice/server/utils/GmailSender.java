package ru.practice.server.utils;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Класс, реализующий поддержку отправку электронной почты
 */
public class GmailSender {
    /**
     *логин электоронной почты отправителя
     */
    private String username;
    /**
     * пароль электоронной почты отправителя
     */
    private String password;
    /**
     *свойства подключения
     */
    private Properties props;

    /**
     *Конструктор класса GmailSender
     * @param username логин электоронной почты отправителя
     * @param password пароль электоронной почты
     */
    public GmailSender(String username, String password) {
        this.username = username;
        this.password = password;
        /**
         * параметры передачи через почтовый сервер Gmail
         * требуется разрешить на аккаунте передачу сообщений по протоколу IMAP
         */
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); //передача по tls протоколу
        props.put("mail.smtp.host", "smtp.gmail.com");
        /**
         * Убедиться, что на сервере совпадает порт STMP
         * https://support.google.com/mail/answer/7104828 - узнать порт
         */
        props.put("mail.smtp.port", "587");
    }

    /**
     * Метод для отправки писем
     * @param subject тема письма
     * @param text текст письма
     * @param toEmail получатель письма
     */
    public void send(String subject, String text, String toEmail){
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            //от кого
            message.setFrom(new InternetAddress(username));
            //кому
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            //Заголовок письма
            message.setSubject(subject);
            //Содержимое
            message.setText(text);

            //Отправляем сообщение
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
