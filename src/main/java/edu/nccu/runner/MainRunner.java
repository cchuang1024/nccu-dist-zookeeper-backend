package edu.nccu.runner;

import java.util.Map;
import java.util.UUID;
import javax.annotation.PreDestroy;

import edu.nccu.component.NodeManager;
import edu.nccu.component.ServerMonitor;
import edu.nccu.config.Constant;
import edu.nccu.domain.AppState;
import edu.nccu.domain.ModuleState;
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
    private ServerMonitor serverMonitor;

    @Autowired
    public MainRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        runManager();
        runMonitor();
    }

    @SuppressWarnings("unchecked")
    private void runManager() throws InterruptedException {
        Map<String, ModuleState> moduleStates = (Map<String, ModuleState>) context.getBean("moduleStates");
        if(!ModuleState.ENABLED.equals(moduleStates.get(Constant.MODULE_MANAGER))){
            return;
        }

        log.info("run manager mode...");

        this.nodeManager = context.getBean(NodeManager.class);
        this.nodeManager.setHostId(UUID.randomUUID().toString());
        this.nodeManager.setAppState(AppState.INIT);

        Thread managerThread = new Thread(this.nodeManager);
        managerThread.start();
        managerThread.join();
    }

    @SuppressWarnings("unchecked")
    private void runMonitor() throws InterruptedException {
        Map<String, ModuleState> moduleStates = (Map<String, ModuleState>) context.getBean("moduleStates");
        if(!ModuleState.ENABLED.equals(moduleStates.get(Constant.MODULE_MONITOR))){
            return;
        }

        log.info("run monitor mode...");

        this.serverMonitor = context.getBean(ServerMonitor.class);

        Thread monitorThread = new Thread(this.serverMonitor);
        monitorThread.start();
        monitorThread.join();
    }

    @PreDestroy
    public void destroy() {
        destroyManager();
        destroyMonitor();
    }

    @SuppressWarnings("unchecked")
    private void destroyManager() {
        Map<String, ModuleState> moduleStates = (Map<String, ModuleState>) context.getBean("moduleStates");
        if(!ModuleState.ENABLED.equals(moduleStates.get(Constant.MODULE_MANAGER))){
            return;
        }

        this.nodeManager.setAppState(AppState.DESTROY);
    }

    @SuppressWarnings("unchecked")
    private void destroyMonitor() {
        Map<String, ModuleState> moduleStates = (Map<String, ModuleState>) context.getBean("moduleStates");
        if(!ModuleState.ENABLED.equals(moduleStates.get(Constant.MODULE_MONITOR))){
            return;
        }

        this.serverMonitor.setAppState(AppState.DESTROY);
    }
}
