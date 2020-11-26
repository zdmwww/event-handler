package site.autzone.event.handler.task.listener;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.autzone.event.handler.repository.ICustomItemRepository;
import site.autzone.event.handler.task.Task;
import site.autzone.event.handler.task.TaskArg;
import site.autzone.event.handler.task.TaskConsumer;
import site.autzone.event.handler.task.TaskExecutor;
import site.autzone.event.handler.task.discovery.TaskEvent;

@Task(consumerKey="Clean", 
description="清理已完成任务")
@TaskConsumer(interval=3600000, batchSize=100, workNum=1)
@TaskExecutor(corePoolSize=1,maxPoolSize=1,queueCapacity=2,keepAliveSeconds=30)
@TaskArg(required=true,argCode="maxResult", description="每次删除的任务数目", sampleValues={"1000"})
@Component
public class CleanTask extends AbstractEventListener {
	@Autowired ICustomItemRepository customItemRepository;
	
	@Override
	public void onApplicationEvent(TaskEvent event) {
		 Optional<Integer> maxResult = event.getItemArg("maxResult", Integer.class);
		 if(maxResult.isPresent()) {
			 customItemRepository.cleanItems(maxResult.get());
		 }
	}
}
