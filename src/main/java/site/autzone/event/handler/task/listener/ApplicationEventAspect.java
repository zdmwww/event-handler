package site.autzone.event.handler.task.listener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.repository.ItemCrudRepository;
import site.autzone.event.handler.task.discovery.TaskEvent;

@Aspect
@Component
public class ApplicationEventAspect {
	private static Logger TASKLOG = LoggerFactory.getLogger("TASKLOG");

	@Autowired
	ItemCrudRepository itemCrudRepository;

	@Pointcut("execution(* site.autzone.event.handler.task..*.onApplicationEvent(java.util.EventObject+))")
	public void event() {
	}

	@Transactional
	@Around("event()")
	public void around(ProceedingJoinPoint pjp) {
		TaskEvent schEvent = (TaskEvent) pjp.getArgs()[0];
		Item itemSource = (Item) schEvent.getSource();
		Optional<Item> itemOp = itemCrudRepository.findById(itemSource.getId());
		if (itemOp.isPresent()) {
			Item item = itemOp.get();
			TASKLOG.info("{}::{}::{}::{}", itemSource.getConsumerKey(), itemSource.getBatchId(),
					itemSource.getName(), "task starts running execute.");
			try {
				pjp.proceed();
				if (!TaskStatus.RanToCompletion.getName().equals(item.getStatus())) {
					item.setStatus(TaskStatus.RanToCompletion.getName());
				}
				TASKLOG.info("{}::{}::{}::{}", item.getConsumerKey(), item.getBatchId(), item.getName(),
						"The task is completed and executed.");
			} catch (Throwable e) {
				StringWriter writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter(writer);
				e.printStackTrace( printWriter);
				printWriter.flush();
				String stackTrace = writer.toString();
				item.setStatus(TaskStatus.Faulted.getName());
				item.setFinishMessage("任务执行失败.");
				item.setDetail(stackTrace);
				TASKLOG.error("{}::{}::{}::{}", itemSource.getConsumerKey(), itemSource.getBatchId(),
						itemSource.getName(), "The task execution failed." + stackTrace);
			} finally {
				itemCrudRepository.save(item);
			}

		} else {
			TASKLOG.info("{}::{}::{}::{}", itemSource.getConsumerKey(), itemSource.getBatchId(),
					itemSource.getName(), "The task is gone and will no longer be executed.");
		}
	}
}
