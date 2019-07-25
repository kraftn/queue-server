package ru.practice.server.utils;

import ru.practice.server.models.Task;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class Queue {
    public static final String DONE = "Successfully done";
    public static final String RUNNING = "Running";
    public static final String WAITING = "In the queue";
    public static final String NO_INTERNET = "No Internet connection";

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

    /*public void rollBack() {
        entityManager.getTransaction().rollback();
    }*/

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
