package site.autzone.event.handler.task.event;

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
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.task.annotation.Task;

/** @author xiaowj */
@Service
public class TaskEventMulticaster extends SimpleApplicationEventMulticaster
    implements InitializingBean, ApplicationContextAware {
  private ApplicationContext context;
  private static final String executorDefaultPreFixName = "site.autzone.event.executor.";
  // 默认的线程池
  @Autowired
  @Qualifier("defaultTaskEventExecutor")
  ThreadPoolTaskExecutor defaultTaskEventExcutor;
  // 各个工作节点的线程池注册中心,为每个事件处理器单独设置线程池
  // beanname -> site.autzone.event.executor.{task.consumerKey().toLowerCase().replace("_", "-")}
  @Autowired ConfigurableBeanFactory beanFactory;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
      String executorBeanName = executorDefaultPreFixName + listener.getClass().getSimpleName();
      if (event.getSource() instanceof Item) {
        Task taskAnno = this.getAnnotationFromAwaredListener(listener, Task.class);
        if (taskAnno != null) {
          executorBeanName =
              executorDefaultPreFixName + this.eventTaskGenPrefixName(taskAnno.consumerKey());
        }
      }
      if (context.containsBean(executorBeanName)) {
        Object beanTp = context.getBean(executorBeanName);
        if (beanTp instanceof Executor) {
          executor = (Executor) beanTp;
        }
      }
      if (executor == null) {
        executor = getTaskExecutor();
      }
      if (executor != null) {
        executor.execute(() -> invokeListener(listener, event));
      } else {
        invokeListener(listener, event);
      }
    }
  }

  private List<ApplicationListener<?>> getEventApplicationListeners(
      ApplicationEvent event, ResolvableType type) {
    List<ApplicationListener<?>> allListeners = new ArrayList<>();
    for (ApplicationListener<?> applicationListener : super.getApplicationListeners()) {

      Object object = event.getSource();
      if (object instanceof Item) {
        Item item = (Item) object;
        Task taskAnno = this.getAnnotationFromAwaredListener(applicationListener, Task.class);
        if (taskAnno != null && item.getConsumerKey().equals(taskAnno.consumerKey())) {
          allListeners.add(applicationListener);
        }
      }
    }
    return allListeners;
  }

  public <A extends Annotation> A getAnnotationFromAwaredListener(
      ApplicationListener<?> applicationListener, Class<A> a) {
    if (applicationListener instanceof TargetClassAware) {
      TargetClassAware aware = (TargetClassAware) applicationListener;
      return aware.getTargetClass().getAnnotation(a);
    } else {
      return applicationListener.getClass().getAnnotation(a);
    }
  }

  public Collection<ApplicationListener<?>> getAllApplicationListeners() {
    return super.getApplicationListeners();
  }

  public void addTaskHandler(Task fetcher, Object listener) {
    if (fetcher != null) {
      String preFixName = eventTaskGenPrefixName(fetcher.consumerKey());
      Executor executor = threadPool(preFixName, fetcher);
      beanFactory.registerSingleton(executorDefaultPreFixName + preFixName, executor);
    }
    this.addApplicationListener((ApplicationListener<?>) listener);
  }

  public void removeTaskHandler(String consumerKey) {
    DefaultSingletonBeanRegistry reg = (DefaultSingletonBeanRegistry) beanFactory;
    String beanName = executorDefaultPreFixName + eventTaskGenPrefixName(consumerKey);
    if (beanFactory.containsBean(beanName)) {
      reg.destroySingleton(beanName);
    }
  }

  public String eventTaskGenPrefixName(String consumerKey) {
    return consumerKey.toLowerCase().replace("_", "-");
  }

  private Executor threadPool(String threadNamePrefix, Task task) {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setKeepAliveSeconds(task.interval() / 1000 * 3); // 线程空闲最大存活时间，设置为扫描时间的3倍，三次扫描未发现新任务则销毁执行线程
    int halfSize = task.size() / 2;
    pool.setCorePoolSize(
        halfSize > 50 ? 50 : halfSize < 1 ? 1 : halfSize); // 核心线程池数,每次拉取任务数量的一半作为核心线程数，但是1<=x<=50
    int doubledSize = task.size() * 2;
    pool.setMaxPoolSize(
        doubledSize > 1000
            ? 1000
            : doubledSize < 1 ? 1 : doubledSize); // 最大线程数 = 每次任务拉取的批次数目，但是1<=x<=1000
    pool.setQueueCapacity(doubledSize * 5); // 阻塞任务队列 = 每次任务拉取的批次数目 x 10
    pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 队列满，线程被拒绝执行策略
    pool.setThreadNamePrefix(threadNamePrefix + ".");
    pool.initialize();
    return pool;
  }

  private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
    Item item = (Item) event.getSource();
    try {
      return ResolvableType.forClass(Class.forName(item.getConsumerKey()));
    } catch (Exception e) {
      for (ApplicationListener<?> applicationListener : super.getApplicationListeners()) {
        Task itemJob = applicationListener.getClass().getAnnotation(Task.class);
        if (itemJob != null && itemJob.consumerKey().equals(item.getConsumerKey())) {
          return ResolvableType.forClass(applicationListener.getClass());
        }
      }
    }
    return ResolvableType.forInstance(event);
  }
}
