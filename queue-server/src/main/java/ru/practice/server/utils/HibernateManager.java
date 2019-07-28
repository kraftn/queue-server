package ru.practice.server.utils;

import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Класс для подключения и работы с базой данных
 */
public class HibernateManager {
    /** Ссылка на экземпляр класса */
    private static HibernateManager instance = null;
    /** Поле для взаимодействия с базой данных */
    private EntityManager entityManager;
    /** Поле для генерации новых экземпляров EntityManager */
    private SessionFactory sessionFactory;

    /**
     * Конструктор - создание нового объекта
     */
    private HibernateManager() {
        sessionFactory = buildSessionFactory();
        entityManager = sessionFactory.createEntityManager();
    }

    /**
     * Метод для настройки подключения к базе данных
     * @return ссылка на объект SessionFactory
     * @throws Error если не найден файл с настройками подключения к базе данных
     */
    private SessionFactory buildSessionFactory() throws Error {
        Properties dbConnectionProperties = new Properties();
        try {
            dbConnectionProperties.load(
                    ClassLoader.getSystemClassLoader().getResourceAsStream("hibernate.properties")
            );
        } catch (IOException e) {
            throw new Error("Cannot read hibernate properties");
        }

        Configuration configuration = new Configuration();
        // Добавить классы с аннотациями Hibernate
        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(TaskType.class);

        configuration.addProperties(dbConnectionProperties);

        return configuration.buildSessionFactory();
    }

    /**
     * Получить экземпляр класса
     * @return экземпляр класса
     */
    public static HibernateManager getInstance() {
        // Если экземпляр класса ещё не был создан
        if (instance == null) {
            instance = new HibernateManager();
        }
        return instance;
    }

    /**
     * Получить ссылку на новый объект класса EntityManager
     * @return ссылка на экземпляр класса EntityManager
     */
    public EntityManager createEntityManager(){
        return sessionFactory.createEntityManager();
    }

    /**
     * Метод для выборки из базы данных всех типов задач
     * @return список типов задач
     */
    public List<TaskType> getAllTaskTypes(){
        TypedQuery<TaskType> query = entityManager.createQuery("Select x from TaskType x",
                TaskType.class);
        return query.getResultList();
    }
}
