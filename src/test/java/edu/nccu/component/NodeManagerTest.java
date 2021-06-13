package edu.nccu.component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class NodeManagerTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${media.server.host}")
    private String mediaHost;

    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${zookeeper.maxRetries}")
    private int maxRetries;

    @Value("${zookeeper.baseSleepTimeMs}")
    private int sleepTime;

    @Test
    public void testLoadContext() {
        assertThat(context).isNotNull();
        assertThat(taskExecutor).isNotNull();

        NodeManager nodeManager = context.getBean(NodeManager.class);
        assertThat(nodeManager).isNotNull();
    }

    @Test
    public void testRunTwoManager() throws InterruptedException {
        NodeManager nodeManager1 = context.getBean(NodeManager.class);
        nodeManager1.setHostId(UUID.randomUUID().toString());
        taskExecutor.execute(nodeManager1);
        TimeUnit.SECONDS.sleep(3L);

        NodeManager nodeManager2 = context.getBean(NodeManager.class);
        nodeManager2.setHostId(UUID.randomUUID().toString());
        taskExecutor.execute(nodeManager2);
        TimeUnit.SECONDS.sleep(3L);

        nodeManager1.destroy();
        TimeUnit.SECONDS.sleep(9L);

    }
}
