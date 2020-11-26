package site.autzone.event.handler.task.listener;

import java.util.concurrent.BlockingQueue;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.repository.ICustomItemRepository;

/**
 * 获取任务到队列中
 *
 * @author xiaowj
 */
public class FetchTask implements Runnable, Manageable {
  private STATUS status = STATUS.RUNNING;

  private BlockingQueue<Item> queue;

  private ICustomItemRepository itemCrudRepository;

  /** 消费者key */
  private String consumerKey;

  /** 每批次扫描的数量 */
  private int batchSize = 1;

  /** 扫描的间隔时间毫秒 */
  private int interval = 1000;

  public FetchTask(ICustomItemRepository itemCrudRepository) {
    this.itemCrudRepository = itemCrudRepository;
  }

  public FetchTask(BlockingQueue<Item> queue, ICustomItemRepository itemCrudRepository) {
    this.queue = queue;
    this.itemCrudRepository = itemCrudRepository;
  }

  @Override
  public void run() {
    while (!(this.status == STATUS.INTERRUPT)) {
      if (status == STATUS.INTERRUPT) {
        break;
      } else if (status == STATUS.PAUSE) {
        while (status != STATUS.RUNNING) {
          if (status == STATUS.INTERRUPT) {
            break;
          } else if (status == STATUS.PAUSE) {
            Helper.SLEEP_SECONDS(10);
          }
        }
      }

      try {
        itemCrudRepository
            .fetchItems(this.consumerKey, this.batchSize)
            .forEach(
                item -> {
                  try {
                    queue.put(item);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                });
      } catch (Exception e) {
        e.printStackTrace();
        // 乐观锁
      }
      Helper.SLEEP_MILLISECONDS(interval);
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

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public int getInterval() {
    return interval;
  }
}
