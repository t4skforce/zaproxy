package org.zaproxy.zap.db.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordParam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "PARAM")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "param")
public class ParamModel implements Serializable {

	private static final long serialVersionUID = -2388241933088708037L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PARAMID")
	private Long id;

	@NonNull
	@Column(name = "SITE", nullable = false)
	private String site;

	@NonNull
	@Column(name = "TYPE", nullable = false)
	private String type;

	@NonNull
	@Column(name = "NAME", nullable = false)
	private String name;

	@NonNull
	@Column(name = "USED", nullable = false)
	private Integer used;

	@NonNull
	@Column(name = "FLAGS", nullable = false)
	private String flags;

	@NonNull
	@Column(name = "VALS", nullable = false)
	private String values;

	@Version
	@Column(name = "VERSION")
	private long version;

	/**
	 * Legacy support for zapproxy models
	 *
	 * @deprecated (2.10.1) Replaced by {@link org.zaproxy.zap.db.model.ParamModel}
	 */
	@Deprecated
	public RecordParam toRecord() {
		return new RecordParam(getId(), getSite(), getType(), getName(), getUsed(), getFlags(), getValues());
	}

}
