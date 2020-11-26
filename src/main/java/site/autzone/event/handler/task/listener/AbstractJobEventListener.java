package site.autzone.event.handler.task.listener;

import java.util.Arrays;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import site.autzone.event.handler.domain.builder.ItemBuilder;
import site.autzone.event.handler.repository.ItemCrudRepository;
import site.autzone.event.handler.task.Task;

/**
 * 可调度事件抽象类
 *
 * @author xiaowj
 */
public abstract class AbstractJobEventListener extends AbstractEventListener implements Job {
  @Autowired protected ItemCrudRepository itemCrudRepository;
  @Autowired ItemBuilder itemBuilder;

  /** 触发器出发之后发布事件 */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    if (context.getJobDetail().getJobDataMap() == null
        || context.getJobDetail().getJobDataMap().isEmpty()) {
      throw new IllegalArgumentException("job data is required");
    }
    Task taskAnno = this.getClass().getAnnotation(Task.class);
    itemBuilder.name(taskAnno.description()).consumerKey(taskAnno.consumerKey());
    context
        .getJobDetail()
        .getJobDataMap()
        .forEach(
            (k, v) -> {
              if (Arrays.stream(Item.class.getFields()).anyMatch(f -> f.getName().equals(k))) {
                itemBuilder.map(k, v);
              } else {
                itemBuilder.attribute().attr(k, String.valueOf(v)).end();
              }
            });
    for (int i = 0; i < 1000; i++) {
      long start = System.currentTimeMillis();
      itemCrudRepository.save(itemBuilder.build());
      System.out.println("cost:" +  (System.currentTimeMillis() - start)); 
    }
  }
}
