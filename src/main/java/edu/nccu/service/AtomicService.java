package edu.nccu.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AtomicService {

    private final AtomicBoolean inUse = new AtomicBoolean(false);

    public void use() {
        if (!inUse.compareAndSet(false, true)) {
            throw new IllegalStateException("Needs to be used by one client at a time");
        }

        try {
            TimeUnit.MILLISECONDS.sleep((long) (3 * Math.random()));
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            inUse.set(false);
        }
    }
}
