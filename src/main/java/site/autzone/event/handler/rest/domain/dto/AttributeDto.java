package site.autzone.event.handler.rest.domain.dto;

import site.autzone.event.handler.domain.Attribute;

public class AttributeDto {
	private Long id;
	private String key;
	private String value;
	public AttributeDto() {
	}
	public AttributeDto(Attribute attribute) {
		this.id = attribute.getId();
		this.key = attribute.getKey();
		this.value = attribute.getValue();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
