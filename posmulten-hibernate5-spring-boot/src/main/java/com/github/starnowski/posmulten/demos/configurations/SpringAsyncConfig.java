package com.github.starnowski.posmulten.demos.configurations;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {
    @Value("${threadpool.corepoolsize:5}")
    int corePoolSize;

    @Value("${threadpool.maxpoolsize:10}")
    int maxPoolSize;

    @Bean("taskExecutor")
    @Override
    public Executor getAsyncExecutor () {
        return new ConcurrentTaskExecutor(
                Executors.newFixedThreadPool(maxPoolSize));
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler () {
        return (throwable, method, objects) ->
                System.out.println("-- exception handler -- "+throwable);
    }
}
