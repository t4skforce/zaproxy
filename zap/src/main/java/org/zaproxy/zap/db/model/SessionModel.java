package org.zaproxy.zap.db.model;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.UpdateTimestamp;
import org.parosproxy.paros.db.RecordSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "SESSION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "session")
public class SessionModel implements Serializable {

	private static final long serialVersionUID = 1669779732110944371L;

	@Id
	@Column(name = "SESSIONID")
	private Long id;

	@Column(name = "SESSIONNAME")
	private String name;

	@Builder.Default
	@UpdateTimestamp
	@Column(name = "LASTACCESS", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccess = new Date();

	@Version
	@Column(name = "VERSION")
	private long version;

	/**
	 * Legacy support for zapproxy models
	 *
	 * @deprecated (2.10.1) Replaced by
	 *             {@link org.zaproxy.zap.db.model.SessionModel}
	 */
	@Deprecated
	public RecordSession toRecord() {
		return new RecordSession(getId(), getName(),
				java.sql.Date.valueOf(getLastAccess().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
	}

}
