package edu.nccu.runner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.nccu.component.ZkReentrantLock;
import edu.nccu.service.AtomicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MainRunner implements CommandLineRunner {

    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${zookeeper.maxRetries}")
    private int maxRetries;

    @Value("${zookeeper.baseSleepTimeMs}")
    private int sleepTime;

    private final AtomicService atomicService;

    @Autowired
    public MainRunner(AtomicService atomicService) {
        this.atomicService = atomicService;
    }

    private static final int QTY = 5;
    private static final int REPETITIONS = QTY * 10;
    private static final String PATH = "/zkApp/app1";

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(QTY);
        try {
            for (int i = 0; i < QTY; ++i) {
                final int index = i;
                Callable<Void> task = () -> {
                    ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(sleepTime, maxRetries);
                    CuratorFramework zkClient = CuratorFrameworkFactory.newClient(connectString, retryPolicy);

                    try {
                        zkClient.start();
                        final ZkReentrantLock example = new ZkReentrantLock(zkClient, PATH, atomicService, "Client " + index);
                        for (int j = 0; j < REPETITIONS; ++j) {
                            example.doWork(10, TimeUnit.SECONDS);
                        }
                    } catch (InterruptedException ex) {
                        log.error(ExceptionUtils.getStackTrace(ex));
                        Thread.currentThread().interrupt();
                    } catch (Throwable ex) {
                        log.error(ExceptionUtils.getStackTrace(ex));
                    } finally {
                        CloseableUtils.closeQuietly(zkClient);
                    }

                    return null;
                };

                executorService.submit(task);
            }

            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            Thread.currentThread().interrupt();
        }
    }

}
