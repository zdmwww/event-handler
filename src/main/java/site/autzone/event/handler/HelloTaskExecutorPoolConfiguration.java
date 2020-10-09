package site.autzone.event.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConditionalOnProperty(prefix="hellotask", name="eventexcutor", havingValue = "true")
public class HelloTaskExecutorPoolConfiguration {
    @Value("${hellotask.eventexcutor.core-pool-size}")
    private int corePoolSize;

    @Value("${hellotask.eventexcutor.max-pool-size}")
    private int maxPoolSize;

    @Value("${hellotask.eventexcutor.queue-capacity}")
    private int queueCapacity;

    @Value("${hellotask.eventexcutor.keep-alive-seconds}")
    private int keepAliveSeconds;
    
    @Value("${hellotask.eventexcutor.thread-name-prefix}")
    private String threadNamePrefix;


    @Bean(name="smart.event.HELLO_WORLD")
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
