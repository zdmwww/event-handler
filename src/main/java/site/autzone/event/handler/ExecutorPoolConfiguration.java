package site.autzone.event.handler;

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
@ConfigurationProperties(prefix="eventexcutor")
public class ExecutorPoolConfiguration {
    @Value("${eventexcutor.core-pool-size}")
    private int corePoolSize;
    @Value("${eventexcutor.max-pool-size}")
    private int maxPoolSize;
    @Value("${eventexcutor.queue-capacity}")
    private int queueCapacity;
    @Value("${eventexcutor.keep-alive-seconds}")
    private int keepAliveSeconds;
    @Value("${eventexcutor.thread-name-prefix}")
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
