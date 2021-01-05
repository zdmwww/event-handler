package site.autzone.event.handler.task;

import java.util.Arrays;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.ItemRepository;
import site.autzone.event.handler.item.builder.ItemBuilder;
import site.autzone.event.handler.task.annotation.Task;

/**
 * 可调度事件抽象类
 *
 * @author xiaowj
 */
public abstract class AbstractJobEventTask extends AbstractEventTask implements Job {
  @Autowired ItemRepository itemRepository;

  /** 触发器出发之后发布事件 */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    if (context.getJobDetail().getJobDataMap() == null
        || context.getJobDetail().getJobDataMap().isEmpty()) {
      throw new IllegalArgumentException("job data is required");
    }
    Task taskAnno = this.getClass().getAnnotation(Task.class);
    ItemBuilder itemBuilder = new ItemBuilder().name(taskAnno.name()).consumerKey(taskAnno.consumerKey());
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
    itemRepository.save(itemBuilder.build());
  }
}
