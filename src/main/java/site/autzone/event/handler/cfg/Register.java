package site.autzone.event.handler.cfg;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Register {
  private Map<String, EventTaskProperties> registerTaskProperties =
      new HashMap<String, EventTaskProperties>();

  public Map<String, EventTaskProperties> getRegisterTaskProperties() {
    return registerTaskProperties;
  }

  public void setRegisterTaskProperties(Map<String, EventTaskProperties> registerTaskProperties) {
    this.registerTaskProperties = registerTaskProperties;
  }
  
  public void clear() {
    this.registerTaskProperties.clear();
  }
}
