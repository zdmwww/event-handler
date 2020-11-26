package site.autzone.event.handler;

import java.util.Map;

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
import site.autzone.event.handler.task.listener.ItemEventLoop;

@Component
public class EventHandlerApplicationStartedEventListener
    implements ApplicationListener<ApplicationStartedEvent> {
  @Autowired private TaskEventMulticaster taskEventMulticaster;
  @Autowired private ICustomItemRepository customItemRepository;
  @Autowired Environment env;

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
  }
}
