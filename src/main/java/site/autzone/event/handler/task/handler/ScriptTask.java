package site.autzone.event.handler.task.handler;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.micrometer.core.instrument.util.IOUtils;
import site.autzone.event.handler.task.AbstractJobEventTask;
import site.autzone.event.handler.task.annotation.Task;
import site.autzone.event.handler.task.annotation.TaskArg;
import site.autzone.event.handler.task.event.TaskEvent;

@Task(consumerKey = "scriptTask", name = "脚本任务", enable = false)
@TaskArg(
    required = true,
    key = "scriptContent",
    desc = "脚本内容",
    sampleValues = {"println \"hello\""})
@TaskArg(
    required = false,
    key = "scriptTemplatePath",
    desc = "脚本模板路径",
    sampleValues = {"script/template"})
@TaskArg(required = true, key = "message", desc = "消息", sampleValues = {"lol"})
@TaskArg(required = true, key = "whoami", desc = "我是谁", sampleValues = {"codeman"})
public class ScriptTask extends AbstractJobEventTask {
  @Autowired private Binding binding;

  @Override
  public void onApplicationEvent(TaskEvent event) {
    long start = System.currentTimeMillis();
    Resource resource =
        new ClassPathResource(
            event.getString("scriptTemplatePath") != null
                ? event.getString("scriptTemplatePath")
                : "script/template");
    try {
      binding.setVariable("taskEvent", event);
      GroovyShell shell = new GroovyShell(binding);
      String scriptContent = event.getString("scriptContent");
      String template = IOUtils.toString(resource.getInputStream());
      scriptContent = String.format(template, scriptContent);
      eventlog(event.getItem(), scriptContent);
      Object result = shell.evaluate(scriptContent);
      eventlog(
          event.getItem(),
          String.valueOf(result),
          System.currentTimeMillis() - start);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
