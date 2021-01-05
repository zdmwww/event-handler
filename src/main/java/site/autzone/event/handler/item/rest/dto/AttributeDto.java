package site.autzone.event.handler.item.rest.dto;

import site.autzone.event.handler.item.builder.Arg;

public class AttributeDto {
  private String key;
  private String value;

  public AttributeDto() {}
  
  public AttributeDto(Arg arg) {
    this.key = arg.getKey();
    this.value = arg.getValue();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
