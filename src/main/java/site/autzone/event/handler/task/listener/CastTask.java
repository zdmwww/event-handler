package site.autzone.event.handler.task.listener;

import java.util.concurrent.BlockingQueue;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.task.discovery.TaskEvent;
import site.autzone.event.handler.task.discovery.TaskEventMulticaster;

/**
 * 抛出队列中的任务去执行
 * 
 * @author xiaowj
 *
 */
public class CastTask implements Runnable, Manageable {
	private STATUS status = STATUS.RUNNING;
	private TaskEventMulticaster schEventMulticaster;
	private BlockingQueue<Item> queue;

	public CastTask(TaskEventMulticaster schEventMulticaster) {
		this.schEventMulticaster = schEventMulticaster;
	}
	
	public CastTask(BlockingQueue<Item> q, TaskEventMulticaster schEventMulticaster) {
		this.queue = q;
		this.schEventMulticaster = schEventMulticaster;
	}

	@Override
	public void run() {
		Item item = null;
		try {
			while (true) {
				if(this.status == STATUS.INTERRUPT) {
					if(queue.isEmpty()) {
						break;
					}
					item = queue.poll();
				}else {
					//并不能保证每次interrupt都能停止线程
					item = queue.take();
				}
				schEventMulticaster.multicastEvent(new TaskEvent(item));
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
}
