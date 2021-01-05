package site.autzone.event.handler.cfg;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import groovy.sql.Sql;
import net.xdevelop.snowflake.SnowflakeUidGenerator;
import site.autzone.event.handler.item.ItemRepository;

@Configuration
@ComponentScan("site.autzone.event.handler")
public class EventHandlerConfiguration {
  @Bean
  @ConditionalOnMissingBean(SnowflakeUidGenerator.class)
  public SnowflakeUidGenerator customerUidGenerator() {
    long workerId = SnowflakeUidGenerator.getWorkerIdByIP(24);
    return new SnowflakeUidGenerator(workerId);
  }
  
  @Bean
  public ItemRepository itemRepository() {
    return new ItemRepository();
  }
  
  @Bean("groovySql")
  public Sql groovySql(@Autowired DataSource dataSource) {
      return new Sql(dataSource);
  }
}
