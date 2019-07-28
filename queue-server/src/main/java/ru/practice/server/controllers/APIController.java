package ru.practice.server.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.HibernateManager;
import ru.practice.server.utils.Queue;
import ru.practice.server.utils.ThreadManager;

import java.util.List;

/**
 * Контроллер для обработки HTTP-запросов
 */
@Controller
public class APIController {
    /** Поле для хранения доступных видов задач */
    private List<TaskType> taskTypes = HibernateManager.getInstance().getAllTaskTypes();

    /**
     * Метод, обрабатывающий POST запрос для /transferData
     * @param a тело запроса
     */
    @RequestMapping(value = "/transferData", headers="Content-Type=application/json", method = RequestMethod.POST)
    public @ResponseBody void transferData(@RequestBody String a){
        Queue queuePush = new Queue(HibernateManager.getInstance().createEntityManager());

        JSONArray jsonArray = new JSONArray(a);
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Task newTask = new Task();
            String kindString = jsonObject.getString("kind");
            newTask.setKind(findKind(kindString));
            newTask.setStatus(Queue.WAITING);
            String inputString = jsonObject.getJSONObject("input").toString();
            newTask.setInput(inputString);

            queuePush.beginTransaction();
            queuePush.push(newTask);
            queuePush.endTransaction();
        }

        ThreadManager.getInstance().start();
    }

    /**
     * Метод для поиска сущности типа задачи по её строковому представлению
     * @param kind строковое представление типа задачи
     * @return сущность типа задачи
     */
    private TaskType findKind(String kind){
        int i = 0;
        while (!taskTypes.get(i).getKind().equals(kind)){
            i++;
        }
        return taskTypes.get(i);
    }
}