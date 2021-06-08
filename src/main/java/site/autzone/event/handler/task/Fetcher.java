package site.autzone.event.handler.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import site.autzone.event.handler.cfg.EventTaskProperties;
import site.autzone.event.handler.cfg.EventTasksProperties;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.ItemRepository;

/**
 * 获取任务到队列中
 *
 * @author xiaowj
 */
public class Fetcher implements Runnable, Manageable {
  private static Logger TASKLOG = LoggerFactory.getLogger("TASKLOG");

  private STATUS status = STATUS.RUNNING;

  private BlockingQueue<Item> queue;

  private int fetcherId = 0;
  private int partition;
  private String p;
  private String itemP;

  private ItemRepository itemRepository;

  private EventTaskProperties eventTaskProperties;
  private EventTasksProperties eventTasksProperties;

  public Fetcher() {}

  public Fetcher(
      EventTaskProperties eventTaskProperties,
      EventTasksProperties eventTasksProperties,
      ItemRepository itemRepository) {
    this.eventTaskProperties = eventTaskProperties;
    this.eventTasksProperties = eventTasksProperties;
    this.p = eventTaskProperties.getP();
    this.fetcherId = Integer.parseInt(p.substring(p.indexOf("_p") + "_p".length()));
    this.itemRepository = itemRepository;
    this.partition = Integer.parseInt(p.substring(p.indexOf("_p") + "_p".length()));
    this.itemP = "smart_item" + p;
  }

  public Fetcher(
      EventTaskProperties eventTaskProperties,
      EventTasksProperties eventTasksProperties,
      int fetcherId,
      int partition,
      ItemRepository itemRepository) {
    this.eventTaskProperties = eventTaskProperties;
    this.eventTasksProperties = eventTasksProperties;
    this.fetcherId = fetcherId;
    this.itemRepository = itemRepository;
    this.partition = partition;
    this.p = p();
    this.itemP = "smart_item" + p;
  }

  public Fetcher(BlockingQueue<Item> queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        long start = System.currentTimeMillis();
        while (this.status == STATUS.PAUSE) {
          Helper.SLEEP_SECONDS(1);
        }
        if (this.status == STATUS.INTERRUPT) {
          break;
        }
        int success =
            fetchAndLock(
                this.eventTaskProperties.getConsumerKey(), this.eventTaskProperties.getSize());
        TASKLOG.info(
            "success fetch-{} item. fetcherId:{}, partition:{}, consumer key:{}, work num:{}, interval: {}ms, batch size:{}, costs:{}ms",
            success,
            this.fetcherId,
            this.itemP,
            this.eventTaskProperties.getConsumerKey(),
            queue.size(),
            this.eventTaskProperties.getInterval(),
            this.eventTaskProperties.getSize(),
            System.currentTimeMillis() - start);
        Helper.SLEEP_MILLISECONDS(this.eventTaskProperties.getInterval());
      } catch (Exception e) {
        TASKLOG.error("Fetch And Lock Exception.", e);
      }
    }
  }

  public int fetchAndLock(String consumerKey, int maxResult) {
    List<Item> items =
        itemRepository.fetchItems(this.itemP, consumerKey, maxResult, TaskStatus.Created);
    if(CollectionUtils.isEmpty(items)){
      return 0;
    }
    Object[][] params = new Object[items.size()][4];
    for (int i = 0; i < items.size(); i++) {
      Item item = items.get(i);
      params[i] =
          new Object[] {
            TaskStatus.Running.getCode(),
            LocalDateTime.now().toString(),
            item.getId(),
            item.getVersion()
          };
    }
    int[] result = itemRepository.batch(this.itemP, params);
    int succ = 0;
    for (int i = 0; i < result.length; i++) {
      int idx = result[i];
      if (idx == 1) {
        Item item = items.get(i);
        item.setVersion(item.getVersion() + 1);
        item.setPartition(this.p);
        try {
          queue.put(item);
          succ++;
        } catch (InterruptedException e) {
          TASKLOG.info("Fetch And Lock Exception. sleep and refetch.", e);
        }
      }
    }
    return succ;
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

  public STATUS getStatus() {
    return status;
  }

  public void setStatus(STATUS status) {
    this.status = status;
  }

  private String p() {
    if (this.partition == 0) {
      return "_p0";
    }
    return "_p" + this.fetcherId % this.partition;
  }

  public int getFetcherId() {
    return fetcherId;
  }

  public void setFetcherId(int fetcherId) {
    this.fetcherId = fetcherId;
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
