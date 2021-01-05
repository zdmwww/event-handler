package site.autzone.event.handler.task;

import java.util.concurrent.BlockingQueue;
import site.autzone.event.handler.cfg.EventTaskProperties;
import site.autzone.event.handler.cfg.EventTasksProperties;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.task.event.TaskEvent;
import site.autzone.event.handler.task.event.TaskEventMulticaster;

/**
 * 抛出队列中的任务去执行
 *
 * @author xiaowj
 */
public class Cast implements Runnable, Manageable {
  private STATUS status = STATUS.RUNNING;
  private TaskEventMulticaster schEventMulticaster;
  private BlockingQueue<Item> queue;

  private EventTaskProperties eventTaskProperties;
  private EventTasksProperties eventTasksProperties;

  public Cast(
      EventTaskProperties eventTaskProperties,
      EventTasksProperties eventTasksProperties,
      TaskEventMulticaster schEventMulticaster) {
    this.eventTaskProperties = eventTaskProperties;
    this.eventTasksProperties = eventTasksProperties;
    this.schEventMulticaster = schEventMulticaster;
  }

  public Cast(
      EventTaskProperties eventTaskProperties,
      EventTasksProperties eventTasksProperties,
      BlockingQueue<Item> q,
      TaskEventMulticaster schEventMulticaster) {
    this.eventTaskProperties = eventTaskProperties;
    this.eventTasksProperties = eventTasksProperties;
    this.queue = q;
    this.schEventMulticaster = schEventMulticaster;
  }

  @Override
  public void run() {
    Item item = null;
    try {
      while (true) {
        if (this.status == STATUS.INTERRUPT) {
          if (queue.isEmpty()) {
            break;
          }
          item = queue.poll();
        } else {
          // 并不能保证每次interrupt都能停止线程
          item = queue.take();
        }
        schEventMulticaster.multicastEvent(new TaskEvent(this.eventTaskProperties, this.eventTasksProperties, item));
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public BlockingQueue<Item> getQueue() {
    return queue;
  }

  public void setQueue(BlockingQueue<Item> queue) {
    this.queue = queue;
  }

  @Override
  public void pause() {
    this.status = STATUS.PAUSE;
  }

  @Override
  public void resume() {
    this.status = STATUS.PAUSE;
  }

  @Override
  public void interrupt() {
    this.status = STATUS.INTERRUPT;
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
