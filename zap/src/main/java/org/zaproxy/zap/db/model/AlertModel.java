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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordAlert;

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
@Table(name = "ALERT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "alert")
public class AlertModel implements Serializable {

	private static final long serialVersionUID = -3248909395859220382L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERTID")
	private Long id;

	@NonNull
	@Column(name = "SCANID", nullable = false)
	private Long scanId;

	@Builder.Default
	@Column(name = "PLUGINID")
	private Integer pluginId = 0;

	@Column(name = "ALERT")
	private String name;

	@Builder.Default
	@Column(name = "RISK")
	private Integer risk = 0;

	@Builder.Default
	@Column(name = "RELIABILITY")
	private Integer confidence = 1;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "URI")
	private String uri;

	@Column(name = "PARAM")
	private String param;

	@Column(name = "OTHERINFO")
	private String otherInfo;

	@Column(name = "SOLUTION")
	private String solution;

	@Column(name = "REFERENCE")
	private String reference;

	@Column(name = "HISTORYID")
	private Long historyId;

	@Column(name = "SOURCEHISTORYID")
	private Long sourceHistoryId;

	@Column(name = "ATTACK")
	private String attack;

	@Column(name = "EVIDENCE")
	private String evidence;

	@Builder.Default
	@Column(name = "CWEID")
	private int cweId = -1;

	@Builder.Default
	@Column(name = "WASCID")
	private int wascId = -1;

	@Builder.Default
	@Column(name = "SOURCEID")
	private int sourceId = -1;

	@Builder.Default
	@Column(name = "ALERTREF", length = 256)
	private String alertRef = StringUtils.EMPTY;

	@Version
	@Column(name = "VERSION")
	private long version;

	/**
	 * Legacy support for zapproxy models
	 *
	 * @deprecated (2.10.1) Replaced by {@link org.zaproxy.zap.db.model.AlertModel}
	 */
	@Deprecated
	public RecordAlert toRecord() {
		return new RecordAlert(getId().intValue(), getScanId().intValue(), getPluginId(), getName(), getRisk(),
				getConfidence(), getDescription(), getUri(), getParam(), getAttack(), getOtherInfo(), getSolution(),
				getReference(), getEvidence(), getCweId(), getWascId(), getHistoryId().intValue(),
				getSourceHistoryId().intValue(), getSourceId(), getAlertRef());
	}

}
