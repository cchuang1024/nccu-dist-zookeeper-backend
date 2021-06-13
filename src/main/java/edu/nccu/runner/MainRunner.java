package edu.nccu.runner;

import edu.nccu.component.NodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

// @Component
@Slf4j
public class MainRunner implements CommandLineRunner {

    private ThreadPoolTaskExecutor taskExecutor;
    private ApplicationContext context;

    @Autowired
    public MainRunner(
            @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor,
            ApplicationContext context) {
        this.taskExecutor = taskExecutor;
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        NodeManager nodeManager = context.getBean(NodeManager.class);
        taskExecutor.execute(nodeManager);
    }
}
