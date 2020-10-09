package site.autzone.event.handler.task.listener;

import java.util.concurrent.BlockingQueue;
import site.autzone.event.handler.domain.Item;

public interface ItemEventLoop {
	BlockingQueue<Item>  prepareQueue();
	void prepareFetch(FetchTask fetchor);
	void prepareCast(CastTask castor);
	void startLoop();
	void stopLoop();
}
