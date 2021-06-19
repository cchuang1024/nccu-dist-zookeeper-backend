package edu.nccu.component;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.Test;

@Slf4j
public class HttpTest {

    @Test
    public void testCheckEmptyIp(){
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("172.24.200.200");
            try (CloseableHttpResponse response = client.execute(request)) {
                response.getEntity();
            }catch (Exception e) {
                log.error("log 1");
                log.error(ExceptionUtils.getStackTrace(e));
            }
        } catch (Exception e) {
            log.error("log 2");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
