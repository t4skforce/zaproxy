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
import org.parosproxy.paros.db.RecordStructure;
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
@Table(name = "STRUCTURE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "structure")
public class StructureModel extends AbstractModel {

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
    private Integer nameHash;

    @NonNull
    @Column(name = "URL", nullable = false)
    private String url;

    @NonNull
    @Column(name = "METHOD", nullable = false, length = 10)
    private String method;

    public RecordStructure toRecord() {
        return new RecordStructure(getSessionId(), getId(), getParentId(), getHistoryId().intValue(), getName(),
                getUrl(), getMethod());
    }

}
