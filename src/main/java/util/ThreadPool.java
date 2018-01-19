package util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author zzy
 * @date 2017/12/12
 */

public class ThreadPool {
    private static final ThreadPool ourInstance = new ThreadPool();

    private ExecutorService pool;
    public static ThreadPool getInstance() {
        return ourInstance;
    }

    private ThreadPool() {
        pool = new ThreadPoolExecutor(3,
                20,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(128),
                new ThreadPoolExecutor.AbortPolicy());
    }
    public ExecutorService getPool(){
        return pool;
    }
    public void shutDown(){
        pool.shutdown();
    }
}
