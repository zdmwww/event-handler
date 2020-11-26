package site.autzone.event.handler;

import java.util.Map;
import java.util.UUID;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import site.autzone.event.handler.repository.ICustomItemRepository;
import site.autzone.event.handler.task.Task;
import site.autzone.event.handler.task.discovery.TaskEventMulticaster;
import site.autzone.event.handler.task.listener.CastTask;
import site.autzone.event.handler.task.listener.FetchTask;
import site.autzone.event.handler.task.listener.HelloJobTask;
import site.autzone.event.handler.task.listener.HelloJobTask2;
import site.autzone.event.handler.task.listener.ItemEventLoop;

@Component
public class EventHandlerApplicationStartedEventListener
    implements ApplicationListener<ApplicationStartedEvent> {
  @Autowired private TaskEventMulticaster taskEventMulticaster;
  @Autowired private ICustomItemRepository customItemRepository;
  @Autowired Environment env;

  @Autowired Scheduler scheduler;

  @Override
  public void onApplicationEvent(ApplicationStartedEvent event) {
    Map<String, Object> workers = event.getApplicationContext().getBeansWithAnnotation(Task.class);
    workers.forEach(
        (key, listener) -> {
          if ("true".equals(env.getProperty("autzone." + key + ".enable", "true"))) {
            if (listener instanceof ApplicationListener) {
              taskEventMulticaster.addItemTask(listener);
            }
            if (listener instanceof ItemEventLoop) {
              ItemEventLoop loop = (ItemEventLoop) listener;
              loop.prepareFetch(new FetchTask(customItemRepository));
              loop.prepareCast(new CastTask(taskEventMulticaster));
              loop.startLoop();
            }
          }
        });

    // 启动删除成功数据job
    JobDetail job =
        JobBuilder.newJob(HelloJobTask.class).withIdentity(UUID.randomUUID().toString()).build();
    job.getJobDataMap().put("name", "测试123");
    job.getJobDataMap().put("itemSource", "测试");
    try {
      scheduler.scheduleJob(
          job,
          TriggerBuilder.newTrigger()
              .withIdentity(UUID.randomUUID().toString())
              .startNow()
              .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(3))
              .build());
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
    
    // 启动删除成功数据job
    JobDetail job2 =
        JobBuilder.newJob(HelloJobTask2.class).withIdentity(UUID.randomUUID().toString()).build();
    job2.getJobDataMap().put("name", "test");
    job2.getJobDataMap().put("itemSource", "tset");
    try {
      scheduler.scheduleJob(
          job2,
          TriggerBuilder.newTrigger()
              .withIdentity(UUID.randomUUID().toString())
              .startNow()
              .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(3))
              .build());
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }
}
