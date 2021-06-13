package edu.nccu.runner;

import java.util.UUID;
import javax.annotation.PreDestroy;

import edu.nccu.component.NodeManager;
import edu.nccu.domain.AppState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MainRunner implements CommandLineRunner {

    private ApplicationContext context;
    private NodeManager nodeManager;

    @Autowired
    public MainRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        this.nodeManager = context.getBean(NodeManager.class);
        this.nodeManager.setHostId(UUID.randomUUID().toString());
        this.nodeManager.setAppState(AppState.INIT);

        Thread managerThread = new Thread(this.nodeManager);
        managerThread.start();
        managerThread.join();
    }

    @PreDestroy
    public void destroy() {
        this.nodeManager.setAppState(AppState.DESTROY);
    }
}
