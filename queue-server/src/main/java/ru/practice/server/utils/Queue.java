package ru.practice.server.utils;

import ru.practice.server.models.Task;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class Queue {
    public static final String TEMPLATE_TO_PRINT = "Ошибка при выполении задачи с id = %d: %s";

    public static final String DONE = "Успешно выполнено";
    public static final String RUNNING = "Выполняется";
    public static final String WAITING = "В очереди";
    public static final String NO_INTERNET = "Нет подключения к интернету";
    public static final String WRONG_EMAIL_ADDRESS = "Неверный e-mail адрес";

    private EntityManager entityManager;

    public Queue(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    public void endTransaction() {
        entityManager.getTransaction().commit();
    }

    public void push(Task task) {
        entityManager.persist(task);
    }

    public Task top(String kind) {
        Query nativeQuery = entityManager.createNativeQuery(
                "select * from public.get_top(?1)",
                Task.class);
        nativeQuery.setParameter(1, kind);

        Task task;
        try {
            task = (Task)nativeQuery.getSingleResult();
        } catch (NoResultException e) {
            task = null;
        }

        return task;
    }
}
