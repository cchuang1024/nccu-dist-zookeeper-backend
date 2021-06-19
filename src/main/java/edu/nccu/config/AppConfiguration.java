package edu.nccu.config;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.nccu.domain.AppState;
import edu.nccu.domain.ApplicationState;
import edu.nccu.domain.ModuleState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfiguration {

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(8);
        pool.setMaxPoolSize(12);
        pool.setQueueCapacity(100);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.initialize();
        return pool;
    }

    @Bean
    public ApplicationState appState() {
        return new ApplicationState(AppState.INIT);
    }

    @Value("${module.monitor}")
    private Boolean monitorEnabled;

    @Value("${module.manager}")
    private Boolean managerEnabled;

    @Bean("moduleStates")
    public Map<String, ModuleState> moduleStates() {
        return ImmutableMap.<String, ModuleState>builder()
                           .put(Constant.MODULE_MONITOR, ModuleState.ofConfig(monitorEnabled))
                           .put(Constant.MODULE_MANAGER, ModuleState.ofConfig(managerEnabled))
                           .build();
    }
}
