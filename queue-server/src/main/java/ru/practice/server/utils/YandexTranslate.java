package ru.practice.server.utils;
import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Класс, позволяющий переводить текст с помощью Yandex Translate API
 */
public class YandexTranslate {
    /**
     * счетчик, для хождения по строкам json-ответа
     */
    private static int i = 0;
    /**
     * ключ от API YT. Будет заблокирован в Августе
     */
    private static String APIkey = "trnsl.1.1.20190724T111021Z.022523749cea0e17.47536e5b755e722e5a41aa56a99671639df83f6d";

    /**
     *
     * @param lang язык, на который нужно перевести
     * @param input переводимый текст
     * @return возвращает переведенный текст
     * @throws IOException проблемы с чтением битов, а также выбора кодировки
     */
    public static String translate(String lang, String input) throws IOException {
        String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + APIkey;
        URL urlObj = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection)urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes("text=" + URLEncoder.encode(input, "UTF-8") + "&lang=" + lang);

        InputStream response = connection.getInputStream();
        String json = new java.util.Scanner(response).nextLine();
        int start = json.indexOf("[");
        int end = json.indexOf("]");
        String translated = json.substring(start + 2, end - 1);
        i++;
        if (translated.equals(input) && i < 2) {
            // если вернулся тот же текст - меняем направления перевода
            return translate("en", input);
        } else return translated;
    }
}
