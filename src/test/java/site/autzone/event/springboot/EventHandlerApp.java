package site.autzone.event.springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/** Spring Boot启动器 */
@SpringBootApplication
@ComponentScan({"site.autzone"})
public class EventHandlerApp {

  public static void main(String[] args) {
    new SpringApplicationBuilder(EventHandlerApp.class).run(args);
  }
}
