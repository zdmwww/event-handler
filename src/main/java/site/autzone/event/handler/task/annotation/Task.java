package site.autzone.event.handler.task.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Task {
	/**
	 * 任务名称
	 * @return
	 */
    String name();
    /**
     * ConsumerKey
     * @return
     */
    String consumerKey();
    
    /**
     * 一个消费者数目（默认值：1）
     * @return
     */
    int fetchers() default 1;
    /**
     * 消费者扫描一次数据库获取的任务数量（默认值：100）
     * @return
     */
    int size() default 100;
    
    /**
     * 任务消费者获取任务的间隔时间（单位毫秒,默认值10000）
     * @return
     */
    int interval() default 10000;
    
    /**
     * 是否启用（默认值：true）
     * @return
     */
    boolean enable() default true;
}
