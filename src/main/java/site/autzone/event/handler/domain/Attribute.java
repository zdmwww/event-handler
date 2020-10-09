package site.autzone.event.handler.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "SMART_ATTRIBUTE")
@SequenceGenerator(name = "attributequence", sequenceName = "SMART_ATTRIBUTE_SEQ", allocationSize = 1)
public class Attribute implements Serializable {
	private static final long serialVersionUID = 869837849622324976L;

	@Id
	@Column(name = "ID_")
	//@GeneratedValue(strategy=GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attributequence")
	private Long id;

	@Column(name = "KEY_", length = 255)
	private String key;

	@Column(name = "VALUE_", length = 65535, columnDefinition="TEXT")
	@Type(type="text")
	private String value;

	@ManyToOne
    @JoinColumn(name="ITEM_ID_", nullable=false)
    private Item item;

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

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
