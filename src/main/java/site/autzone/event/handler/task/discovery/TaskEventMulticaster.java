package site.autzone.event.handler.task.discovery;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.TargetClassAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.task.Task;
import site.autzone.event.handler.task.TaskExecutor;

/**
 * @author xiaowj
 *
 */
@Service
public class TaskEventMulticaster extends SimpleApplicationEventMulticaster 
	implements InitializingBean, ApplicationContextAware {  
	private ApplicationContext context;
	private final static String executorDefaultPreFixName = "smart.event.executor.";
	//默认的线程池
	@Autowired
	@Qualifier("defaultTaskEventExecutor")
	ThreadPoolTaskExecutor defaultTaskEventExcutor;
	//各个工作节点的线程池注册中心,为每个事件处理器单独设置线程池
	//beanname -> ezremedy.tp.{classSimpleName}
    @Autowired
    ConfigurableBeanFactory beanFactory;

	@Override  
	public void setApplicationContext(ApplicationContext applicationContext)  
	        throws BeansException {  
	    context = applicationContext;  
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setTaskExecutor(defaultTaskEventExcutor);
	}
	
	@Override
	public void multicastEvent(final ApplicationEvent event) {
		multicastEvent(event, resolveDefaultEventType(event));
	}
	
	@Override
	public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		for (final ApplicationListener<?> listener : getEventApplicationListeners(event, type)) {
			Executor executor = null;
			String executorBeanName = executorDefaultPreFixName+listener.getClass().getSimpleName();
			if(context.containsBean(executorBeanName)) {
				Object beanTp = context.getBean(executorBeanName);
				if(beanTp instanceof Executor) {
					executor = (Executor)beanTp;
				}
			}
			if(executor == null){
				executor = getTaskExecutor();
			}
			if (executor != null) {
				executor.execute(() -> invokeListener(listener, event));
			}else {
				invokeListener(listener, event);
			}
		}
	}

	private List<ApplicationListener<?>> getEventApplicationListeners(ApplicationEvent event, ResolvableType type) {
		List<ApplicationListener<?>> allListeners = new ArrayList<>();
		for(ApplicationListener<?> applicationListener : super.getApplicationListeners()) {
			
			Object object = event.getSource();
			if(object instanceof Item) {
				Item item = (Item)object;
				Task itemJob = this.getAnnotationFromAwaredListener(applicationListener, Task.class);
				if(itemJob != null && item.getConsumerKey().equals(itemJob.consumerKey())) {
					allListeners.add(applicationListener);
				}
			}
		}
		return allListeners;
	}
	
	public  <A extends Annotation> A getAnnotationFromAwaredListener(ApplicationListener<?> applicationListener,
			Class<A> a) {
		if(applicationListener instanceof TargetClassAware) {
			TargetClassAware aware = (TargetClassAware)applicationListener;
			return aware.getTargetClass().getAnnotation(a);
		}else {
			return  applicationListener.getClass().getAnnotation(a);
		}
	}
	
	public Collection<ApplicationListener<?>> getAllApplicationListeners() {
		return super.getApplicationListeners();
	}
	
	public void addItemTask(Object listener) {
		TaskExecutor itemExecutor = this.getAnnotationFromAwaredListener((ApplicationListener<?>)listener, 
				TaskExecutor.class);
		if(itemExecutor != null) {
			Executor executor = threadPool(itemExecutor);
			beanFactory.registerSingleton(executorDefaultPreFixName+listener.getClass().getSimpleName(), 
					executor);
		}
		this.addApplicationListener((ApplicationListener<?>) listener);
	}
	
	private Executor threadPool(TaskExecutor annotation) {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(annotation.keepAliveSeconds());
        pool.setCorePoolSize(annotation.corePoolSize());//核心线程池数
        pool.setMaxPoolSize(annotation.maxPoolSize()); // 最大线程
        pool.setQueueCapacity(annotation.queueCapacity());//队列容量
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //队列满，线程被拒绝执行策略
        pool.setThreadNamePrefix(annotation.threadNamePrefix());
        pool.initialize();
        return pool;
	}

	private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
		Item item = (Item)event.getSource();
		try {
			return ResolvableType.forClass(Class.forName(item.getConsumerKey()));
		} catch (Exception e) {
			for(ApplicationListener<?> applicationListener : super.getApplicationListeners()) {
				Task itemJob = applicationListener.getClass().getAnnotation(Task.class);
				if(itemJob != null && itemJob.consumerKey().equals(item.getConsumerKey())) {
					return ResolvableType.forClass(applicationListener.getClass());
				}
			}
		}
		return ResolvableType.forInstance(event);
	}
}
