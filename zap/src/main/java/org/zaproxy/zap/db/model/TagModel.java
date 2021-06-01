package org.zaproxy.zap.db.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordTag;
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
@Table(name = "TAG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "tag")
public class TagModel extends AbstractModel {

    private static final long serialVersionUID = -1862481599875979358L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAGID")
    private Long id;

    @Builder.Default
    @Column(name = "TAG", length = 1024)
    private String tag = StringUtils.EMPTY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HISTORYID", nullable = false)
    private HistoryModel history;

    public RecordTag toRecord() {
        return new RecordTag(getId(), history.getId(), getTag());
    }

}
