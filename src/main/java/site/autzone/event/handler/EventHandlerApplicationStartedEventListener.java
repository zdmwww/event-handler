package site.autzone.event.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EventHandlerApplicationStartedEventListener
    implements ApplicationListener<ApplicationStartedEvent> {
  @Autowired EventHandlerInitialization eventHandlerInitialization;

  @Override
  public void onApplicationEvent(ApplicationStartedEvent event) {
    eventHandlerInitialization.initLoad();
  }
}
