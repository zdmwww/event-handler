package site.autzone.event.handler.item;

import java.util.Date;

public class Item {

  private Long id;

  private Integer version;

  private String name;

  private String batchId;

  private String creator;

  private String routeKey;

  private String consumerKey;

  private String desc;

  private String detail;

  private Date createTime;

  private Date modifyTime;

  private String itemSource;

  private int status;

  private String finishMessage;

  private Argument attribute;

  private String partition;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBatchId() {
    return batchId;
  }

  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
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

  public Argument getAttribute() {
    return attribute;
  }

  public void setAttribute(Argument attribute) {
    this.attribute = attribute;
  }

  public String getPartition() {
    return partition;
  }

  public void setPartition(String partition) {
    this.partition = partition;
  }
}
