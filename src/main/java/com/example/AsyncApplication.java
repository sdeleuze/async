package com.example;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class AsyncApplication implements AsyncConfigurer {

	private static final Log LOG = LogFactory.getLog(AsyncApplication.class);

	private static final int THREAD_POOL_SIZE = 1;
	//private static final int THREAD_POOL_SIZE = 10;

	public static void main(String[] args) {
		SpringApplication.run(AsyncApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			final AsyncServiceA bean = ctx.getBean(AsyncServiceA.class);
			bean.a().whenComplete((value, ex) -> LOG.info(value));
		};
	}

	@Override
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor getAsyncExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(THREAD_POOL_SIZE);
		executor.setMaxPoolSize(THREAD_POOL_SIZE);
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {
			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
				LOG.error("Something wen wrong: " + ex.getMessage());
			}
		};
	}
}
