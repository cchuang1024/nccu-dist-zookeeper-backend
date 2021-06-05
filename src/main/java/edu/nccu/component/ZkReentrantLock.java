package edu.nccu.component;

import java.util.concurrent.TimeUnit;

import edu.nccu.service.AtomicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

@Slf4j
public class ZkReentrantLock {
    private final InterProcessMutex lock;
    private final AtomicService atomicService;
    private final String clientName;

    public ZkReentrantLock(CuratorFramework client, String lockPath, AtomicService atomicService, String clientName) {
        this.atomicService = atomicService;
        this.clientName = clientName;
        this.lock = new InterProcessMutex(client, lockPath);
    }

    public void doWork(long time, TimeUnit unit) throws Exception {
        if (!lock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the lock");
        }
        log.info(clientName + " has the lock");
        if (!lock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the lock");
        }
        log.info(clientName + " has the lock again");

        try {
            atomicService.use(); //access resource exclusively
        } finally {
            log.info(clientName + " releasing the lock");
            lock.release(); // always release the lock in a finally block
            lock.release(); // always release the lock in a finally block
        }
    }
}
