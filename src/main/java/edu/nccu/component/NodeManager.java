package edu.nccu.component;

import java.util.concurrent.TimeUnit;

import edu.nccu.domain.AppState;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class NodeManager implements Runnable {

    private static final String PATH_HOST = "/zkApp/host";
    private static final String PATH_ID = "/zkApp/id";

    @Value("${media.server.host}")
    private String mediaHost;

    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${media.server.recreateTimeSec}")
    private long recreateTime;

    private AppState appState;
    private String hostId;
    private ZkClient zkClient;

    @Autowired
    public NodeManager() {
        this.appState = AppState.INIT;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public AppState getAppState() {
        return appState;
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    @Override
    public void run() {
        ZkConnection zkConn = new ZkConnection(connectString);
        this.zkClient = new ZkClient(zkConn);

        main();
    }

    private void main() {
        try {
            while (true) {
                switch (appState) {
                    case INIT:
                        createNode();
                        break;
                    case DESTROY:
                        destroy();
                        return;
                    case CONNECTED:
                    default:
                        break;
                }

                TimeUnit.SECONDS.sleep(recreateTime);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void checkNode() {
        if (!zkClient.exists(PATH_HOST)) {
            appState = AppState.INIT;
            main();
        }

        log.info("get node value {}", zkClient.readData(PATH_ID).toString());
    }

    private synchronized void createNode() {
        try {
            log.info("creating node at {}", Thread.currentThread().getName());

            zkClient.createEphemeral(PATH_HOST, mediaHost);
            log.info("ephemeral node {} created with {} at {}", PATH_HOST, mediaHost, Thread.currentThread().getName());

            zkClient.createEphemeral(PATH_ID, hostId);
            log.info("ephemeral node {} created with {} at {}", PATH_ID, hostId, Thread.currentThread().getName());

            appState = AppState.CONNECTED;
        } catch (ZkNoNodeException noNode) {
            log.info("parent node has not been created.");
            log.info(ExceptionUtils.getStackTrace(noNode));

            String parentDir = PATH_HOST.substring(0, PATH_HOST.lastIndexOf('/'));
            zkClient.createPersistent(parentDir, true);

            createNode();
        } catch (ZkNodeExistsException nodeExist) {
            log.info("node had been created.");
            log.info(ExceptionUtils.getStackTrace(nodeExist));
        }
    }

    public void destroy() {
        log.info("shutting down and destroying...");

        this.zkClient.delete(PATH_ID);
        this.zkClient.delete(PATH_HOST);
        this.zkClient.close();
    }
}
