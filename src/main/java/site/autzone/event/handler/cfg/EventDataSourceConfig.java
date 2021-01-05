package site.autzone.event.handler.cfg;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import site.autzone.sqlbee.SqlRunner;

@Configuration
@EnableTransactionManagement
public class EventDataSourceConfig {

  @Bean("event-datasource")
  @ConfigurationProperties("autzone.datasource")
  public DataSource dataSource(){
      return DruidDataSourceBuilder.create().build();
  }

  @Bean
  public SqlRunner sqlRunner() {
      return new SqlRunner(this.dataSource());
  }
}
