package org.zaproxy.zap.db.schema;

import java.io.Serializable;
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "scan")
@Table(name = "SCAN")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scan implements Serializable {

    private static final long serialVersionUID = 1L;

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

}
