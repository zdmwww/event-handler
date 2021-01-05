package site.autzone.event.handler.task.handler;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.autzone.event.handler.item.ItemRepository;
import site.autzone.event.handler.task.AbstractEventTask;
import site.autzone.event.handler.task.annotation.Task;
import site.autzone.event.handler.task.annotation.TaskArg;
import site.autzone.event.handler.task.event.TaskEvent;

@Task(consumerKey="clean", name="任务清理", interval=3600000, size=100, fetchers=1, enable = false)
@TaskArg(required=true,key="maxResult", desc="每次删除的任务数目", sampleValues={"1000"})
@TaskArg(required=true,key="partitions", desc="删除的分区", sampleValues={"1,2,3,4"})
@Component
public class Cleaner extends AbstractEventTask {
	@Autowired ItemRepository itemRepository;
	
	@Override
	public void onApplicationEvent(TaskEvent event) {
		 Optional<Integer> maxResult = event.getItemArg("maxResult", Integer.class);
		 if(maxResult.isPresent()) {
			 for (String partition : event.getString("partitions").split(",")) {
				 itemRepository.delete(maxResult.get(), Integer.parseInt(partition));
			 }
		 }
	}
}
