package site.autzone.event.handler.cfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 默认的消费者线程池配置
 * @author xiaowj
 *
 */
@Configuration
@ConfigurationProperties(prefix="autzone.executor")
public class ExecutorPoolConfiguration {
    @Value("${executor.core-pool-size:10}")
    private int corePoolSize;
    @Value("${executor.max-pool-size: 1000}")
    private int maxPoolSize;
    @Value("${executor.queue-capacity: 2000}")
    private int queueCapacity;
    @Value("${executor.keep-alive-seconds: 10}")
    private int keepAliveSeconds;
    @Value("${executor.thread-name-prefix: default.}")
    private String threadNamePrefix;
    
    @Bean(name="defaultTaskEventExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(keepAliveSeconds);
        pool.setCorePoolSize(corePoolSize);//核心线程池数
        pool.setMaxPoolSize(maxPoolSize); // 最大线程
        pool.setQueueCapacity(queueCapacity);//队列容量
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()); //队列满，线程被拒绝执行策略
        pool.setThreadNamePrefix(threadNamePrefix);
        return pool;
    }
}
