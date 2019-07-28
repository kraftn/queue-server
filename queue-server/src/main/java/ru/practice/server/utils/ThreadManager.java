package ru.practice.server.utils;

import ru.practice.server.workers.MailSender;
import ru.practice.server.workers.QuadraticEquationSolver;
import ru.practice.server.workers.Translator;

import java.util.ArrayList;

/**
 * Класс для управления потоками выполнения различных типов задач
 */
public class ThreadManager {
    /** Ссылка на экземпляр класса */
    private static ThreadManager instance = null;
    /** Список потоков */
    private ArrayList<Thread> threads = new ArrayList<>();
    /** Список обработчиков */
    private ArrayList<Runnable> workers = new ArrayList<>();

    /**
     * Конструктор, инициализирующий обработчики
     */
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

    /**
     * Получить экземпляр класса
     * @return экземпляр класса
     */
    public static ThreadManager getInstance(){
        // Если экземпляр класса ещё не был создан
        if (instance == null){
            instance = new ThreadManager();
        }

        return instance;
    }

    /**
     * Начать выполнение задач в потоках
     */
    public void start() {
        for (int i = 0; i < threads.size(); i++){
            Thread thread = threads.get(i);

            /*
            Если поток не был создан или закончил своё выполнение,
            то необходимо создать новый и запустить его
             */
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(workers.get(i));
                threads.set(i, thread);
                thread.start();
            }
        }
    }
}
