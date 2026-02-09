package com.myprojects.java_bonds_advisor.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

    @Bean(name = "myAsyncExecutor")
    public ThreadPoolTaskExecutor tBankAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int availableProcessors = Runtime.getRuntime().availableProcessors(); // gets quantity of the system cores

        executor.setCorePoolSize(availableProcessors * 3);
        executor.setMaxPoolSize(availableProcessors * 6); // a reserve under a load
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("my-async-executor-thread-");
        executor.initialize();

        return executor;
    }
}
