package site.autzone.event.handler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Spring Boot启动器 */
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan({"site.autzone.event.handler", "site.autzone.sqlbee"})
public class EventHandlerApp {

  public static void main(String[] args) {
    new SpringApplicationBuilder(EventHandlerApp.class).run(args);
  }
}
