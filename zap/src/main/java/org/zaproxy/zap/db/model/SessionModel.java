package org.zaproxy.zap.db.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.UpdateTimestamp;
import org.parosproxy.paros.db.RecordSession;
import org.zaproxy.zap.db.model.base.AbstractModel;

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
public class SessionModel extends AbstractModel {

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

    public RecordSession toRecord() {
        return new RecordSession(getId(), getName(), java.sql.Date.valueOf(getLastAccess().toLocaleString()));
    }

}
