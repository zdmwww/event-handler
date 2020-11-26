package site.autzone.event.handler.task.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.task.Task;
import site.autzone.event.handler.task.TaskConsumer;
import site.autzone.event.handler.task.discovery.GenericEventListener;

public abstract class AbstractEventListener
    implements GenericEventListener, Manageable, ItemEventLoop {
  private static Logger TASKLOG = LoggerFactory.getLogger("TASKLOG");

  private BlockingQueue<Item> workqueue;
  private FetchTask fetchor;
  private CastTask castor;

  protected STATUS status = STATUS.RUNNING;

  public List<ResolvableType> declaredEventTypes;

  public AbstractEventListener() {
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

  public void eventlog(Item item, String message) {
    if (item == null) {
      TASKLOG.info(message);
    } else {
      TASKLOG.info("{}::{}::{}::{}", item.getConsumerKey(), item.getBatchId(), item.getName(), message);
    }
  }

  public void eventlog(Item item) {
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
  public void prepareFetch(FetchTask fetchor) {
    this.fetchor = fetchor;
  }

  @Override
  public void prepareCast(CastTask castor) {
    this.castor = castor;
  }

  @Override
  public void startLoop() {
    Task itemJob = this.getClass().getAnnotation(Task.class);
    this.prepareQueue();
    if (itemJob != null) {
      this.fetchor.setConsumerKey(itemJob.consumerKey());
      TaskConsumer taskConsumer = this.getClass().getAnnotation(TaskConsumer.class);
      if (taskConsumer != null) {
        this.fetchor.setBatchSize(taskConsumer.batchSize());
        this.fetchor.setInterval(taskConsumer.interval());
      }
      this.fetchor.setQueue(this.workqueue);
      new Thread(this.fetchor).start();

      this.castor.setQueue(this.workqueue);
      new Thread(this.castor).start();
      TASKLOG.info(
          "scan thread started. consumer key:{}, work num:{}, interval: {}, batch size:{}",
          this.fetchor.getConsumerKey(),
          workqueue.size(),
          this.fetchor.getInterval(),
          this.fetchor.getBatchSize());
    }
  }

  @Override
  public void stopLoop() {
    this.fetchor.interrupt();
    this.castor.interrupt();
  }

  @Override
  public BlockingQueue<Item> prepareQueue() {
    TaskConsumer taskConsumer = this.getClass().getAnnotation(TaskConsumer.class);
    int workerNum = 1;
    if (taskConsumer != null) {
      workerNum = taskConsumer.workNum();
    }
    workqueue = new ArrayBlockingQueue<>(workerNum);
    return this.workqueue;
  }
}
