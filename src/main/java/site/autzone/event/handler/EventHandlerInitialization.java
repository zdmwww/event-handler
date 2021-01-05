package site.autzone.event.handler;

import java.io.IOException;
import java.util.Map;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.util.IOUtils;
import site.autzone.event.handler.cfg.EventTaskProperties;
import site.autzone.event.handler.cfg.EventTasksProperties;
import site.autzone.event.handler.cfg.Register;
import site.autzone.event.handler.item.ItemRepository;
import site.autzone.event.handler.task.Cast;
import site.autzone.event.handler.task.EventLoop;
import site.autzone.event.handler.task.Fetcher;
import site.autzone.event.handler.task.annotation.Task;
import site.autzone.event.handler.task.event.TaskEventMulticaster;
import site.autzone.sqlbee.SqlRunner;

import javax.sql.DataSource;

@Service
public class EventHandlerInitialization {
  Logger log = LoggerFactory.getLogger(EventHandlerInitialization.class);
  @Autowired private TaskEventMulticaster taskEventMulticaster;
  @Autowired Scheduler scheduler;
  @Qualifier("event-datasource")
  @Autowired
  DataSource dataSource;
  @Autowired SqlRunner sqlRunner;
  @Autowired ItemRepository itemRepository;
  @Autowired EventTasksProperties eventTasksProperties;

  @Value("${autzone.init-database.open: false}")
  private boolean initDatabase;

  @Value("${autzone.init-database.partition: 10}")
  private int initPartition;

  @Value("${autzone.init-database.init-sql-path: db/init-mysql.sql}")
  private String initSqlPath;

  @Value("${autzone.partition}")
  private int partition;

  @Autowired Register register;
  @Autowired ConfigurableBeanFactory beanFactory;

  @Autowired private ApplicationContext applicationContext;

  public void reload() {
    long start = System.currentTimeMillis();
    log.info("reloading...");
    unload(false);
    refreshLoad();
    long end = System.currentTimeMillis();
    log.info("reloaded. cost:{}ms", end - start);
  }
  
  public void unload(boolean clearRegister) {
    long start = System.currentTimeMillis();
    log.info("start unload...");
    if(this.register != null) {
      this.register.getRegisterTaskProperties().values().forEach(regTask -> {
        if(regTask.getEnable()) {
          regTask.getRunningLoops().forEach(loop -> {
            log.info("stop loop.key:{},name:{},loop hashCode:{}", regTask.getConsumerKey(), regTask.getName(), loop.hashCode());
            loop.stopLoop();
          });
          log.info("remove event handler.");
          taskEventMulticaster.removeTaskHandler(regTask.getConsumerKey());
        }
      });
      if(clearRegister) {
        log.info("clear register.");
        this.register.clear();
      }
    }
    log.info("clear listeners.");
    taskEventMulticaster.removeAllListeners();
    long end = System.currentTimeMillis();
    log.info("unloaded. cost:{}ms", end - start);
  }
  
  public void refreshLoad() {
    long start = System.currentTimeMillis();
    log.info("refresh loading...");

    if(!this.register.getRegisterTaskProperties().isEmpty()) {
      Map<String, Object> workers = applicationContext.getBeansWithAnnotation(Task.class);
      workers.forEach(
          (key, eventTask) -> {
            Task eventTaskAnno = getEventTaskAnno(eventTask);
            if (eventTaskAnno == null) {
              throw new RuntimeException("not found @EventTaskAnno annotation.");
            }
            if(this.register.getRegisterTaskProperties().containsKey(eventTaskAnno.consumerKey()) && this.register
                .getRegisterTaskProperties()
                .get(eventTaskAnno.consumerKey())
                .getEnable() != null && this.register
                .getRegisterTaskProperties()
                .get(eventTaskAnno.consumerKey())
                .getEnable() && eventTask instanceof ApplicationListener) {
              // 注册事件处理器
              taskEventMulticaster.addTaskHandler(eventTaskAnno, eventTask);
              // 启动loop
              startLoop(this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()),
                  eventTask);
            }
          });
    }
    long end = System.currentTimeMillis();
    log.info("refresh loaded. cost:{}ms", end - start);
  
  }

  public void initLoad() {

    long start = System.currentTimeMillis();
    log.info("start loading...");
    // 初始化数据
    initDatabase();

    // 校验分区表是否存在
    checkPartition();

    // 注册任务
    registerTasks();

    Map<String, Object> workers = applicationContext.getBeansWithAnnotation(Task.class);
    workers.forEach(
        (key, eventTask) -> {
          Task eventTaskAnno = getEventTaskAnno(eventTask);
          if (eventTaskAnno == null) {
            throw new RuntimeException("not found @EventTaskAnno annotation.");
          }

          registerTaskWithAnno(eventTaskAnno);

          if (this.register
              .getRegisterTaskProperties()
              .get(eventTaskAnno.consumerKey())
              .getEnable()) {
            if (eventTask instanceof ApplicationListener) {
              // 注册事件处理器
              taskEventMulticaster.addTaskHandler(eventTaskAnno, eventTask);
              // 启动loop
              startLoop(
                  this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()),
                  eventTask);
            }
          }
        });
    long end = System.currentTimeMillis();
    log.info("loaded. cost:{}ms", end - start);
  }

  private void registerTaskWithAnno(Task eventTaskAnno) {
    if (!this.register.getRegisterTaskProperties().containsKey(eventTaskAnno.consumerKey())) {
      this.register
          .getRegisterTaskProperties()
          .put(eventTaskAnno.consumerKey(), new EventTaskProperties());
      this.register
          .getRegisterTaskProperties()
          .get(eventTaskAnno.consumerKey())
          .setConsumerKey(eventTaskAnno.consumerKey());
    }

    if (this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()).getName()
        == null) {
      this.register
          .getRegisterTaskProperties()
          .get(eventTaskAnno.consumerKey())
          .setName(eventTaskAnno.name());
    }

    if (this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()).getFetchers()
        == null) {
      this.register
          .getRegisterTaskProperties()
          .get(eventTaskAnno.consumerKey())
          .setFetchers(eventTaskAnno.fetchers());
    }

    if (this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()).getSize()
        == null) {
      this.register
          .getRegisterTaskProperties()
          .get(eventTaskAnno.consumerKey())
          .setSize(eventTaskAnno.size());
    }

    if (this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()).getInterval()
        == null) {
      this.register
          .getRegisterTaskProperties()
          .get(eventTaskAnno.consumerKey())
          .setInterval(eventTaskAnno.interval());
    }

    if (this.register.getRegisterTaskProperties().get(eventTaskAnno.consumerKey()).getEnable()
        == null) {
      this.register
          .getRegisterTaskProperties()
          .get(eventTaskAnno.consumerKey())
          .setEnable(eventTaskAnno.enable());
    }
  }

  private void startLoop(EventTaskProperties eventTaskProperties, Object eventTask) {
    if (eventTask instanceof EventLoop) {
      for (int fetcherId = 0; fetcherId < eventTaskProperties.getFetchers(); fetcherId++) {
        try {
          Object loopObject = eventTask.getClass().newInstance();
          EventLoop loop = (EventLoop) loopObject;
          Fetcher fetcher = null;
          String p = eventTaskProperties.getP();
          if (p != null) {
            fetcher =
                new Fetcher(eventTaskProperties, this.eventTasksProperties, this.itemRepository);
          } else {
            fetcher =
                new Fetcher(
                    eventTaskProperties,
                    this.eventTasksProperties,
                    fetcherId,
                    this.partition,
                    this.itemRepository);
          }
          loop.prepareFetch(fetcher);
          loop.prepareCast(
              new Cast(eventTaskProperties, this.eventTasksProperties, taskEventMulticaster));
          loop.startLoop(eventTaskProperties, this.register.getRegisterTaskProperties());
          eventTaskProperties.getRunningLoops().add(loop);
        } catch (InstantiationException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private void registerTasks() {
    this.eventTasksProperties
        .getEventTasks()
        .forEach(
            taskProperties -> {
              this.register
                  .getRegisterTaskProperties()
                  .put(taskProperties.getConsumerKey(), taskProperties);
            });
  }

  private void checkPartition() {
    // check partition num
    for (int i = 0; i < this.partition; i++) {
      sqlRunner.execute(String.format("select 1 from %s limit 1", ("smart_arguments_p" + i)));
      sqlRunner.execute(String.format("select 1 from %s limit 1", ("smart_item_p" + i)));
    }
  }

  private void initDatabase() {
    // 初始化建表
    if (this.initDatabase) {
      log.info("event handler init database, all table be drop and recreate.");
      Resource resource = new ClassPathResource(initSqlPath);
      log.info("init sql file path:{}", initSqlPath);
      if (!resource.exists()) {
        log.error("init database fail. no init resource sql file.");
        throw new RuntimeException("init database fail. no init resource sql file.");
      }
      for (int i = 0; i < 10; i++) {
        log.info("drop table if exists smart_arguments_p{}", i);
        sqlRunner.execute("drop table if exists smart_arguments_p" + i);
        log.info("drop table if exists smart_item_p{}", i);
        sqlRunner.execute("drop table if exists smart_item_p" + i);
        try {
          String initSqlOrigin = IOUtils.toString(resource.getInputStream());
          String initSql =
              initSqlOrigin
                  .replace("smart_item_p", "smart_item_p" + i)
                  .replace("smart_arguments_p", "smart_arguments_p" + i)
                  .replaceAll("[\\t\\n\\r]", "");
          for (String sql : initSql.split(";")) {
            log.info(sql);
            sqlRunner.execute(sql);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      log.info("event handler init database successfull.");
    }
  }

  private Task getEventTaskAnno(Object listener) {
    if (listener instanceof TargetClassAware) {
      TargetClassAware aware = (TargetClassAware) listener;
      return aware.getTargetClass().getAnnotation(Task.class);
    }
    return listener.getClass().getAnnotation(Task.class);
  }
}
