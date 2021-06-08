package org.zaproxy.zap.db.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordContext;
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
@Table(name = "CONTEXT_DATA")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "context_data")
public class ContextModel extends AbstractModel {

    private static final long serialVersionUID = 1623735529779770521L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATAID")
    private Long id;

    @NonNull
    @Column(name = "CONTEXTID", nullable = false)
    private Long contextId;

    @NonNull
    @Column(name = "TYPE", nullable = false)
    private Integer type;

    @Column(name = "DATA")
    private String data;

    /**
     * Legacy support for zapproxy models
     *
     * @deprecated (2.10.1) Replaced by
     *             {@link org.zaproxy.zap.db.model.ContextModel}
     */
    @Deprecated
    public RecordContext toRecord() {
        return new RecordContext(getId(), getContextId().intValue(), getType(), getData());
    }
}
