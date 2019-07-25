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

public class HibernateManager {
    private static HibernateManager instance = null;
    private EntityManager entityManager;
    private SessionFactory sessionFactory;

    private HibernateManager() {
        sessionFactory = buildSessionFactory();
        entityManager = sessionFactory.createEntityManager();
    }

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
        //Добавить классы с аннотациями hibernate
        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(TaskType.class);

        configuration.addProperties(dbConnectionProperties);

        return configuration.buildSessionFactory();
    }

    public static HibernateManager getInstance() {
        if (instance == null) {
            instance = new HibernateManager();
        }
        return instance;
    }

    public EntityManager createEntityManager(){
        return sessionFactory.createEntityManager();
    }

    public List<TaskType> getAllTaskTypes(){
        TypedQuery<TaskType> query = entityManager.createQuery("Select x from TaskType x",
                TaskType.class);
        return query.getResultList();
    }
}
