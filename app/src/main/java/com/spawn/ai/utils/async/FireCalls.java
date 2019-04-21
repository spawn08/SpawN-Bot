package com.spawn.ai.utils.async;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FireCalls {

    private static Executor executor = Executors.newFixedThreadPool(5);

    public static void exec(Runnable command) {
        executor.execute(command);
    }
}
