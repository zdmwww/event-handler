package site.autzone.event.handler.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import site.autzone.event.handler.cfg.EventTaskProperties;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.task.event.GenericEventListener;

public abstract class AbstractEventTask
    implements GenericEventListener, Manageable, EventLoop {
  private static Logger TASKLOG = LoggerFactory.getLogger("TASKLOG");

  private BlockingQueue<Item> workqueue;
  private Fetcher fetchor;
  private Cast castor;

  protected STATUS status = STATUS.RUNNING;

  public List<ResolvableType> declaredEventTypes;

  public AbstractEventTask() {
    this.declaredEventTypes = resolveDeclaredEventTypes();
  }

  public List<ResolvableType> resolveDeclaredEventTypes() {
    declaredEventTypes = new ArrayList<>();
    declaredEventTypes.add(ResolvableType.forClass(this.getClass()));
    return declaredEventTypes;
  }

  @Override
  public boolean supportsEventType(ResolvableType eventType) {
    for (ResolvableType declaredEventType : this.declaredEventTypes) {
      if (declaredEventType.isAssignableFrom(eventType)) {
        return true;
      }
      Class<?> eventClass = eventType.getRawClass();
      if (eventClass != null && PayloadApplicationEvent.class.isAssignableFrom(eventClass)) {
        ResolvableType payloadType = eventType.as(PayloadApplicationEvent.class).getGeneric();
        if (declaredEventType.isAssignableFrom(payloadType)) {
          return true;
        }
      }
    }
    return eventType.hasUnresolvableGenerics();
  }

  public static void eventlog(Item item, String message) {
    if (item == null) {
      TASKLOG.info(message);
    } else {
      TASKLOG.info("{}-{}-{}:{}", item.getConsumerKey(), item.getBatchId(), item.getName(), message);
    }
  }

  public static void eventlog(Item item, String message, Throwable e) {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    e.printStackTrace(printWriter);
    printWriter.flush();
    String stackTrace = writer.toString();
    message = message + ":" + stackTrace;
    if (item == null) {
      TASKLOG.error(message);
    } else {
      TASKLOG.error(
          "{}::{}::{}::{}", item.getConsumerKey(), item.getBatchId(), item.getName(), message);
    }
  }

  public static void eventlog(Item item, String message, long cost) {
    if (item == null) {
      TASKLOG.info(message);
    } else {
      TASKLOG.info(
          "{}::{}::{}::{} costs:{}ms",
          item.getConsumerKey(),
          item.getBatchId(),
          item.getName(),
          message,
          cost);
    }
  }

  public static void eventlog(Item item) {
    eventlog(item, "");
  }

  @Override
  public boolean supportsSourceType(@Nullable Class<?> sourceType) {
    return true;
  }

  @Override
  public void interrupt() {
    this.status = STATUS.INTERRUPT;
  }

  @Override
  public void pause() {
    this.status = STATUS.PAUSE;
  }

  @Override
  public void resume() {
    this.status = STATUS.RUNNING;
  }

  public boolean checkStatus() {
    return checkStatus(null);
  }

  public boolean checkStatus(Item item) {
    if (status == STATUS.INTERRUPT) {
      eventlog(item, "事件被中断执行了.");
      return false;
    } else if (status == STATUS.PAUSE) {
      while (status != STATUS.RUNNING) {
        if (status == STATUS.INTERRUPT) {
          eventlog(item, "事件被中断执行了.");
          return false;
        } else if (status == STATUS.PAUSE) {
          Helper.SLEEP_SECONDS(10);
        }
      }
    }
    return true;
  }

  @Override
  public void prepareFetch(Fetcher fetchor) {
    this.fetchor = fetchor;
  }

  @Override
  public void prepareCast(Cast castor) {
    this.castor = castor;
  }

  @Override
  public void startLoop(EventTaskProperties eventTaskProperties, Map<String, EventTaskProperties> registerTaskProperties) {
    this.prepareQueue(eventTaskProperties);
    this.fetchor.setQueue(this.workqueue);
    new Thread(this.fetchor).start();
    this.castor.setQueue(this.workqueue);
    new Thread(this.castor).start();
    TASKLOG.info(
        "scan thread started. consumer key:{}, fetcherId:{}, fetchers：{}， work num:{}, interval: {}, batch size:{}",
        this.fetchor.getEventTaskProperties().getConsumerKey(),
        this.fetchor.getFetcherId(),
        this.fetchor.getEventTaskProperties().getFetchers(),
        eventTaskProperties.getSize(),
        this.fetchor.getEventTaskProperties().getInterval(),
        this.fetchor.getEventTaskProperties().getSize());
  }

  @Override
  public void stopLoop() {
    this.fetchor.interrupt();
    this.castor.interrupt();
  }

  @Override
  public BlockingQueue<Item> prepareQueue(EventTaskProperties eventTaskProperties) {
    workqueue = new ArrayBlockingQueue<>(eventTaskProperties.getSize());
    return this.workqueue;
  }
}
