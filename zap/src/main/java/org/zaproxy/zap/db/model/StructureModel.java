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
import org.parosproxy.paros.db.RecordStructure;

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
@Table(name = "STRUCTURE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "structure")
public class StructureModel implements Serializable {

	private static final long serialVersionUID = 9194128916336901977L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STRUCTUREID")
	private Long id;

	@NonNull
	@Column(name = "SESSIONID", nullable = false)
	private Long sessionId;

	@NonNull
	@Column(name = "PARENTID", nullable = false)
	private Long parentId;

	@NonNull
	@Column(name = "HISTORYID", nullable = false)
	private Long historyId;

	@NonNull
	@Column(name = "NAME", nullable = false)
	private String name;

	@NonNull
	@Column(name = "NAMEHASH", nullable = false)
	private Long nameHash;

	@NonNull
	@Column(name = "URL", nullable = false)
	private String url;

	@NonNull
	@Column(name = "METHOD", nullable = false, length = 10)
	private String method;

	@Version
	@Column(name = "VERSION")
	private long version;

	/**
	 * Legacy support for zapproxy models
	 *
	 * @deprecated (2.10.1) Replaced by
	 *             {@link org.zaproxy.zap.db.model.StructureModel}
	 */
	@Deprecated
	public RecordStructure toRecord() {
		return new RecordStructure(getSessionId(), getId(), getParentId(), getHistoryId().intValue(), getName(),
				getUrl(), getMethod());
	}

}
