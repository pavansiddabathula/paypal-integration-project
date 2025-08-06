package com.hulkhire.payments.config;


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfig {

	@Bean
	ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(1000);
		executor.setThreadNamePrefix("Async-Task-");
		executor.initialize();
		log.info("ThreadPoolTaskExecutor initialized with core pool size: {}, max pool size: {}, queue capacity: {}",
				executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

		return executor;
	}
	
	@Bean
	RestClient restClient() {
	       PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
	       connectionManager.setMaxTotal(100);
	       connectionManager.setDefaultMaxPerRoute(20);
	       CloseableHttpClient httpClient = HttpClients.custom()
	           .setConnectionManager(connectionManager)
	           .evictIdleConnections(TimeValue.ofSeconds(30))
	           .build();
	       HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
	       requestFactory.setConnectTimeout(10000);  // 10 seconds
	       requestFactory.setReadTimeout(15000);     // 15 seconds
	       requestFactory.setConnectionRequestTimeout(10000);
	       return RestClient.builder()
	           .requestFactory(requestFactory)
	           .build();
	   }

	
	 
}

