package site.autzone.event.handler.domain.builder;

import java.util.ArrayList;
import java.util.List;
import site.autzone.configurer.AbstractConfigurer;
import site.autzone.event.handler.domain.Attribute;

public class AttributeConfigurer extends AbstractConfigurer<ItemBuilder> {  
  private List<Attribute> attributes = new ArrayList<>();
  
  public AttributeConfigurer() {}

  public AttributeConfigurer(ItemBuilder parent) {
    init(parent);
  }

  public AttributeConfigurer attr(String key, String value) {
    Attribute attribute = new Attribute();
    attribute.setKey(key);
    attribute.setValue(value);
    attributes.add(attribute);
    return this;
  }

  @Override
  public void configure(ItemBuilder parent) {
    parent.getAttributes().addAll(attributes);
  }
}
