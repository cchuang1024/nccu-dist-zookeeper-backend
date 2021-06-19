package edu.nccu.component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import edu.nccu.domain.AppState;
import edu.nccu.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class ServerMonitor implements Runnable {

    @Value("${media.server.monitorPeriodSec}")
    private long monitorPeriod;

    private AppState appState;
    private ZookeeperService zkService;

    @Autowired
    public ServerMonitor(ZookeeperService zkService) {
        this.appState = AppState.INIT;
        this.zkService = zkService;
    }

    public AppState getAppState() {
        return appState;
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    @Override
    public void run() {
        try {
            while (true) {
                switch (appState) {
                    case INIT:
                        checkHost();
                        break;
                    case DESTROY:
                        destroy();
                        return;
                    case CONNECTED:
                    default:
                        break;
                }

                TimeUnit.SECONDS.sleep(monitorPeriod);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        try {
            while (true) {
                TimeUnit.SECONDS.sleep(monitorPeriod);
            }
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void checkHost() {
        log.info("check host...");

        String host = zkService.readHost();
        if (StringUtils.isEmpty(host)) {
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = prepareRequest(host);
            try (CloseableHttpResponse response = client.execute(request)) {
                response.getEntity();
            } catch (HttpHostConnectException | ClientProtocolException ex) {
                log.info("connection failed and clean node.");
                cleanHost();
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void cleanHost() {
        try {
            zkService.cleanHost();
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    private HttpGet prepareRequest(String host) {
        return new HttpGet(String.format("http://%s:1935/", host));
    }

    public void destroy() {
        log.info("shutting down and destroying...");
        zkService.destroy();
    }
}
