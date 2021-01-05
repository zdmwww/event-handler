package site.autzone.event.handler.task.event;

import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 
 * @author xiaowj
 *
 */
public interface GenericEventListener extends ApplicationListener<TaskEvent> {
	
	boolean supportsEventType(ResolvableType eventType);

	boolean supportsSourceType(@Nullable Class<?> sourceType);
}
