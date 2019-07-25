package ru.practice.server.workers;

import org.json.JSONObject;
import org.json.JSONStringer;
import ru.practice.server.models.Task;
import ru.practice.server.models.TaskType;
import ru.practice.server.utils.Queue;

public class QuadraticEquationSolver implements Runnable {
    private Queue queue;

    public QuadraticEquationSolver(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.beginTransaction();
        Task currentTask = queue.top(TaskType.QUADRATIC_EQUATION);
        queue.endTransaction();

        while (currentTask != null) {
            queue.beginTransaction();
            currentTask.setStatus(Queue.RUNNING);
            queue.endTransaction();

            String inputString = currentTask.getInput();
            JSONObject inputJson = new JSONObject(inputString);
            double[] coefficients = new double[]{
                    inputJson.getDouble("a"), inputJson.getDouble("b"),
                    inputJson.getDouble("c")
            };

            Double[] x = solve(coefficients);
            String result;
            result = new JSONStringer().object()
                    .key("x1")
                    .value(x[0])
                    .key("x2")
                    .value(x[1])
                    .endObject().toString();

            queue.beginTransaction();
            currentTask.setOutput(result);
            currentTask.setStatus(Queue.DONE);
            currentTask = queue.top(TaskType.QUADRATIC_EQUATION);
            queue.endTransaction();
        }
    }

    private Double[] solve(double[] coef) {
        Double[] x = new Double[2];
        double discriminant = Math.pow(coef[1], 2) - 4 * coef[0] * coef[2];
        if (discriminant == 0.0) {
            x[0] = -coef[1] / (2 * coef[0]);
            x[1] = null;
        } else if (discriminant < 0) {
            x[0] = null;
            x[1] = null;
        } else {
            x[0] = (-coef[1] + Math.sqrt(discriminant)) / (2 * coef[0]);
            x[1] = (-coef[1] - Math.sqrt(discriminant)) / (2 * coef[0]);
        }
        return x;
    }
}