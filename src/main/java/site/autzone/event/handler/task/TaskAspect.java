package site.autzone.event.handler.task;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.autzone.event.handler.item.Argument;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.ItemRepository;
import site.autzone.event.handler.task.event.TaskEvent;

@Aspect
@Component
public class TaskAspect {
  @Autowired ItemRepository itemRepository;

  @Pointcut(
      "execution(* site.autzone.event.handler.task..*.onApplicationEvent(java.util.EventObject+))")
  public void event() {}

  @Transactional
  @Around("event()")
  public void around(ProceedingJoinPoint pjp) {
    long start = System.currentTimeMillis();
    TaskEvent schEvent = (TaskEvent) pjp.getArgs()[0];
    Item itemSource = (Item) schEvent.getSource();
    AbstractEventTask.eventlog(itemSource, "task starts running execute.");
    if (itemRepository.count(itemSource, TaskStatus.Running) == 0) {
      AbstractEventTask.eventlog(itemSource, "The task must be running executed.");
      return;
    }

    try {
      Argument arg = itemRepository.getArgByItem(itemSource);
      itemSource.setAttribute(arg);
      pjp.proceed();
      if (TaskStatus.RanToCompletion.getCode() != itemSource.getStatus()) {
        itemRepository.updateTaskStatus(itemSource, TaskStatus.RanToCompletion);
      }
      AbstractEventTask.eventlog(
          itemSource, "The task is completed and executed.", System.currentTimeMillis() - start);
    } catch (Throwable e) {
      itemRepository.updateTaskStatus(itemSource, TaskStatus.Faulted);
      AbstractEventTask.eventlog(itemSource, "The task execution failed.", e);
    }
  }
}
