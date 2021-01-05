package site.autzone.event.handler.task;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import site.autzone.event.handler.cfg.EventTaskProperties;
import site.autzone.event.handler.item.Item;

public interface EventLoop {
  BlockingQueue<Item> prepareQueue(EventTaskProperties eventTaskProperties);

  void prepareFetch(Fetcher fetchor);

  void prepareCast(Cast castor);

  void stopLoop();

  void startLoop(EventTaskProperties eventTaskProperties, Map<String, EventTaskProperties> registerTaskProperties);
}
