package edu.nccu.component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class NodeManager implements Runnable {
    private static final long WAIT = 5L;
    private static final String PATH = "/zkApp/media";

    @Value("${media.server.host}")
    private String mediaHost;

    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${zookeeper.maxRetries}")
    private int maxRetries;

    @Value("${zookeeper.baseSleepTimeMs}")
    private int sleepTime;

    private CuratorFramework zkClient;
    private PersistentNode node;
    private String hostId;

    @Autowired
    public NodeManager() { }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public void run() {
        try {
            ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(sleepTime, maxRetries);
            zkClient = CuratorFrameworkFactory.newClient(connectString, retryPolicy);

            zkClient.start();

            node = new PersistentNode(zkClient, CreateMode.EPHEMERAL, false, PATH, hostId.getBytes(StandardCharsets.UTF_8));
            node.start();
            node.waitForInitialCreate(WAIT, TimeUnit.SECONDS);

            log.info("created a ephemeral node {} with {} at {}", PATH, hostId, Thread.currentThread().getName());

            while (true) {
                TimeUnit.SECONDS.sleep(10L);
            }
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @PreDestroy
    public void destroy() {
        CloseableUtils.closeQuietly(node);
        CloseableUtils.closeQuietly(zkClient);
    }
}
