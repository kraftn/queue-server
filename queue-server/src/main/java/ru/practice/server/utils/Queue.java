package ru.practice.server.utils;

import ru.practice.server.models.Task;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * Класс, реализующий в базе данных тип данных "Очередь"
 */
public class Queue {
    /** Шаблон для вывода сообщения об ошибке */
    public static final String TEMPLATE_TO_PRINT = "Ошибка при выполении задачи с id = %d: %s";

    /** Статус успешно выполненнной задачи */
    public static final String DONE = "Успешно выполнено";
    /** Статус выполняемой задачи */
    public static final String RUNNING = "Выполняется";
    /** Статус задачи, ожидающей выполнения */
    public static final String WAITING = "В очереди";
    /** Статус задачи при отсутствии подключения к интернету */
    public static final String NO_INTERNET = "Нет подключения к интернету";
    /** Статус задачи при указании неверного адреса электронной почты */
    public static final String WRONG_EMAIL_ADDRESS = "Неверный e-mail адрес";
    /** Статус задачи при указании неверного языка */
    public static final String WRONG_LANGUAGE = "Неверный язык для перевода";

    /** Объект для работы с базой данных */
    private EntityManager entityManager;

    /**
     * Конструктор - создание нового объекта
     * @param entityManager {@link HibernateManager#createEntityManager()}
     */
    public Queue(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Начать транзакцию в базе данных
     */
    public void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    /**
     * Завершить транзакцию в базе данных
     */
    public void endTransaction() {
        entityManager.getTransaction().commit();
    }

    /**
     * Поместить в конец очереди новую задачу
     * @param task задача, подлежащая выполнению
     */
    public void push(Task task) {
        entityManager.persist(task);
    }

    /**
     * Получить из начала очереди задачу с определённым типом
     * @param kind тип задачи
     * @return задача из начала очереди
     */
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
