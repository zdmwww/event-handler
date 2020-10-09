package site.autzone.event.handler.task;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 任务线程池配置
 * @author xiaowj
 *
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskExecutor {
    int corePoolSize() default 1;
    int maxPoolSize() default 5;
    int queueCapacity() default 50;
    int keepAliveSeconds() default 300;
    String threadNamePrefix() default "event.taskexecutor";
}
