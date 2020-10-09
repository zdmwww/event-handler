package site.autzone.event.handler.task.discovery;

import java.util.Optional;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.context.ApplicationEvent;
import site.autzone.event.handler.domain.Attribute;
import site.autzone.event.handler.domain.Item;

/**
 * 
 * @author xiaowj
 *
 */
public class TaskEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;

  private Item item;

  public TaskEvent(Item item) {
    super(item);
    this.item = item;
  }

  public Item getItem() {
    return this.item;
  }

  public Optional<Attribute> getItemArg(String key) {
    return this.item.getAttribute(key);
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> getItemArg(String key, Class<T> cl) {
    Optional<Attribute> attr = this.getItemArg(key);
    if (attr.isPresent()) {
      Object o = ConvertUtils.convert(attr.get().getValue(), cl);
      return Optional.of((T) o);
    } else {
      return Optional.empty();
    }
  }
}
