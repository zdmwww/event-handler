package site.autzone.event.handler.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "SMART_ITEM")
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "itemquence", sequenceName = "SMART_ATTRIBUTE_SEQ", allocationSize = 1)
@OptimisticLocking
public class Item implements Serializable {
	private static final long serialVersionUID = -4318353461544635745L;
	@Id
	@Column(name = "ID_", unique = true)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemquence")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private Integer version;
	@Column(name = "NAME_", length = 255)
	private String name;
	@Column(name = "BATCH_ID_", length = 255)
	private String batchId;
	@Column(name = "CREATOR_", length = 255)
	private String creator;
	@Column(name = "ROUTE_KEY_", length = 255)
	private String routeKey;
	@Column(name = "CONSUMER_KEY_", length = 255)
	private String consumerKey;
	@Column(name = "DESC_", length = 2000)
	private String desc;
	@Column(name = "DETAIL_", length = 4000)
	private String detail;
	@CreatedDate
	@Column(name = "CREATE_TIME_")
	private Date createTime;
	@LastModifiedDate
	@Column(name = "MODIFY_TIME_")
	private Date modifyTime;
	@Column(name = "ITEM_SOURCE_", length = 255)
	private String itemSource;
	@Column(name = "STATUS_", length = 255)
	private String status;
	@Column(name = "FINISH_MESSAGE_", length = 2000)
	private String finishMessage;
	@OneToMany(mappedBy="item",cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<Attribute> attributes;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFinishMessage() {
		return finishMessage;
	}
	public void setFinishMessage(String finishMessage) {
		this.finishMessage = finishMessage;
	}
	public Set<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}
	public void addAttributes(Attribute attribute) {
	  if(this.attributes == null || this.attributes.isEmpty()) {
	    this.attributes = new HashSet<Attribute>();
	  }
	  this.attributes.add(attribute);
	}
	public Optional<Attribute> getAttribute(String key) {
		if(this.attributes != null && !this.attributes.isEmpty()) {
			Optional<Attribute> attribute = this.attributes.stream()
					.filter(attr -> key.equals(attr.getKey())).findFirst();
			if(attribute.isPresent()) {
				return attribute;
			}
		}
		if(this.getAttributes() == null || 
				this.getAttributes().isEmpty()) {
			Optional<Attribute> attribute = Optional.empty();
			return attribute;
		}
		return this.getAttributes().stream()
				.filter(attr -> key.equals(attr.getKey()))
				.findFirst();
	}
}
