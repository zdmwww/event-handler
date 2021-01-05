package site.autzone.event.handler.task.event;

import java.util.Optional;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.context.ApplicationEvent;
import com.alibaba.fastjson.JSONArray;
import site.autzone.event.handler.cfg.EventTaskProperties;
import site.autzone.event.handler.cfg.EventTasksProperties;
import site.autzone.event.handler.cfg.TaskArgProperties;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.builder.Arg;
import site.autzone.event.handler.item.rest.dto.AttributeDto;

/** @author xiaowj */
public class TaskEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;

  private Item item;
  private EventTaskProperties eventTaskProperties;
  private EventTasksProperties eventTasksProperties;

  public TaskEvent(
      EventTaskProperties eventTaskProperties,
      EventTasksProperties eventTasksProperties,
      Item item) {
    super(item);
    this.item = item;
    this.eventTaskProperties = eventTaskProperties;
    this.eventTasksProperties = eventTasksProperties;
  }

  public Item getItem() {
    return this.item;
  }

  public String getString(String key) {
    Optional<AttributeDto> attr = this.getItemArg(key);
    if (attr.isPresent()) {
      return attr.get().getValue();
    }
    return null;
  }

  public <T> T get(String key, Class<T> cl) {
    Optional<T> attr = this.getItemArg(key, cl);
    if (attr.isPresent()) {
      return attr.get();
    } else {
      return null;
    }
  }

  public Optional<AttributeDto> getItemArg(String key) {
    Optional<Arg> argument =
        JSONArray.parseArray(this.item.getAttribute().getValue(), Arg.class)
            .stream()
            .filter(arg -> key.equals(arg.getKey()))
            .findFirst();
    if (argument.isPresent()) {
      AttributeDto attr = new AttributeDto(argument.get());
      return Optional.of(attr);
    }
    if (this.eventTaskProperties.getArgs() != null) {
      Optional<TaskArgProperties> argOp =
          this.eventTaskProperties
              .getArgs()
              .stream()
              .filter(arg -> key.equals(arg.getKey()))
              .findAny();
      if (argOp.isPresent()) {
        AttributeDto attr = new AttributeDto();
        attr.setKey(argOp.get().getKey());
        attr.setValue(argOp.get().getValue());
        return Optional.of(attr);
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> getItemArg(String key, Class<T> cl) {
    Optional<AttributeDto> attr = this.getItemArg(key);
    if (attr.isPresent()) {
      Object o = ConvertUtils.convert(attr.get().getValue(), cl);
      return Optional.of((T) o);
    } else {
      return Optional.empty();
    }
  }

  public EventTaskProperties getEventTaskProperties() {
    return eventTaskProperties;
  }

  public void setEventTaskProperties(EventTaskProperties eventTaskProperties) {
    this.eventTaskProperties = eventTaskProperties;
  }

  public EventTasksProperties getEventTasksProperties() {
    return eventTasksProperties;
  }

  public void setEventTasksProperties(EventTasksProperties eventTasksProperties) {
    this.eventTasksProperties = eventTasksProperties;
  }
}
