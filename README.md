# event handler
可以灵活配置的通过拉取方式的数据库队列。

## 如何开始
### 1. 加入依赖
pom.xml中增加以下依赖：
```java
<dependency>
	<groupId>site.autzone</groupId>
	<artifactId>event-handler</artifactId>
	<version>1.0.3</version>
</dependency>
```

### 2. 配置异常处理数据源
配置文件中增加异常处理数据源的配置信息：
```properties
autzone: 
  datasource:
    jdbc-url: ${EXCEPTION_DB_URL:jdbc:mysql://10.15.2.48:3306/smart_reminder?useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Shanghai}
    username: ${EXCEPTION_DB_USERNAME:root}
    password: ${EXCEPTION_DB_PASSWORD:mysql123}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 增加扫描包路径
```java
site.autzone.event.handler
```

### 4. 发布一个任务示例代码

```java
@Autowired ItemCrudRepository itemCrudRepository;

//在需要异常重试的代码创建Item,持久化到数据库中
itemCrudRepository.save(//持久化数据
    new ItemBuilder()
        .name("名称")//名称
        .desc("这是描述信息")//描述
        .consumerKey("hello")//消费者的key
        .itemSource("test")//处理的来源
        .attribute()//需要写入的参数
        .attr("param1", "010")//示例参数1
        .attr("param2", "参数2")//示例参数21
        .attr("message", "参数2")//示例参数21
        .end()
        .build());

```

### 5. 任务处理器
```java
package site.autzone.event.handler.task.listener;

import java.util.Optional;

import org.springframework.stereotype.Component;
import site.autzone.event.handler.domain.Attribute;
import site.autzone.event.handler.task.Task;
import site.autzone.event.handler.task.TaskArg;
import site.autzone.event.handler.task.TaskConsumer;
import site.autzone.event.handler.task.TaskExecutor;
import site.autzone.event.handler.task.discovery.TaskEvent;

@Task(consumerKey="HELLO_WORLD", 
description="示例任务",
created="2017-08-12 15:58:00")
@TaskArg(required=true, 
argCode="message", 
description="示例参数", sampleValues={"lol"})
@TaskConsumer(interval=3000, batchSize=1000, workNum=200)
@TaskExecutor(corePoolSize=2,maxPoolSize=10,queueCapacity=100,keepAliveSeconds=300)
@Component
public class HelloTask extends AbstractEventListener {
	@Override
	public void onApplicationEvent(TaskEvent event) {
		Optional<Attribute> attrOptional = event.getItemArg("message");
		if(attrOptional.isPresent()) {
			eventlog(event.getItem(), attrOptional.get().getValue());
		}else {
			eventlog(event.getItem());
		}
	}
}
```
