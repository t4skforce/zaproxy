package org.zaproxy.zap.db.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordScan;
import org.zaproxy.zap.db.model.base.AbstractModel;

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
@Table(name = "SCAN")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "scan")
public class ScanModel extends AbstractModel {

    private static final long serialVersionUID = -8545900633149366456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCANID")
    private Long id;

    @NonNull
    @Column(name = "SESSIONID", nullable = false)
    private Long sessionId;

    @Column(name = "SCANNAME")
    private String name;

    @NonNull
    @Column(name = "SCANTIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    public RecordScan toRecord() {
        return new RecordScan(getId().intValue(), getName(), java.sql.Date.valueOf(getTime().toLocaleString()));
    }

}
