package site.autzone.event.handler.task.listener;

import java.util.Optional;
import org.springframework.stereotype.Component;
import site.autzone.event.handler.domain.Attribute;
import site.autzone.event.handler.task.Task;
import site.autzone.event.handler.task.TaskArg;
import site.autzone.event.handler.task.TaskConsumer;
import site.autzone.event.handler.task.TaskExecutor;
import site.autzone.event.handler.task.discovery.TaskEvent;

@Task(consumerKey = "HELLO_WORLD2", description = "示例任务", created = "2017-08-12 15:58:00")
@TaskArg(
    required = true,
    argCode = "message",
    description = "示例参数",
    sampleValues = {"lol"})
@TaskConsumer(interval = 3000, batchSize = 1000, workNum = 200)
@TaskExecutor(corePoolSize = 200, maxPoolSize = 300, queueCapacity = 500, keepAliveSeconds = 300)
@Component
public class HelloJobTask2 extends AbstractJobEventListener {
  @Override
  public void onApplicationEvent(TaskEvent event) {
    Optional<Attribute> attrOptional = event.getItemArg("message");
    if (attrOptional.isPresent()) {
      eventlog(event.getItem(), attrOptional.get().getValue());
    } else {
      eventlog(event.getItem());
    }
  }
}
