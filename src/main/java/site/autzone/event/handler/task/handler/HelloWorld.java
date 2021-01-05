package site.autzone.event.handler.task.handler;

import java.util.Optional;
import site.autzone.event.handler.item.rest.dto.AttributeDto;
import site.autzone.event.handler.task.AbstractJobEventTask;
import site.autzone.event.handler.task.annotation.Task;
import site.autzone.event.handler.task.annotation.TaskArg;
import site.autzone.event.handler.task.event.TaskEvent;

@Task(consumerKey = "hello_world", name = "示例任务", enable = false)
@TaskArg(required = true, key = "message", desc = "消息", sampleValues = {"lol"})
@TaskArg(required = true, key = "whoami", desc = "我是谁", sampleValues = {"codeman"})
public class HelloWorld extends AbstractJobEventTask {
  @Override
  public void onApplicationEvent(TaskEvent event) {
    Optional<AttributeDto> attrOptional = event.getItemArg("message");
    if (attrOptional.isPresent()) {
      eventlog(event.getItem(), event.getString("whoami") + "->" + attrOptional.get().getValue());
    } else {
      eventlog(event.getItem());
    }
  }
}
