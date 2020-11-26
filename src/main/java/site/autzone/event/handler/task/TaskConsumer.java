package site.autzone.event.handler.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskConsumer {
  /**
   * 一个消费者缓存的任务数
   * @return
   */
  int workNum() default 1;
  /**
   * 消费者扫描一次数据库获取的任务数量
   * @return
   */
  int batchSize() default 1000;
  
  /**
   * 任务消费者获取任务的间隔时间（单位毫秒）
   * @return
   */
  int interval() default 120000;
}
