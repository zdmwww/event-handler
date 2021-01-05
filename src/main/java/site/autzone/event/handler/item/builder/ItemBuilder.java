package site.autzone.event.handler.item.builder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import com.alibaba.fastjson.JSONArray;
import net.xdevelop.snowflake.SnowflakeUidGenerator;
import site.autzone.configurer.AbstractConfigurerBuilder;
import site.autzone.configurer.Configurer;
import site.autzone.event.handler.item.Argument;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.rest.dto.ItemDto;
import site.autzone.event.handler.task.TaskStatus;

public class ItemBuilder extends AbstractConfigurerBuilder<Item> {
  private static final SnowflakeUidGenerator snowflakeUidGenerator = new SnowflakeUidGenerator(SnowflakeUidGenerator.getWorkerIdByIP(24));
  private Set<Arg> args = new LinkedHashSet<Arg>();
  private String batchId =
      LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
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
  private int status = TaskStatus.Created.getCode();
  private int version;
  private Long id;
  private Map<String, Object> mapFields = new HashMap<String, Object>();

  public ItemBuilder() {}

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
  
  public ItemBuilder item(ItemDto itemDto) {
    this.id = itemDto.getId();
    this.name = itemDto.getName();
    this.batchId = itemDto.getBatchId();
    this.routeKey = itemDto.getRouteKey();
    this.consumerKey = itemDto.getConsumerKey();
    this.creator = itemDto.getCreator();
    this.desc = itemDto.getDesc();
    this.detail = itemDto.getDetail();
    this.createTime = itemDto.getCreateTime();
    this.modifyTime = itemDto.getModifyTime();
    this.itemSource = itemDto.getItemSource();
    this.status = itemDto.getStatus();
    this.finishMessage = itemDto.getFinishMessage();
    itemDto.getAttributes().forEach(arg -> {
      this.args.add(new Arg(arg.getKey(), arg.getValue()));
    });
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

  public ItemBuilder status(int status) {
    this.status = status;
    return this;
  }

  public ItemBuilder status(TaskStatus status) {
    this.status = status.getCode();
    return this;
  }

  public ItemBuilder version(int version) {
    this.version = version;
    return this;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public ArgConfigurer attribute() {
    Configurer attributeConfigurer = new ArgConfigurer();
    attributeConfigurer.init(this);
    this.add(attributeConfigurer);
    return (ArgConfigurer) attributeConfigurer;
  }

  public Set<Arg> getAttributes() {
    return args;
  }

  public void setAttributes(Set<Arg> args) {
    this.args = args;
  }

  @Override
  protected Item performBuild() {
    Item item = new Item();
    if(this.id == null) {
      this.id = snowflakeUidGenerator.getUID();
    }
    if (!this.mapFields.isEmpty()) {
      BeanUtils.copyProperties(this.mapFields, item);
    }
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
    if (!args.isEmpty()) {
      Argument attr = new Argument();
      attr.setItemId(item.getId());
      attr.setValue(JSONArray.toJSONString(args));
      item.setAttribute(attr);
    }
    return item;
  }
}
