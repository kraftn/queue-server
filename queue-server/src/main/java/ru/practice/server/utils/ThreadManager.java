package ru.practice.server.utils;

import ru.practice.server.workers.MailSender;
import ru.practice.server.workers.QuadraticEquationSolver;
import ru.practice.server.workers.Translator;

import java.util.ArrayList;

public class ThreadManager {
    private static ThreadManager instance = null;
    private ArrayList<Thread> threads = new ArrayList<>();
    private ArrayList<Runnable> workers = new ArrayList<>();

    private ThreadManager(){
        HibernateManager hibernateManager = HibernateManager.getInstance();

        Queue queue = new Queue(hibernateManager.createEntityManager());
        QuadraticEquationSolver solver = new QuadraticEquationSolver(queue);
        workers.add(solver);
        threads.add(null);

        queue = new Queue(hibernateManager.createEntityManager());
        Translator translator = new Translator(queue);
        workers.add(translator);
        threads.add(null);

        queue = new Queue(hibernateManager.createEntityManager());
        MailSender sender = new MailSender(queue);
        workers.add(sender);
        threads.add(null);
    }

    public static ThreadManager getInstance(){
        if (instance == null){
            instance = new ThreadManager();
        }

        return instance;
    }

    public void start() {
        for (int i = 0; i < threads.size(); i++){
            Thread thread = threads.get(i);
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(workers.get(i));
                threads.set(i, thread);
                thread.start();
            }
        }
    }
}
