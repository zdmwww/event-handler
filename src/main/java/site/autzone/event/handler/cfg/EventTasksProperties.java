package site.autzone.event.handler.cfg;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "tasks")
@ConditionalOnResource(resources = {"classpath:event-tasks.yml"})
@PropertySource(value = "classpath:event-tasks.yml", factory = YamlPropertySourceFactory.class)
public class EventTasksProperties {
  private List<EventTaskProperties> eventTasks = new ArrayList<EventTaskProperties>();

  public List<EventTaskProperties> getEventTasks() {
    return eventTasks;
  }

  public void setEventTasks(List<EventTaskProperties> eventTasks) {
    this.eventTasks = eventTasks;
  }
}
