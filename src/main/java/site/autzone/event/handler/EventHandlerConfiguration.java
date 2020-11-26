package site.autzone.event.handler;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import net.xdevelop.snowflake.SnowflakeUidGenerator;
import site.autzone.event.handler.domain.builder.ItemBuilder;

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
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public ItemBuilder itemBuilder() {
      return new ItemBuilder(customerUidGenerator());
  }
}
