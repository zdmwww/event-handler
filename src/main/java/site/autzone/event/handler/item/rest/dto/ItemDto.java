package site.autzone.event.handler.item.rest.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSONArray;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.builder.Arg;

public class ItemDto {
  private Long id;
  private String name;
  private String batchId;
  private String routeKey;
  private String consumerKey;
  private String creator;
  private String desc;
  private String detail;
  private Date createTime;
  private Date modifyTime;
  private String itemSource;
  private int status;
  private String finishMessage;
  private List<AttributeDto> attributes = new ArrayList<>();

  public ItemDto() {}

  public ItemDto(Item item) {
    this.id = item.getId();
    this.name = item.getName();
    this.batchId = item.getBatchId();
    this.consumerKey = item.getConsumerKey();
    this.creator = item.getCreator();
    this.createTime = item.getCreateTime();
    this.desc = item.getDesc();
    this.detail = item.getDetail();
    this.finishMessage = item.getFinishMessage();
    this.itemSource = item.getItemSource();
    this.modifyTime = item.getModifyTime();
    this.status = item.getStatus();
    JSONArray.parseArray(item.getAttribute().getValue(), Arg.class)
        .stream()
        .forEach(
            arg -> {
              this.attributes.add(new AttributeDto(arg));
            });
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getBatchId() {
    return batchId;
  }

  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  public String getRouteKey() {
    return routeKey;
  }

  public void setRouteKey(String routeKey) {
    this.routeKey = routeKey;
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getItemSource() {
    return itemSource;
  }

  public void setItemSource(String itemSource) {
    this.itemSource = itemSource;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getFinishMessage() {
    return finishMessage;
  }

  public void setFinishMessage(String finishMessage) {
    this.finishMessage = finishMessage;
  }

  public List<AttributeDto> getAttributes() {
    return this.attributes;
  }

  public void setAttributes(List<AttributeDto> attributes) {
    this.attributes = attributes;
  }
}
