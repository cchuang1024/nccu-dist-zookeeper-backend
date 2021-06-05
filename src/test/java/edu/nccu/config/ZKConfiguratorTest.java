package edu.nccu.config;

import org.apache.curator.framework.CuratorFramework;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ZKConfiguratorTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testLoadContext() {
        CuratorFramework zkClient = context.getBean(CuratorFramework.class);
        assertThat(zkClient).isNotNull();
    }
}
