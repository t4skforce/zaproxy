package org.zaproxy.zap.db.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordSessionUrl;
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
@Table(name = "SESSION_URL")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "session_url")
public class SessionUrlModel extends AbstractModel {

    private static final long serialVersionUID = 731686980824317726L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "URLID")
    private Long id;

    @NonNull
    @Column(name = "TYPE", nullable = false)
    private Integer type;

    @Builder.Default
    @Column(name = "URL", length = 8192)
    private String url = StringUtils.EMPTY;

    public RecordSessionUrl toRecord() {
        return new RecordSessionUrl(getId(), getType(), getUrl());
    }

}
