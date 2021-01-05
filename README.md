# 事件处理器(event-handler)
易于扩展的事件处理框架.

## 如何开始
### 1. 加入依赖
pom.xml中增加以下依赖：
```java
<dependency>
	<groupId>site.autzone</groupId>
	<artifactId>event-handler</artifactId>
	<version>1.1.0</version>
</dependency>
```

### 2. 配置数据源
配置文件中增加数据源的配置信息：
```properties
autzone:
  partition: 1 #配置item的分表数目
  init-database: #配置初始化数据库
    open: false  #是否初始化数据库
    partition: 10 #初始化分区数目
    init-sql-path: db/init-mysql.sql #初始化sql语句
  #item数据源配置druid
  datasource:
    url: ${DB_URL:jdbc:mysql://xxx.xxx.xxx.xxx:3306/smart_reminder?useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Shanghai}
    username: ${DB_USERNAME:username}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    druid.initial-size: 10
    druid.max-active: 500 #最大数据库连接数
```
数据库连接池使用的druid,可以根据druid配置调整连接池相关信息。
### 3. 配置日志打印
```
    <logger name="site.autzone" level="info" additivity="false">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
        ...
    </logger>
```
### 3. 增加扫描包路径
```java
site.autzone
```

### 4. 发布一个任务示例代码

```java
  @Autowired ItemBuilder itemBuiler;

//持久化Item到数据库中
Item item = itemBuiler
        .name("名称")//名称
        .desc("这是描述信息")//描述
        .consumerKey("hello_world")//消费者的key
        .itemSource("test")//处理的来源
        .attribute()//需要写入的参数
        .attr("param1", "010")//示例参数1
        .attr("param2", "参数2")//示例参数21
        .attr("message", "参数2")//示例参数21
        .end()
        .save()

```

### 5. 任务处理器
```java
import java.util.Optional;
import site.autzone.event.handler.item.rest.dto.AttributeDto;
import site.autzone.event.handler.task.AbstractJobEventTask;
import site.autzone.event.handler.task.annotation.Task;
import site.autzone.event.handler.task.annotation.TaskArg;
import site.autzone.event.handler.task.event.TaskEvent;

@Task(consumerKey = "hello_world", name = "示例任务", enable = false)
@TaskArg(required = true, key = "message", desc = "消息", sampleValues = {"lol"})
@TaskArg(required = true, key = "whoami", desc = "我是谁", sampleValues = {"codeman"})
public class HelloWorld extends AbstractJobEventTask {
  @Override
  public void onApplicationEvent(TaskEvent event) {
    Optional<AttributeDto> attrOptional = event.getItemArg("message");
    if (attrOptional.isPresent()) {
      eventlog(event.getItem(), event.getString("whoami") + "->" + attrOptional.get().getValue());
    } else {
      eventlog(event.getItem());
    }
  }
}
```
### 6. 任务配置
```yml
tasks: 
  event-tasks:
     -
       consumer-key: hello_world  #consumer的key
       name: 示例任务
       fetchers: 1  #pull任务的线程数
       size: 1000  #每次拉取任务的数量
       interval: 3000  #拉取任务的间隔时间
       enable: true  #是否启用
       args:  #配置的参数
         -
           key: whoami
           value: codeman
         -
           key: message
           value: lol
     -
       consumer-key: scriptTask1  #task配置
       name: 脚本任务
       fetchers: 1
       size: 1000
       interval: 4000
       enable: true  #是否启用
       args:
         -
           key: whoami
           value: codeman
         -
           key: message
           value: lol
     -
       consumer-key: clean  #task配置
       enable: false  #是否可用
```

