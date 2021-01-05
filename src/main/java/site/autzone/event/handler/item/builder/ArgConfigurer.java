package site.autzone.event.handler.item.builder;

import java.util.ArrayList;
import java.util.List;
import site.autzone.configurer.AbstractConfigurer;

public class ArgConfigurer extends AbstractConfigurer<ItemBuilder> {
  private List<Arg> args = new ArrayList<>();

  public ArgConfigurer() {}

  public ArgConfigurer(ItemBuilder parent) {
    init(parent);
  }

  public ArgConfigurer attr(String key, String value) {
    Arg arg = new Arg();
    arg.setKey(key);
    arg.setValue(value);
    args.add(arg);
    return this;
  }

  @Override
  public void configure(ItemBuilder parent) {
    parent.getAttributes().addAll(args);
  }
}
