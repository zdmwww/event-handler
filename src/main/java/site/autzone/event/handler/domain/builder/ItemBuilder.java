package site.autzone.event.handler.domain.builder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import net.xdevelop.snowflake.SnowflakeUidGenerator;
import site.autzone.configurer.AbstractConfiguredBuilder;
import site.autzone.configurer.Configurer;
import site.autzone.event.handler.domain.Attribute;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.task.listener.TaskStatus;

public class ItemBuilder extends AbstractConfiguredBuilder<Item> {
  private SnowflakeUidGenerator snowflakeUidGenerator;
  private Set<Attribute> attributes = new LinkedHashSet<Attribute>();
  private String batchId = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
  private String consumerKey;
  private Date createTime = new Date();
  private String creator;
  private String desc;
  private String detail;
  private String finishMessage;
  private String itemSource = "no_source";
  private Date modifyTime = new Date();
  private String name = "no_name";
  private String routeKey;
  private String status = TaskStatus.Created.getName();
  private int version;
  private Map<String, Object> mapFields = new HashMap<String, Object>();
  
  public ItemBuilder(SnowflakeUidGenerator snowflakeUidGenerator) {
    this.snowflakeUidGenerator = snowflakeUidGenerator;
  }

  public ItemBuilder map(String k, Object v) {
    mapFields.put(k, v);
    return this;
  }
  
  public ItemBuilder batchId(String batchId) {
    this.batchId = batchId;
    return this;
  }
  
  public ItemBuilder consumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
    return this;
  }
  
  public ItemBuilder createTime(Date createTime) {
    this.createTime = createTime;
    return this;
  }
  
  public ItemBuilder creator(String creator) {
    this.creator = creator;
    return this;
  }
  
  public ItemBuilder desc(String desc) {
    this.desc = desc;
    return this;
  }
  
  public ItemBuilder detail(String detail) {
    this.detail = detail;
    return this;
  }
  
  public ItemBuilder finishMessage(String finishMessage) {
    this.finishMessage = finishMessage;
    return this;
  }
  
  public ItemBuilder itemSource(String itemSource) {
    this.itemSource = itemSource;
    return this;
  }
  
  public ItemBuilder modifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
    return this;
  }
  
  public ItemBuilder name(String name) {
    this.name = name;
    return this;
  }
  
  public ItemBuilder routeKey(String routeKey) {
    this.routeKey = routeKey;
    return this;
  }
  
  public ItemBuilder status(String status) {
    this.status = status;
    return this;
  }
  
  public ItemBuilder version(int version) {
    this.version = version;
    return this;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public AttributeConfigurer attribute() {
    Configurer attributeConfigurer = new AttributeConfigurer();
    attributeConfigurer.init(this);
    this.add(attributeConfigurer);
    return (AttributeConfigurer) attributeConfigurer;
  }

  public Set<Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Set<Attribute> attributes) {
    this.attributes = attributes;
  }

  @Override
  protected Item performBuild() {
    System.out.println(this); 
    Item item = new Item();
    long id = this.snowflakeUidGenerator.getUID();
    if(!this.mapFields.isEmpty()) {
      BeanUtils.copyProperties(this.mapFields, item);
    }
    item.setAttributes(attributes);
    item.setBatchId(this.batchId);
    item.setConsumerKey(this.consumerKey);
    item.setCreateTime(createTime);
    item.setCreator(creator);
    item.setDesc(desc);
    item.setDetail(detail);
    item.setFinishMessage(finishMessage);
    item.setItemSource(itemSource);
    item.setModifyTime(modifyTime);
    item.setName(name);
    item.setRouteKey(routeKey);
    item.setStatus(status);
    item.setVersion(version);
    item.setId(id);
    attributes.forEach(attr -> {
      attr.setItem(item);
      attr.setId(this.snowflakeUidGenerator.getUID());
    });
    return item;
  }
}
